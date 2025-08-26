package co.edu.escuelaing.example;

import co.edu.escuelaing.httpserver.HttpServer;
import co.edu.escuelaing.httpserver.Router;

/**
 *
 * Autor: Alexandra Moreno Latorre
 */

public class App {
    public static void main(String[] args) throws Exception {
        Router router = new Router();
        HttpServer server = new HttpServer(router);
        server.start(35006); // Puerto del servidor
    }
}

