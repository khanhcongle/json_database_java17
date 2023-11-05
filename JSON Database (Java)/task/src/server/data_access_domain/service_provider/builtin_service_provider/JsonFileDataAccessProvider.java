package server.data_access_domain.service_provider.builtin_service_provider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.app.GsonBean;
import server.data_access_domain.service_provider.DataAccessProvider;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class JsonFileDataAccessProvider<K extends Supplier & Comparable, T>
        implements DataAccessProvider<K, T> {
    private final Gson gson = GsonBean.getInstance();
    private final File file;
    public JsonFileDataAccessProvider(String filePath) throws IOException {
        file = new File(filePath);
        System.out.println(file.getAbsolutePath());

        if (file.createNewFile()) {
            System.out.println("created new db file");
        } else {
            System.out.println("db file already exist");
        }
    }

    @Override
    public void delete(K key) {
        Map<String, Object> records = getDbContentAsMap();
        if (key.get() instanceof String) {
            records.remove((String) key.get());

        } else if (key.get() instanceof List<?>) {
            List<String> keys = (List<String>) key.get();
            query(records, keys, (map, k) -> map.remove(k)).orElseThrow();

        } else {
            // array
            throw new IllegalArgumentException("Array not supported yet");
        }
        writeBackToDb(records);
    }

    @Override
    public T get(K key) {
        Map<String, Object> records = getDbContentAsMap();
        if (key.get() instanceof String) {
            return (T) records.getOrDefault((String) key.get(), null);

        } else if (key.get() instanceof List<?>) {
            List<String> keys = (List<String>) key.get();
            return (T) query(records, keys, (map, k) -> map.get(k)).orElse(null);

        } else {
            throw new IllegalArgumentException("key type is not supported: " + key.get().getClass());
        }
    }

    @Override
    public T update(K key, T entity) {
        Map<String, Object> records = getDbContentAsMap();

        if (key.get() instanceof String) {
            records.put((String) key.get(), entity);

        } else if (key.get() instanceof List<?>) {
            List<String> keys = (List<String>) key.get();
            query(records, keys, (map, k) -> map.put(k, entity));

        } else {
            throw new IllegalArgumentException("key type is not supported: " + key.get().getClass());
        }
        writeBackToDb(records);
        return entity;
    }

    private static Optional<Object> query(Map<String, Object> records,
                                          List<String> keys,
                                          BiFunction<Map<String, Object>, String, Object> onFound) {
        Map<String, Object> current = null;
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            Object next = current == null ? records.get(k) : current.get(k);
            if (i == keys.size() - 1) {
                return Optional.ofNullable(current == null ? records : current)
                        .map(map -> onFound.apply(map, k));
            }
            if (next instanceof Map) {
                // another branch
                current = (Map<String, Object>) next;
            } else {
                throw new IllegalArgumentException("Cannot go deeper because current map is not a Map: " + next);
            }
        }
        return Optional.empty();
    }

    private void writeBackToDb(Map<String, Object> records) {
        try(Writer writer = new FileWriter(file)) {
            gson.toJson(records, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getDbContentAsMap() {
        final Type type = new TypeToken<Map<String, Object>>() {}.getType();

        Map<String, Object> records;
        try (FileReader fileReader = new FileReader(file)) {
            records = gson.fromJson(fileReader, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (records == null) {
            records = new HashMap<>();
        }
        return records;
    }

    @Override
    public void validate(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
    }
}
