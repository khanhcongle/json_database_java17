package client.command_forwarding_domain;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Function;

public class CommandForwarder {

    private DataInputStream input;
    private DataOutputStream output;
    private Function<Command, String> requestConverter;

    public CommandForwarder(DataInputStream input, DataOutputStream output, Function<Command, String> requestConverter) {
        this.input = input;
        this.output = output;
        this.requestConverter = requestConverter;
    }

    public void send(Command message) {
        try {
            this.output.writeUTF(requestConverter.apply(message));
            System.out.println("Sent: " + requestConverter.apply(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String receive() {
        try {
            return this.input.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
