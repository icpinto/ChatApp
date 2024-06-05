package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server02 {

    public static void main(String[] args) {
        int portB = 12346; // Server B listening port
        String hostnameA = "localhost"; // Server A hostname
        int portA = 12345; // Server A listening port

        // Start listening for connections from Server A
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(portB)) {
                System.out.println("Server B started, waiting for connection from Server A...");

                Socket socket = serverSocket.accept();
                System.out.println("Server B: Connected to Server A");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Server B received: " + message);
                    out.println("Acknowledged: " + message);
                }
            } catch (IOException e) {
                System.err.println("Server B error: " + e.getMessage());
            }
        }).start();

        // Connect to Server A
        try (Socket socket = new Socket(hostnameA, portA);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Server B: Connected to Server A on " + hostnameA + ":" + portA);

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("Server A says: " + in.readLine());
            }

        } catch (IOException e) {
            System.err.println("Server B client error: " + e.getMessage());
        }
    }
}
