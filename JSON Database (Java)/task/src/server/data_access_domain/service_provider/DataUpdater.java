package server.data_access_domain.service_provider;

public interface DataUpdater<K, T> {
    T update(K key, T entity);
}
