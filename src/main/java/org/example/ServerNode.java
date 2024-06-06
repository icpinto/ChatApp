package org.example;

import java.util.HashMap;
import java.util.Map;

public class ServerNode extends  Node implements Runnable {
    private static int clientCount;
    private static final Map<String, Integer> clientports = new HashMap<>();


    public ServerNode(String hostname, int localPort, int remotePort) {
        super(hostname, localPort, remotePort);
    }

    public static synchronized ChatNode addChatNode(String hostname, int localPort, int remotePort){
        clientCount+= clientCount;
        String newClientName = "Client" + clientCount;
        ChatNode node1 = new ChatNode("localhost", 12345, 12346, newClientName);
        clientports.put(newClientName, localPort);
        return node1;
    }

    @Override
    public void run() {
        startServer();
        startClient();

    }
}
