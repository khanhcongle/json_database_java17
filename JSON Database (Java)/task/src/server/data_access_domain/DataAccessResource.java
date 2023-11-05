package server.data_access_domain;

import client.command_forwarding_domain.Command;
import server.data_access_domain.use_case.CommandHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class DataAccessResource<K extends Supplier & Comparable> implements CommandHandler {
    private final DataAccessService<K, Object> dataAccessService;

    public DataAccessResource(DataAccessService<K, Object> dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public CompletionStage<Object> handle(Command command) throws ExitRequestedException {
        K key = (K) command.getKey();

        switch (command.getType()) {
            case "get":
                return CompletableFuture.completedStage(dataAccessService.get(key));
            case "set":
                dataAccessService.set(key, command.getValue());
                break;
            case "delete":
                dataAccessService.delete(key);
                break;
            case "exit":
                throw new ExitRequestedException();
            default:
                throw new UnsupportedOperationException("unknown commandType: " + command.getType());
        }
        return CompletableFuture.completedStage(null);
    }

}
