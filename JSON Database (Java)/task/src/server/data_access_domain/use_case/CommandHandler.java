package server.data_access_domain.use_case;

import client.command_forwarding_domain.Command;
import server.data_access_domain.ExitRequestedException;

import java.util.concurrent.CompletionStage;

public interface CommandHandler {
    CompletionStage<Object> handle(Command command) throws ExitRequestedException;
}
