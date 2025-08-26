package co.edu.escuelaing.httpserver;

import java.io.*;
import java.net.*;

/**
 * Cliente unificado que permite conectarse a diferentes tipos de servidores:
 * - Echo (TCP)
 * - Function (TCP)
 * - Time (UDP)
 *
 * Autor: Alexandra Moreno Latorre
 */
public class Client {

    /**
     * Cliente TCP tipo "Echo".
     */
    public void startEchoClient(String host, int port) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("EchoClient conectado a " + host + ":" + port);
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo: " + in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cliente TCP que llama funciones (ej. cuadrado).
     */
    public void startFunctionClient(String host, int port, String function, double value) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(function + ":" + value);
            System.out.println("Respuesta del servidor: " + in.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cliente UDP que pide la hora.
     */
    public void startTimeUDPClient(String host, int port) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] buffer = "TIME".getBytes();
            InetAddress address = InetAddress.getByName(host);

            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(request);

            byte[] recvBuffer = new byte[1024];
            DatagramPacket response = new DatagramPacket(recvBuffer, recvBuffer.length);
            socket.receive(response);

            String received = new String(response.getData(), 0, response.getLength());
            System.out.println("Hora recibida del servidor: " + received);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
