package server.data_access_domain.service_provider.builtin_service_provider;

import server.data_access_domain.service_provider.DataAccessProvider;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MapLikeDataAccessProvider<K extends Supplier & Comparable, T>
        implements DataAccessProvider<K, T> {
    @Override
    public void delete(K key) {
        updateThenSave(records -> {
            if (key.get() instanceof String) {
                records.remove((String) key.get());

            } else if (key.get() instanceof List<?>) {
                List<String> keys = (List<String>) key.get();
                MapQueryUtils.query(records, keys, (map, k) -> map.remove(k)).orElseThrow();

            } else {
                // array
                throw new IllegalArgumentException("Array not supported yet");
            }
            return null;
        });
    }

    private synchronized Object updateThenSave(Function<Map<String, Object>, Object> process) {
        Map<String, Object> records = getDbContentAsMap();
        Object result = process.apply(records);
        writeBackToDb(records);
        return result;
    }

    @Override
    public T get(K key) {
        Map<String, Object> records = getDbContentAsMap();
        if (key.get() instanceof String) {
            return (T) records.getOrDefault((String) key.get(), null);

        } else if (key.get() instanceof List<?>) {
            List<String> keys = (List<String>) key.get();
            return (T) MapQueryUtils.query(records, keys, (map, k) -> map.get(k)).orElse(null);

        } else {
            throw new IllegalArgumentException("key type is not supported: " + key.get().getClass());
        }
    }
    @Override
    public T update(K key, T entity) {
        updateThenSave(records -> {
            if (key.get() instanceof String) {
                return records.put((String) key.get(), entity);

            } else if (key.get() instanceof List<?>) {
                List<String> keys = (List<String>) key.get();
                return MapQueryUtils.query(records, keys, (map, k) -> map.put(k, entity));

            } else {
                throw new IllegalArgumentException("key type is not supported: " + key.get().getClass());
            }
        });
        return this.get(key);
    }

    @Override
    public void validate(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
    }

    protected abstract Map<String, Object> getDbContentAsMap();

    protected abstract void writeBackToDb(Map<String, Object> records);
    class MapQueryUtils {
        static Optional<Object> query(Map<String, Object> records,
                                      List<String> keys,
                                      BiFunction<Map<String, Object>, String, Object> action) {
            Map<String, Object> current = null;
            for (int i = 0; i < keys.size(); i++) {
                String k = keys.get(i);
                Object next = current == null ? records.get(k) : current.get(k);
                if (i == keys.size() - 1) {
                    return Optional.ofNullable(current == null ? records : current)
                            .map(map -> action.apply(map, k));
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
    }
}
