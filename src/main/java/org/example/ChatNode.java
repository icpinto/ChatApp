package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatNode extends Node implements Runnable {

    private final String userName;
    private  Map<String, Integer> remotePorts = new HashMap<>();
    private final String hostname = "localhost";
    private final int serverNodePort;
    private final List<String> messageHistory = new ArrayList<>(); // List to store messages

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
                new Thread(() -> startReading(in, logWriter)).start();
                /**new Thread(() -> {
                    try {
                        startWriting(out, logWriter);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();**/
                //startReading(in, logWriter);
                //startWriting(out, logWriter);
                showMenu(out);

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
                    synchronized (messageHistory) {
                        messageHistory.add(message); // Store received messages
                    }
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

    private void showMenu(PrintWriter out) {
        Scanner scanner = new Scanner(System.in);
        String choice;

        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Read Messages");
            System.out.println("2. Send Message");
            System.out.println("3. Send Private Message");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    readMessages();
                    break;
                case "2":
                    sendMessage(scanner, out);
                    break;
                case "3":
                    sendPrivateMessage(scanner, out);
                    break;
                case "4":
                    exitApplication(out);
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void readMessages() {
        System.out.println("Reading messages from server...");
        synchronized (messageHistory) {
            for (String message : messageHistory) {
                System.out.println(message);
            }
        }
    }

    private void sendMessage(Scanner scanner, PrintWriter out) {
        System.out.print("Enter your message: ");
        String message = scanner.nextLine();
        out.println(message);
        logMessage("Sent: " + message);
    }

    private void sendPrivateMessage(Scanner scanner, PrintWriter out) {
        System.out.print("Enter recipient's username: ");
        String recipient = scanner.nextLine();
        System.out.print("Enter your message: ");
        String message = scanner.nextLine();
        out.println("/pm " + recipient + " " + message);
        logMessage("Sent private message to " + recipient + ": " + message);
    }

    private void logMessage(String message) {
        try (PrintWriter logWriter = new PrintWriter(new FileWriter("ChatNode_Log.txt", true))) {
            logWriter.println(message);
        } catch (IOException e) {
            System.err.println("Error logging message: " + e.getMessage());
        }
    }

    private void exitApplication(PrintWriter out) {
        System.out.println("Exiting application...");
        if (out != null) {
            out.close();
        }
        System.exit(0);
    }


    @Override
    public void run() {
        startServer();
        startClient();
    }
}
