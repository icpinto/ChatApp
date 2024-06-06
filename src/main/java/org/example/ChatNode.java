package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatNode extends Node implements Runnable {

    private final String userName;
    public ChatNode(String hostname, int localPort, int remotePort, String userName) {
        super(hostname, localPort, remotePort);
        this.userName = userName;
    }

    @Override
    public void run() {
        startServer();
        startClient();
    }

    public static void main(String[] args) {
        //ChatNode node1 = new ChatNode("localhost", 12345, 12346);
        ChatNode node2 = new ChatNode("localhost", 12346, 12345, "node2");

        //new Thread(node1).start();
        new Thread(node2).start();
    }
}
