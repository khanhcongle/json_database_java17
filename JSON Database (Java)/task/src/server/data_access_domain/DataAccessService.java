package server.data_access_domain;

import server.data_access_domain.service_provider.*;

import java.util.function.Supplier;

public class DataAccessService<K extends Supplier & Comparable, T> {
    DataGetter<K, T> getter;
    KeyValidator<K> keyValidator;
    DataUpdater<K, T> updater;
    DataDeleter<K> deleter;

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
            return null;
        }
        return value;
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
