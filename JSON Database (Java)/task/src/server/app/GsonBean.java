package server.app;

import client.command_forwarding_domain.Command;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonBean {
    private static Gson gson;
    public static Gson getInstance() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Command.Key.class, new Command.KeyConverter())
                    .create();
        }
        return gson;
    }
}
