package server.app;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MessageClient<T> implements Closeable {
    private final Function<Map, String> toOutput;
    private final BiFunction<String, Type, T> fromInput;
    private final DataInput input;
    private final DataOutput output;
    private final Socket socket;
    public MessageClient(Socket socket,
                         Function<Map, String> toOutput,
                         BiFunction<String, Type, T> fromInput) throws IOException {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.toOutput = toOutput;
        this.fromInput = fromInput;
    }
    public T readNewMessage(Class<T> clazz) {
        try {
            return fromInput.apply(input.readUTF(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendSuccessMessage(Object message) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("response", "OK");
            if (message != null) {
                map.put("value", message);
            }
            output.writeUTF(toOutput.apply(map));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendErrorMessage(Object message) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("response", "ERROR");
            if (message != null) {
                map.put("reason", message);
            }
            output.writeUTF(toOutput.apply(map));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            // do nothing
        }
    }
}
