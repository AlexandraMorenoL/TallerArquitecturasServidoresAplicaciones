package co.edu.escuelaing;

import java.io.*;
import java.lang.reflect.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Servidor HTTP muy sencillo (solo GET) y secuencial (no concurrente).
 * - Carga controladores @RestController (por CLI o escaneo simple de target/classes).
 * - Soporta @GetMapping(path) con retorno String y @RequestParam(String) con defaultValue.
 * - Sirve estáticos desde classpath "public" (HTML/PNG).
 *
 * @author Alexandra Moreno
 */
public class HttpServer {

    /** ====== Arranque ====== */
    public static void main(String[] args) throws Exception {
        Args cli = parseArgs(args);
        List<Object> controllers = (cli.classNames.isEmpty())
                ? loadControllersByScan()
                : loadControllersByNames(cli.classNames);

        if (controllers.isEmpty()) {
            System.err.println("No se encontraron controladores. Pase FQCN por CLI o compile clases con @RestController.");
            System.exit(2);
            return;
        }

        Map<String, Handler> routes = new HashMap<>();
        for (Object c : controllers) registerRoutes(c, routes);
        routes.putIfAbsent("/", new Handler(null, null, (qp) -> "<h1>Servidor listo</h1>"));

        System.out.println("Servidor HTTP en puerto " + cli.port);
        serve(cli.port, routes);
    }

    /** ====== Registro de rutas por reflexión ====== */
    private static void registerRoutes(Object controller, Map<String, Handler> routes) {
        for (Method m : controller.getClass().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(GetMapping.class)) continue;
            if (!m.getReturnType().equals(String.class))
                throw new IllegalStateException("@GetMapping debe retornar String: " + m);
            String path = normalize(m.getAnnotation(GetMapping.class).value());
            if (routes.containsKey(path)) throw new IllegalStateException("Ruta duplicada: " + path);
            m.setAccessible(true);
            routes.put(path, new Handler(controller, m, null));
            System.out.println("GET " + path + " -> " + m);
        }
    }

    /** ====== Servidor HTTP básico (solo GET) ====== */
    private static void serve(int port, Map<String, Handler> routes) throws IOException {
        try (ServerSocket ss = new ServerSocket(port)) {
            while (true) {
                try (Socket s = ss.accept()) {
                    handle(s, routes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void handle(Socket s, Map<String, Handler> routes) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
        OutputStream out = s.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) return;

        String[] parts = requestLine.split(" ");
        String method = parts.length > 0 ? parts[0] : "GET";
        String target = parts.length > 1 ? parts[1] : "/";
        if (!"GET".equalsIgnoreCase(method)) {
            writeText(out, 405, "Method Not Allowed", "text/html; charset=UTF-8", "<h1>405 - Solo GET</h1>");
            return;
        }

        String path = target, query = "";
        int qIdx = target.indexOf('?');
        if (qIdx >= 0) { path = target.substring(0, qIdx); query = target.substring(qIdx + 1); }
        Map<String,String> qps = parseQuery(query);

        // Rutas dinámicas
        Handler h = routes.get(path);
        if (h != null) {
            String body;
            try {
                body = h.invoke(qps);
            } catch (InvocationTargetException ite) {
                Throwable t = ite.getTargetException();
                t.printStackTrace();
                body = "<h1>500</h1><pre>" + escapeHtml(t.toString()) + "</pre>";
                writeText(out, 500, "Internal Server Error", "text/html; charset=UTF-8", body);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                body = "<h1>500</h1><pre>" + escapeHtml(e.toString()) + "</pre>";
                writeText(out, 500, "Internal Server Error", "text/html; charset=UTF-8", body);
                return;
            }
            writeText(out, 200, "OK", "text/html; charset=UTF-8", body == null ? "" : body);
            return;
        }

        // Estáticos
        byte[] bytes = tryServeStatic(path);
        if (bytes != null) {
            writeBytes(out, 200, "OK", mime(path), bytes);
            return;
        }

        writeText(out, 404, "Not Found", "text/html; charset=UTF-8", "<h1>404 - Not Found</h1>");
    }

    /** ====== Utilidades HTTP ====== */
    private static void writeText(OutputStream out, int code, String reason, String ctype, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        writeStart(out, code, reason, ctype, bytes.length);
        out.write(bytes);
        out.flush();
    }
    private static void writeBytes(OutputStream out, int code, String reason, String ctype, byte[] bytes) throws IOException {
        writeStart(out, code, reason, ctype, bytes.length);
        out.write(bytes);
        out.flush();
    }
    private static void writeStart(OutputStream out, int code, String reason, String ctype, int len) throws IOException {
        line(out, "HTTP/1.1 " + code + " " + reason);
        line(out, "Content-Type: " + ctype);
        line(out, "Content-Length: " + len);
        line(out, "Connection: close");
        line(out, "");
    }
    private static void line(OutputStream out, String s) throws IOException {
        out.write((s + "\r\n").getBytes(StandardCharsets.UTF_8));
    }

    private static Map<String,String> parseQuery(String q) {
        Map<String,String> m = new HashMap<>();
        if (q == null || q.isEmpty()) return m;
        for (String p : q.split("&")) {
            int i = p.indexOf('=');
            String k = i >= 0 ? p.substring(0, i) : p;
            String v = i >= 0 ? p.substring(i + 1) : "";
            m.put(urlDecode(k), urlDecode(v));
        }
        return m;
    }
    private static String urlDecode(String s) { return URLDecoder.decode(s, StandardCharsets.UTF_8); }

    private static String normalize(String p) { return (p == null || p.isEmpty()) ? "/" : (p.startsWith("/") ? p : "/" + p); }
    private static String escapeHtml(String s){ return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;"); }

    /** ====== Estáticos desde classpath /public ====== */
    private static byte[] tryServeStatic(String requestPath) {
        String path = sanitize(requestPath);
        String resource = mapToResource(path);
        try (InputStream in = HttpServer.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) return null;
            return in.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }
    private static String mapToResource(String p) {
        if (p.equals("/")) return "public/index.html";
        if (p.endsWith("/")) return "public" + p + "index.html";
        return "public" + p;
    }
    private static String sanitize(String p) {
        String s = (p == null || p.isEmpty()) ? "/" : p;
        s = s.replace('\\', '/');
        while (s.contains("..")) s = s.replace("..", "");
        if (!s.startsWith("/")) s = "/" + s;
        return s;
    }
    private static String mime(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".html") || n.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (n.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (n.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        if (n.endsWith(".png"))  return "image/png";
        if (n.endsWith(".jpg") || n.endsWith(".jpeg")) return "image/jpeg";
        if (n.endsWith(".gif"))  return "image/gif";
        if (n.endsWith(".svg"))  return "image/svg+xml; charset=UTF-8";
        if (n.endsWith(".ico"))  return "image/x-icon";
        if (n.endsWith(".txt"))  return "text/plain; charset=UTF-8";
        return "application/octet-stream";
    }

    /** ====== Carga de controladores ====== */
    private static List<Object> loadControllersByNames(List<String> names) throws Exception {
        List<Object> out = new ArrayList<>();
        for (String fqcn : names) out.add(instantiateController(fqcn));
        return out;
    }
    private static Object instantiateController(String fqcn) throws Exception {
        Class<?> cz = Class.forName(fqcn);
        if (!cz.isAnnotationPresent(RestController.class))
            throw new IllegalArgumentException("Falta @RestController en " + fqcn);
        Constructor<?> k = cz.getDeclaredConstructor();
        k.setAccessible(true);
        return k.newInstance();
    }
    private static List<Object> loadControllersByScan() {
        List<Object> out = new ArrayList<>();
        for (String name : scanAppClasses()) {
            try {
                Class<?> cl = Class.forName(name);
                if (cl.isAnnotationPresent(RestController.class)) {
                    Constructor<?> k = cl.getDeclaredConstructor();
                    k.setAccessible(true);
                    out.add(k.newInstance());
                    System.out.println("Cargado controller: " + name);
                }
            } catch (Throwable ignored) {}
        }
        return out;
    }
    private static List<String> scanAppClasses() {
        List<String> result = new ArrayList<>();
        try {
            URL rootUrl = HttpServer.class.getProtectionDomain().getCodeSource().getLocation();
            File root = new File(URLDecoder.decode(rootUrl.getPath(), StandardCharsets.UTF_8));
            if (root.isDirectory()) walk(root, root.getAbsolutePath().length() + 1, result);
        } catch (Exception ignored) {}
        return result;
    }
    private static void walk(File dir, int cut, List<String> out) {
        File[] fs = dir.listFiles(); if (fs == null) return;
        for (File f : fs) {
            if (f.isDirectory()) walk(f, cut, out);
            else if (f.getName().endsWith(".class") && !f.getName().contains("$")) {
                String rel = f.getAbsolutePath().substring(cut).replace(File.separatorChar, '.');
                out.add(rel.substring(0, rel.length() - ".class".length()));
            }
        }
    }

    /** ====== Tipos auxiliares ====== */
    private record Args(int port, List<String> classNames) {}
    private static Args parseArgs(String[] args) {
        int port = 8080; List<String> classes = new ArrayList<>();
        for (String a : args) { if (a.matches("\\d+")) port = Integer.parseInt(a); else if (a.contains(".")) classes.add(a); }
        System.out.println("CLI -> port=" + port + " classes=" + classes);
        return new Args(port, classes);
    }

    /** Handler: guarda método + instancia, o bien un lambda simple (fallback). */
    private static class Handler {
        final Object target; final Method method; final Simple simple;
        Handler(Object target, Method method, Simple simple){ this.target=target; this.method=method; this.simple=simple; }
        String invoke(Map<String,String> qps) throws Exception {
            if (simple != null) return simple.handle(qps);
            Parameter[] ps = method.getParameters();
            Object[] args = new Object[ps.length];
            for (int i = 0; i < ps.length; i++) {
                Parameter p = ps[i];
                if (p.getType().equals(String.class)) {
                    RequestParam rp = p.getAnnotation(RequestParam.class);
                    if (rp == null) throw new IllegalStateException("Parámetro String sin @RequestParam en " + method);
                    String v = qps.get(rp.value());
                    args[i] = (v != null) ? v : rp.defaultValue();
                } else {
                    throw new IllegalStateException("Solo se soportan parámetros String con @RequestParam");
                }
            }
            Object ret = method.invoke(target, args);
            return ret == null ? "" : ret.toString();
        }
    }
    @FunctionalInterface private interface Simple { String handle(Map<String,String> qps); }
}
