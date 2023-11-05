package server.data_access_domain.service_provider.builtin_service_provider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.app.GsonBean;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JsonFileDataAccessProvider<K extends Supplier & Comparable, T>
        extends MapLikeDataAccessProvider<K, T> {
    private final Gson gson = GsonBean.getInstance();
    private final File file;
    public JsonFileDataAccessProvider(String filePath) throws IOException {
        file = new File(filePath);
        System.out.println(file.getAbsolutePath());

        if (file.createNewFile()) {
            System.out.println("created new db file");
        } else {
            System.out.println("db file already exist");
        }
    }

    @Override
    protected void writeBackToDb(Map<String, Object> records) {
        try(Writer writer = new FileWriter(file)) {
            gson.toJson(records, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Map<String, Object> getDbContentAsMap() {
        final Type type = new TypeToken<Map<String, Object>>() {}.getType();

        Map<String, Object> records;
        try (FileReader fileReader = new FileReader(file)) {
            records = gson.fromJson(fileReader, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (records == null) {
            records = new HashMap<>();
        }
        return records;
    }

}
