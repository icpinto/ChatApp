package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server01 {

    public static void main(String[] args) {
        int portA = 12345; // Server A listening port
        String hostnameB = "localhost"; // Server B hostname
        int portB = 12346; // Server B listening port

        // Start listening for connections from Server B
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(portA)) {
                System.out.println("Server A started, waiting for connection from Server B...");

                Socket socket = serverSocket.accept();
                System.out.println("Server A: Connected to Server B");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Server A received: " + message);
                    out.println("Acknowledged: " + message);
                }
            } catch (IOException e) {
                System.err.println("Server A error: " + e.getMessage());
            }
        }).start();

        // Connect to Server B
        try (Socket socket = new Socket(hostnameB, portB);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Server A: Connected to Server B on " + hostnameB + ":" + portB);

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("Server B says: " + in.readLine());
            }

        } catch (IOException e) {
            System.err.println("Server A client error: " + e.getMessage());
        }
    }
}
