package Polyaeva.implementation;

import Polyaeva.LoggerBase;
import Polyaeva.LoggerInterface;

import java.nio.file.Files;
import java.nio.file.Path;

public class ClientLogger extends LoggerBase implements LoggerInterface {
    private static final String LOGGER_DIRECTORY = getLoggerDirectory();
    private static final String clientLogName = "clientLog.txt";
    private static ClientLogger clientLogger = null;

    private ClientLogger() {
        super(clientLogName);
    }

    public synchronized static ClientLogger createInstance() {
        if (clientLogger == null) {
            clientLogger = new ClientLogger();
        }

        if (Files.notExists(Path.of(LOGGER_DIRECTORY)))
            createDir(LOGGER_DIRECTORY);

        if (Files.notExists(Path.of(LOGGER_DIRECTORY + "/" + clientLogName)))
            createFile(LOGGER_DIRECTORY + "/" + clientLogName);

        return clientLogger;
    }
}