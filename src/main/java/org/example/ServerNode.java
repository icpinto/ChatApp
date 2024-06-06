package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerNode extends  Node implements Runnable {
    private static int clientCount;
    private static final Map<String, Integer> clientPorts = new HashMap<>();


    public ServerNode(int localPort) {
        super( localPort);
    }

    public static synchronized void addChatNode(String hostname, int clientPort, int serverNodePort) {
        clientCount++;
        String newClientName = "Client" + clientCount;

        clientPorts.put(newClientName, clientPort);

        // Construct the command to launch ChatNode
        List<String> command = new ArrayList<>();
        String javaBin = System.getProperty("java.home") + "/bin/java";
        String classpath = "/home/icpinto/Desktop/Java/P2P/src/main/java/"; // Ensure this is the correct path to the classes

        // Basic command to run the Java class
        command.add("xterm");
        command.add("-e");
        command.add(javaBin);
        command.add("-cp");
        command.add(classpath);
        command.add("org.example.ChatNodeLauncher");
        command.add(String.valueOf(clientPort));
        command.add(String.valueOf(serverNodePort));
        command.add(newClientName);

        try {
            System.out.println(command);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.start();
        } catch (IOException e) {
            System.err.println("Error starting new ChatNode process: " + e.getMessage());
        }
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

    public void startClient(String hostname, int remotePort) {
        new Thread(() -> {
            try (Socket socket = new Socket(hostname, remotePort);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 PrintWriter logWriter = new PrintWriter(new FileWriter("ChatNode_Log.txt", true))) {

                System.out.println("ChatNode: Connected to server on " + hostname + ":" + remotePort);
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
        //startClient();

    }
}
