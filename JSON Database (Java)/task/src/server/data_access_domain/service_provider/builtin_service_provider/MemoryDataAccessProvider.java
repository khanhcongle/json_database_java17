package server.data_access_domain.service_provider.builtin_service_provider;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Supplier;

public class MemoryDataAccessProvider<K extends Supplier & Comparable, T>
        extends MapLikeDataAccessProvider<K, T> {
    private Hashtable<String, T> data = new Hashtable<>();

    @Override
    protected Map<String, Object> getDbContentAsMap() {
        return (Map<String, Object>) this.data;
    }

    @Override
    protected void writeBackToDb(Map<String, Object> updatedData) {
        this.data = (Hashtable<String, T>) updatedData;
    }

}
