package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatNode extends Node implements Runnable {

    private final String userName;
    private  Map<String, Integer> remotePorts = new HashMap<>();
    private final String hostname = "localhost";
    private final int serverNodePort;
    public ChatNode(int localPort, int serverNodePort, String userName) {
        super(localPort);
        this.userName = userName;
        this.serverNodePort = serverNodePort;
    }

    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(this.localPort);
                 PrintWriter logWriter = new PrintWriter(new FileWriter("ChatNode_Log.txt", true))) {
                System.out.println("ChatNode started, waiting for connection on port " + this.localPort + "...");

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
            try (Socket socket = new Socket(this.hostname, this.serverNodePort);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 PrintWriter logWriter = new PrintWriter(new FileWriter("ChatNode_Log.txt", true))) {

                System.out.println("ChatNode: Connected to server on " + this.hostname + ":" + this.serverNodePort);
                startReading(in, logWriter);
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


    @Override
    public void run() {
        startServer();
        startClient();
    }
}
