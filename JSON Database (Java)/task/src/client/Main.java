package client;

import client.command_forwarding_domain.CommandFactory;
import client.command_forwarding_domain.CommandForwarder;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


public class Main {
    private static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        String address = "127.0.0.1";
        int port = 23456;

        try (Socket socket = new Socket(InetAddress.getByName(address), port)) {
            System.out.println("\nClient started (args: " + String.join(" ", args) + ")");

            CommandForwarder commandForwarder = new CommandForwarder(
                    new DataInputStream(socket.getInputStream()),
                    new DataOutputStream(socket.getOutputStream()),
                    command -> gson.toJson(command.toMap())
            );

            CommandFactory.from(args, commandForwarder)
                    .ifPresent(command -> {
                        String response = command.exec();
                        System.out.println("Received: " + response);
                    });
        }
    }
}
