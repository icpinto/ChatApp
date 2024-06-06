package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Node {
    private final String hostname; // Remote server address
    private final int localPort; // Local listening port
    private final int remotePort; // Remote server port

    public Node(String hostname, int localPort, int remotePort) {
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

                startReading(in, logWriter);
                startWriting(out, logWriter);

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
                /**
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
                readThread.start();**/
                startReading(in, logWriter);

                /**BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                    out.println(userInput);
                    logWriter.println("Sent: " + userInput);
                    logWriter.flush();
                }**/
                startWriting(out, logWriter);

            } catch (IOException e) {
                System.err.println("ChatNode client error: " + e.getMessage());
            }
        }).start();
    }

    private void startReading(BufferedReader in, PrintWriter logWriter) {
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
    }

    private void startWriting(PrintWriter out, PrintWriter logWriter) throws IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        try {
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                logWriter.println("Sent: " + userInput);
                logWriter.flush();
            }
        } catch (IOException e) {
            System.err.println("ChatNode client write error: " + e.getMessage());
        }
    }


}
