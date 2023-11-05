package client.app;

public class Configs {
    private static final String FOLDER_TEST_ENVIRONMENT = System.getProperty("user.dir") + "/src/client/data";
    private static final String FOLDER_LOCAL_ENVIRONMENT = System.getProperty("user.dir") + "/JSON Database (Java)/task/src/client/data";
    public static final String TEST_DATA_FOLDER = System.getenv("IS_LOCAL") != null ? FOLDER_LOCAL_ENVIRONMENT : FOLDER_TEST_ENVIRONMENT;
}
