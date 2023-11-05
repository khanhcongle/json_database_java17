package server.data_access_domain.service_provider.builtin_service_provider;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

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