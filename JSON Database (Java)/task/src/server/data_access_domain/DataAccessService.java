package server.data_access_domain;

import server.data_access_domain.service_provider.DataAccessProvider;
import server.data_access_domain.service_provider.KeyValidator;
import server.data_access_domain.service_provider.DataDeleter;
import server.data_access_domain.service_provider.DataGetter;
import server.data_access_domain.service_provider.DataUpdater;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataAccessService<K extends Supplier & Comparable, T> {
    DataGetter<K, T> getter;
    KeyValidator<K> keyValidator;
    DataUpdater<K, T> updater;
    DataDeleter<K> deleter;
    Consumer<String> onError;

    public DataAccessService(DataAccessProvider<K, T> dataAdapter) {
        this.keyValidator = dataAdapter;
        this.getter = dataAdapter;
        this.updater = dataAdapter;
        this.deleter = dataAdapter;
    }
    public T get(K key) {
        validateKey(key);
        T value = this.getter.get(key);
        if (value == null) {
            onError("No such key");
            return null;
        }
        return value;
    }

    private void onError(String message) {
        if (onError != null) {
            onError.accept(message);
        }
    }

    private void validateKey(K key) {
        keyValidator.validate(key);
    }

    public void set(K key, T newValue) {
        validateKey(key);
        updater.update(key, newValue);
    }

    public void delete(K key) {
        if (!isExist(key)) {
            onError("No such key");
            return;
        }
        deleter.delete(key);
    }

    private boolean isExist(K key) {
        validateKey(key);

        T value = this.getter.get(key);
        return value != null;
    }
}
