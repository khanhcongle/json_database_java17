package server.app;

public class Configs {
    private Configs() {}
    private static final String FILENAME_TEST_ENVIRONMENT = System.getProperty("user.dir") + "/src/server/data/db.json";
    private static final String FILENAME_LOCAL_ENVIRONMENT = System.getProperty("user.dir") + "/JSON Database (Java)/task/src/server/data/db.json";
    public static final String DB_PATH = System.getenv("IS_LOCAL") != null ? FILENAME_LOCAL_ENVIRONMENT : FILENAME_TEST_ENVIRONMENT;
    public static final String WEB_SOCKET_ADDRESS = "127.0.0.1";
    public static final int WEB_SOCKET_PORT = 23456;
}
