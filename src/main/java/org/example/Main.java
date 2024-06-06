package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ServerNode sn1 = new ServerNode(11111);
        new Thread(sn1).start();
        ServerNode.addChatNode("localhost", 12111, 11111);

    }
}