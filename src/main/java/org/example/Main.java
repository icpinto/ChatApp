package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ChatNode node1 = new ChatNode("localhost", 12345, 12346);
        //ChatNode node2 = new ChatNode("localhost", 12346, 12345);

        new Thread(node1).start();
        //new Thread(node2).start();

    }
}