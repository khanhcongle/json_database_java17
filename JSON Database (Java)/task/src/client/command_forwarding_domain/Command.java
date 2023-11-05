package client.command_forwarding_domain;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.StringConverter;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

public class Command {
    private Object value;
    @Parameter(names = {"-t", "--type"})
    private String type;
    @Parameter(names = {"-v", "--value"}, converter = StringConverter.class)
    private String stringValue;

    @Parameter(
            names = {"-k", "--key"},
            converter = KeyConverterForJCommander.class
        )
    private Key key;

    @Parameter(names = {"-in"})
    private String filePath;

    public Command(Command command) {
        this.type = command.type;
        this.key = command.key;
        if (command.getValue() instanceof String) {
            stringValue = (String) command.getValue();
        } else {
            value = command.getValue();
        }
    }

    public Command() {

    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return stringValue != null ? stringValue : value;
    }
    public void setValue(Object object) {
        this.value = object;
    }

    public Key getKey() {
        return this.key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Optional.ofNullable(getType())
                .ifPresent(type -> map.put("type", type));
        Optional.ofNullable(getKey())
                .ifPresent(key -> map.put("key", key.get())); // TODO
        Optional.ofNullable(getValue())
                .ifPresent(value -> map.put("value", value));
        return map;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static class Key<T> implements Comparable, Supplier {
        private T key;

        public Key(T keys) {
            this.key = keys;
        }

        @Override
        public T get() {
            return key;
        }

        @Override
        public int compareTo(Object o) {
            if (key instanceof String) {
                ((String) key).compareTo((String) o);
            }
            return 0;
        }
    }
    public static class StringKey extends Key<String> {

        public StringKey(String keys) {
            super(keys);
        }
    }
    public static class ArrayKey extends Key<List> {

        public ArrayKey(List<String> keys) {
            super(keys);
        }
    }

    public static class KeyConverter implements JsonDeserializer<Key> {

        @Override
        public Command.Key deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return new Command.StringKey(json.getAsString());

            } else if (json.isJsonArray()) {
                JsonArray jsonArray = json.getAsJsonArray();
                List<String> keys = new ArrayList<>();
                for (JsonElement element : jsonArray) {
                    keys.add(element.getAsString());
                }
                return new Command.ArrayKey(keys);
            } else {
                throw new JsonParseException("Invalid JSON value for Key field: " + json);
            }
        }
    }
    public static class KeyConverterForJCommander implements IStringConverter<StringKey> {

        @Override
        public StringKey convert(String value) {
            return new StringKey(value);
        }
    }

    public static class StringConverterForJCommander implements IStringConverter<String> {

        @Override
        public String convert(String value) {
            return value;
        }
    }
}
