package co.edu.escuelaing.httpserver;

/**
 *
 * Autor: Alexandra Moreno Latorre
 */
public interface FunctionalHandler {
    String handle(HttpRequest req, HttpResponse res);
}
