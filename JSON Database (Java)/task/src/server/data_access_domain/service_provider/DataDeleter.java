package server.data_access_domain.service_provider;

public interface DataDeleter<T> {
    void delete(T index);
}
