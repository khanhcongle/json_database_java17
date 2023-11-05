package server.app;

import client.command_forwarding_domain.Command;
import com.google.gson.Gson;
import server.data_access_domain.ExitRequestedException;
import server.data_access_domain.use_case.CommandHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class WebSocketServer {
    private static final Gson gson = GsonBean.getInstance();
    private boolean isRunning = true;

    public static void startServer(CommandHandler handler) throws IOException {
        new WebSocketServer().start(handler);
    }

    private void start(CommandHandler handler) throws IOException {
        isRunning = true;

        try (ServerSocket server = new ServerSocket(Configs.WEB_SOCKET_PORT, 50, InetAddress.getByName(Configs.WEB_SOCKET_ADDRESS))) {
            System.out.println("Server started!");
            while (isRunning) {
                Socket socket = server.accept(); // new client connection

                new Thread(() -> {
                    MessageClient<Command> messager = createMessageClient(socket);
                    try {
                        handler.handle(messager.readNewMessage(Command.class))
                                .thenAccept(object -> messager.sendSuccessMessage(object));

                    } catch (ExitRequestedException e) {
                        messager.sendSuccessMessage(null);
                        isRunning = false; // terminate the loop
                        try {
                            server.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    } catch (Exception e) {
                        messager.sendErrorMessage(null);
                        throw e;
                    } finally {
                        messager.close();
                    }
                }).start();
            }
        }
    }

    private static <T> MessageClient<T> createMessageClient(Socket socket) {
        MessageClient<T> messageClient;
        try {
            messageClient = new MessageClient<>(socket, gson::toJson, gson::fromJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return messageClient;
    }

}
