package co.edu.escuelaing.httpserver;

/**
 * Router para manejar las rutas del servidor
 * Autor: Alexandra Moreno Latorre
 */

public class Router {

    public String resolve(String path) {
        if (path.equals("/")) {
            return paginaInicio();
        } else if (path.startsWith("/hello")) {
            String nombre = "desconocido";
            if (path.contains("name=")) {
                nombre = path.split("name=")[1];
            }
            return paginaSaludo(nombre);
        } else if (path.equals("/pi")) {
            return paginaPi();
        } else {
            return pagina404();
        }
    }

    private String paginaInicio() {
        return "<!DOCTYPE html>" +
                "<html><head><title>Inicio</title></head>" +
                "<body>" +
                "<h1>Bienvenido a mi servidor HTTP</h1>" +
                "<p>Prueba estas rutas:</p>" +
                "<ul>" +
                "<li><a href='/hello?name=Alexandra'>Saludo con tu nombre</a></li>" +
                "<li><a href='/pi'>Valor de PI</a></li>" +
                "</ul>" +
                "</body></html>";
    }

    private String paginaSaludo(String nombre) {
        return "<!DOCTYPE html>" +
                "<html><head><title>Saludo</title></head>" +
                "<body>" +
                "<h1>Hola " + nombre + " desde HttpServer</h1>" +
                "<a href='/'>Volver al inicio</a>" +
                "</body></html>";
    }

    private String paginaPi() {
        return "<!DOCTYPE html>" +
                "<html><head><title>PI</title></head>" +
                "<body>" +
                "<h1>El valor de π es: " + Math.PI + "</h1>" +
                "<a href='/'>Volver al inicio</a>" +
                "</body></html>";
    }

    private String pagina404() {
        return "<!DOCTYPE html>" +
                "<html><head><title>Error 404</title></head>" +
                "<body>" +
                "<h1>404 - Página no encontrada</h1>" +
                "<a href='/'>Volver al inicio</a>" +
                "</body></html>";
    }
}
