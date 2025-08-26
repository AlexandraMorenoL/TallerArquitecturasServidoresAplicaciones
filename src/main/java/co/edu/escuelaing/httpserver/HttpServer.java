package co.edu.escuelaing.httpserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor HTTP mínimo con soporte de rutas.
 */


public class HttpServer {
    private Router router;

    public HttpServer(Router router) {
        this.router = router;
    }

    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor HTTP escuchando en el puerto " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleClient(clientSocket);
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();

        String requestLine = in.readLine(); 
        if (requestLine == null || requestLine.isEmpty()) {
            clientSocket.close();
            return;
        }

        String[] parts = requestLine.split(" ");
        String path = parts[1]; // Ejemplo: /hello?name=Alexandra

        // Obtener respuesta desde el Router
        String response = router.resolve(path);

        // Armar respuesta HTTP completa
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + response.getBytes().length + "\r\n" +
                "\r\n" +
                response;

        out.write(httpResponse.getBytes());
        out.flush();
        clientSocket.close();
    }
}
