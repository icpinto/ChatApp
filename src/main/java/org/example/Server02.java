package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server02 {

    public static void main(String[] args) {
        int portB = 12346; // Server B listening port
        String hostnameA = "localhost"; // Server A hostname
        int portA = 12345; // Server A listening port

        // Start listening for connections from Server A
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(portB);
                 PrintWriter logWriter = new PrintWriter(new FileWriter("ServerB_Log.txt", true))) {
                System.out.println("Server B started, waiting for connection from Server A...");

                Socket socket = serverSocket.accept();
                System.out.println("Server B: Connected to Server A");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                Thread readThread = new Thread(() -> {
                    String message;
                    try {
                        while ((message = in.readLine()) != null) {
                            System.out.println("Server B received: " + message);
                            logWriter.println("Received: " + message);
                            logWriter.flush();
                        }
                    } catch (IOException e) {
                        System.err.println("Server B read error: " + e.getMessage());
                    }
                });
                readThread.start();

                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                    out.println(userInput);
                    logWriter.println("Sent: " + userInput);
                    logWriter.flush();
                }

            } catch (IOException e) {
                System.err.println("Server B error: " + e.getMessage());
            }
        }).start();

        // Connect to Server A
        new Thread(() -> {
            try (Socket socket = new Socket(hostnameA, portA);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 PrintWriter logWriter = new PrintWriter(new FileWriter("ServerB_Log.txt", true))) {

                System.out.println("Server B: Connected to Server A on " + hostnameA + ":" + portA);

                Thread readThread = new Thread(() -> {
                    String message;
                    try {
                        while ((message = in.readLine()) != null) {
                            System.out.println("Server A says: " + message);
                            logWriter.println("Received: " + message);
                            logWriter.flush();
                        }
                    } catch (IOException e) {
                        System.err.println("Server B client read error: " + e.getMessage());
                    }
                });
                readThread.start();

                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                    out.println(userInput);
                    logWriter.println("Sent: " + userInput);
                    logWriter.flush();
                }

            } catch (IOException e) {
                System.err.println("Server B client error: " + e.getMessage());
            }
        }).start();
    }
}
