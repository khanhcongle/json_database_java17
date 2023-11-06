package server;

import server.app.Configs;
import server.data_access_domain.service_provider.DataAccessProvider;
import server.data_access_domain.use_case.CommandHandler;
import server.app.WebSocketServer;
import server.data_access_domain.DataAccessService;
import server.data_access_domain.service_provider.builtin_service_provider.JsonFileDataAccessProvider;
import server.data_access_domain.DataAccessResource;

import java.io.IOException;
import java.util.function.Supplier;

public class Main {

    public static void main(String[] args) throws IOException {
        DataAccessProvider provider = new JsonFileDataAccessProvider<>(Configs.DB_PATH);
        DataAccessService service = new DataAccessService<>(provider);
        CommandHandler handler = new DataAccessResource<>(service);

        WebSocketServer.startServer(handler);
    }

}
