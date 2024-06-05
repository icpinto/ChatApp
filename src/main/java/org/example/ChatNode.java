package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatNode implements Runnable {
    private final String hostname; // Remote server address
    private final int localPort; // Local listening port
    private final int remotePort; // Remote server port

    public ChatNode(String hostname, int localPort, int remotePort) {
        this.hostname = hostname;
        this.localPort = localPort;
        this.remotePort = remotePort;
    }

    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(localPort);
                 PrintWriter logWriter = new PrintWriter(new FileWriter("ChatNode_Log.txt", true))) {
                System.out.println("ChatNode started, waiting for connection on port " + localPort + "...");

                Socket socket = serverSocket.accept();
                System.out.println("ChatNode: Connected to client");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                Thread readThread = new Thread(() -> {
                    String message;
                    try {
                        while ((message = in.readLine()) != null) {
                            System.out.println("Received: " + message);
                            logWriter.println("Received: " + message);
                            logWriter.flush();
                        }
                    } catch (IOException e) {
                        System.err.println("ChatNode read error: " + e.getMessage());
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
                System.err.println("ChatNode server error: " + e.getMessage());
            }
        }).start();
    }

    public void startClient() {
        new Thread(() -> {
            try (Socket socket = new Socket(hostname, remotePort);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 PrintWriter logWriter = new PrintWriter(new FileWriter("ChatNode_Log.txt", true))) {

                System.out.println("ChatNode: Connected to server on " + hostname + ":" + remotePort);

                Thread readThread = new Thread(() -> {
                    String message;
                    try {
                        while ((message = in.readLine()) != null) {
                            System.out.println("Server says: " + message);
                            logWriter.println("Received: " + message);
                            logWriter.flush();
                        }
                    } catch (IOException e) {
                        System.err.println("ChatNode client read error: " + e.getMessage());
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
                System.err.println("ChatNode client error: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void run() {
        startServer();
        startClient();
    }

    public static void main(String[] args) {
        //ChatNode node1 = new ChatNode("localhost", 12345, 12346);
        ChatNode node2 = new ChatNode("localhost", 12346, 12345);

        //new Thread(node1).start();
        new Thread(node2).start();
    }
}
