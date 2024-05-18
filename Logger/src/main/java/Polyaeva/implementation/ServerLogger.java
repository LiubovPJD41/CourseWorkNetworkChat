package Polyaeva.implementation;

import Polyaeva.LoggerBase;
import Polyaeva.LoggerInterface;

import java.nio.file.Files;
import java.nio.file.Path;

public class ServerLogger extends LoggerBase implements LoggerInterface {
    private static final String LOGGER_DIRECTORY = getLoggerDirectory();
    private static final String serverLogName = "serverLog.txt";
    private static ServerLogger serverLogger = null;

    private ServerLogger() {
        super(serverLogName);
    }

    public static ServerLogger createInstance() {
        if (serverLogger == null) {
            serverLogger = new ServerLogger();
        }

        if (Files.notExists(Path.of(LOGGER_DIRECTORY)))
            createDir(LOGGER_DIRECTORY);

        if (Files.notExists(Path.of(LOGGER_DIRECTORY + "/" + serverLogName)))
            createFile(LOGGER_DIRECTORY + "/" + serverLogName);

        return serverLogger;
    }
}
