package org.example;

public abstract class Node {
    //protected final String hostname;
    protected final int localPort;
    //protected final int remotePort;

    public Node(int localPort) {
        //this.hostname = hostname;
        this.localPort = localPort;
        //this.remotePort = remotePort;
    }

    public abstract void startServer();
    //public abstract void startClient();
}
