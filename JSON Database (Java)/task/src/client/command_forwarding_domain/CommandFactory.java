package client.command_forwarding_domain;

import client.app.Configs;
import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class CommandFactory {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Command.Key.class, new Command.KeyConverter())
            .create();
    public static Optional<RunableCommand> from(String[] args, CommandForwarder commandForwarder) {
        Command command = new Command();

        // get general command
        JCommander.newBuilder()
                .addObject(command)
                .args(args).build();

        // get detailed command if any
        if (command.getFilePath() != null) {
            String pathname = String.valueOf(Paths.get(Configs.TEST_DATA_FOLDER, command.getFilePath()));
            try (FileReader fileReader = new FileReader(pathname)) {
                command = gson.fromJson(fileReader, Command.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        switch (command.getType()) {
            case "get", "set", "delete", "exit":
                return Optional.of(new RunableCommand(command, commandForwarder));
            default:
                return Optional.empty();
        }
    }

    public static class RunableCommand extends Command {
        CommandForwarder commandForwarder;

        RunableCommand(Command command, CommandForwarder commandForwarder) {
            super(command);
            this.commandForwarder = commandForwarder;
        }

        public String exec() {
            commandForwarder.send(this);
            return commandForwarder.receive();
        }
    }
}
