package Polyaeva;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


public class Server implements ConnectionEventsManager {
    private static final LoggerBase LOGGER = LoggerInterface.getLogger(LoggerType.SERVER);
    static ArrayList<Connection> actualConnections = new ArrayList<>();

    private Server() {
        String settingsFilename = "settingsServer.txt";
        if (Files.notExists(Path.of(settingsFilename))) {
            throw new RuntimeException("No settings file was found");
        }

        System.out.println("Server started!");
        try (ServerSocket serverSocket = new ServerSocket(Connection.getPort(settingsFilename))) {
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("Connection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    @Override
    public synchronized void onConnectionReady(@NotNull Connection connection) {
        actualConnections.add(connection);
        for (Connection connect : actualConnections) {
            if (!connect.equals(connection))
                connect.sendString("New connection: " + connection);
        }
    }

    @Override
    public synchronized void onReceiveMessage(@NotNull Connection connection, @NotNull String message) {
        if (connection.getNickname() == null) {
            connection.setNickname(message.substring(0, message.indexOf(":")));
            return;
        }

        for (Connection connect : actualConnections)
            if (!connect.equals(connection))
                connect.sendString(message);

        StringBuilder logLine = LOGGER.log(connection.toString(), message);
        System.out.println(logLine);
        LOGGER.logFileSave(logLine);
    }

    @Override
    public synchronized void onDisconnect(@NotNull Connection connection) {
        actualConnections.remove(connection);
        sendToUsers("Client disconnected: " + connection);
    }

    @Override
    public synchronized void onException(@NotNull Connection connection, @NotNull Exception e) {
        System.out.println("Connection exception: " + e);
    }

    private void sendToUsers(@NotNull String string) {
        for (Connection connection : actualConnections)
            connection.sendString(string);
    }
}
