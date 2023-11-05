package server.app;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MessageClient<T> implements Closeable {
    private final Function<Map, String> outputTranslator;
    private final BiFunction<String, Type, T> inputTranslator;
    private final DataInput input;
    private final DataOutput output;
    private final Socket socket;
    public MessageClient(Socket socket,
                         Function<Map, String> outputTranslator,
                         BiFunction<String, Type, T> inputTranslator) throws IOException {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.outputTranslator = outputTranslator;
        this.inputTranslator = inputTranslator;
    }
    public T readNewMessage(Class<T> clazz) {
        try {
            return inputTranslator.apply(input.readUTF(), clazz);
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
            output.writeUTF(outputTranslator.apply(map));
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
            output.writeUTF(outputTranslator.apply(map));
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
