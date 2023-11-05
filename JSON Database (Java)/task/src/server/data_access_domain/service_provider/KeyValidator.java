package server.data_access_domain.service_provider;

public interface KeyValidator<T extends Comparable> {
    void validate(T index);
}
