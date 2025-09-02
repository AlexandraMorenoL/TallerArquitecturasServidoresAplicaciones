package co.edu.escuelaing;

/**
 * Controlador de /greeting.
 * Responde con una página HTML que muestra “Hola, <nombre>” en grande.
 * 
 * @author Alexandra Moreno
 */

@RestController
public class GreetingController {

    /**
     * Lee ?name=... (por defecto "World") y renderiza el saludo.
     */
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        String safe = escapeHtml(name); // evitar HTML inyectado
        return "<!doctype html><html lang='es'><meta charset='utf-8'>"
             + "<body style='margin:0;display:flex;align-items:center;justify-content:center;height:100vh;"
             + "font-family: system-ui,-apple-system,Segoe UI,Roboto,sans-serif;'>"
             + "<h1 style='font-size:64px;margin:0;'>Hola, " + safe + "</h1>"
             + "</body></html>";
    }

    /** Escapa caracteres básicos para no romper el HTML. */
    private static String escapeHtml(String s) {
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#x27;");
    }
}
