package org.example;

public class ChatNodeLauncher {
    public static void main(String[] args) {
        System.out.println("sddsddsd");
        if (args.length != 3) {
            System.out.println("Usage: java ChatNodeLauncher <localPort> <serverNodePort> <clientName>");
            System.exit(1);
        }

        //String hostname = args[0];
        int localPort = Integer.parseInt(args[0]);
        int serverNodePort = Integer.parseInt(args[1]);
        String clientName = args[2];

        ChatNode node = new ChatNode( localPort, serverNodePort, clientName);
        new Thread(node::startClient).start();

    }
}
