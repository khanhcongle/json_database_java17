package server;

import server.app.Configs;
import server.data_access_domain.use_case.CommandHandler;
import server.app.WebSocketServer;
import server.data_access_domain.DataAccessService;
import server.data_access_domain.service_provider.builtin_service_provider.JsonFileDataAccessProvider;
import server.data_access_domain.DataAccessResource;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        CommandHandler handler =
                new DataAccessResource<>( new DataAccessService<>( new JsonFileDataAccessProvider<>(Configs.DB_PATH)));

        WebSocketServer.startServer(handler);
    }

}
