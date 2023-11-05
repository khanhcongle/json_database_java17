package server.data_access_domain.service_provider;


import server.data_access_domain.service_provider.DataDeleter;
import server.data_access_domain.service_provider.DataGetter;
import server.data_access_domain.service_provider.DataUpdater;
import server.data_access_domain.service_provider.KeyValidator;

public interface DataAccessProvider<K extends Comparable, T> extends
        DataGetter<K, T>,
        DataUpdater<K, T>,
        DataDeleter<K>,
        KeyValidator<K> {
}
