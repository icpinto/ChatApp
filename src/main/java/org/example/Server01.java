package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server01 {

    public static void main(String[] args) {
        int portA = 12345; // Server A listening port
        String hostnameB = "localhost"; // Server B hostname
        int portB = 12346; // Server B listening port

        // Start listening for connections from Server B
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(portA);
                 PrintWriter logWriter = new PrintWriter(new FileWriter("ServerA_Log.txt", true))) {
                System.out.println("Server A started, waiting for connection from Server B...");

                Socket socket = serverSocket.accept();
                System.out.println("Server A: Connected to Server B");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                Thread readThread = new Thread(() -> {
                    String message;
                    try {
                        while ((message = in.readLine()) != null) {
                            System.out.println("Server A received: " + message);
                            logWriter.println("Received: " + message);
                            logWriter.flush();
                        }
                    } catch (IOException e) {
                        System.err.println("Server A read error: " + e.getMessage());
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
                System.err.println("Server A error: " + e.getMessage());
            }
        }).start();

        // Connect to Server B
        new Thread(() -> {
            try (Socket socket = new Socket(hostnameB, portB);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 PrintWriter logWriter = new PrintWriter(new FileWriter("ServerA_Log.txt", true))) {

                System.out.println("Server A: Connected to Server B on " + hostnameB + ":" + portB);

                Thread readThread = new Thread(() -> {
                    String message;
                    try {
                        while ((message = in.readLine()) != null) {
                            System.out.println("Server B says: " + message);
                            logWriter.println("Received: " + message);
                            logWriter.flush();
                        }
                    } catch (IOException e) {
                        System.err.println("Server A client read error: " + e.getMessage());
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
                System.err.println("Server A client error: " + e.getMessage());
            }
        }).start();
    }
}
