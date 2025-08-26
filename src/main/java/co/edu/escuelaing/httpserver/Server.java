package co.edu.escuelaing.httpserver;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

/**
 * Servidor unificado con varios modos:
 * - Echo (TCP)
 * - Function (TCP)
 * - Time (UDP)
 * - HTTP simple
 * - WebFile server
 *
 * Autor: Alexandra Moreno Latorre
 */
public class Server {

    /**
     * Servidor TCP Echo.
     */
    public void startEchoServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("EchoServer escuchando en " + port);
            try (Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    try {
                        double num = Double.parseDouble(inputLine);
                        out.println("El cuadrado es: " + (num * num));
                    } catch (NumberFormatException e) {
                        out.println("Por favor ingresa un número válido.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Servidor TCP para funciones matemáticas.
     */
    public void startFunctionServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("FunctionServer escuchando en " + port);
            try (Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Recibido: " + request);
                    String[] parts = request.split(":");
                    if (parts.length == 2) {
                        String func = parts[0];
                        double val = Double.parseDouble(parts[1]);
                        if ("square".equalsIgnoreCase(func)) {
                            out.println("Resultado: " + (val * val));
                        } else {
                            out.println("Función no soportada.");
                        }
                    } else {
                        out.println("Formato inválido. Usa function:value");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Servidor UDP que responde la hora actual.
     */
    public void startTimeUDPServer(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("TimeServerUDP escuchando en " + port);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String response = "Hora actual: " + LocalDateTime.now();
                byte[] respData = response.getBytes();

                DatagramPacket responsePacket = new DatagramPacket(
                        respData, respData.length,
                        request.getAddress(), request.getPort());
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Servidor HTTP simple (responde texto fijo).
     */
    public void startHttpServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("HttpServer escuchando en " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String line;
                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        System.out.println(">> " + line);
                    }

                    out.println("HTTP/1.1 200 OK\r\n");
                    out.println("Content-Type: text/html\r\n");
                    out.println("\r\n");
                    out.println("<h1>Servidor HTTP activo</h1>");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Servidor WebFile: sirve un archivo HTML simple.
     */
    public void startWebFileServer(int port, String filePath) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("WebFileServer escuchando en " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     OutputStream out = clientSocket.getOutputStream();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String line;
                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        System.out.println(">> " + line);
                    }

                    File file = new File(filePath);
                    if (file.exists()) {
                        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
                        out.write(("HTTP/1.1 200 OK\r\nContent-Length: " + fileBytes.length + "\r\n\r\n").getBytes());
                        out.write(fileBytes);
                    } else {
                        String errorMsg = "<h1>404 Not Found</h1>";
                        out.write(("HTTP/1.1 404 Not Found\r\nContent-Length: " + errorMsg.length() + "\r\n\r\n").getBytes());
                        out.write(errorMsg.getBytes());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
