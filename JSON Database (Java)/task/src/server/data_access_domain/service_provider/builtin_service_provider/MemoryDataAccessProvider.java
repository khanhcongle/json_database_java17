package server.data_access_domain.service_provider.builtin_service_provider;

import server.data_access_domain.service_provider.DataAccessProvider;

import java.util.Hashtable;
import java.util.function.Supplier;

public class MemoryDataAccessProvider<K extends Supplier & Comparable, T> implements DataAccessProvider<K, T> {
    private final Hashtable<String, T> data;

    public MemoryDataAccessProvider() {
        this.data = new Hashtable<>();
    }

    @Override
    public void delete(K key) {
        if (key.get() instanceof String) {
            data.remove((String) key.get());
        } else {
            throw new IllegalArgumentException("key as Array not supported yet");
        }
    }

    @Override
    public T get(K key) {
        if (key.get() instanceof String) {
            return data.get((String) key.get());
        } else {
            throw new IllegalArgumentException("key as Array not supported yet");
        }
    }

    @Override
    public T update(K key, T entity) {
        if (key.get() instanceof String) {
            return data.put((String) key.get(), entity);
        } else {
            throw new IllegalArgumentException("key as Array not supported yet");
        }
    }

    @Override
    public void validate(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
    }
}
