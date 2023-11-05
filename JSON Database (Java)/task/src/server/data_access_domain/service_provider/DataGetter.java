package server.data_access_domain.service_provider;

public interface DataGetter<T, R> {
    R get(T index);
}
