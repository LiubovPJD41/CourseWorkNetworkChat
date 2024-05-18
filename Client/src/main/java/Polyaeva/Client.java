package Polyaeva;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Client implements ConnectionEventsManager {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final Scanner SCANNER = new Scanner(System.in);
    private final LoggerBase clientLogger = LoggerInterface.getLogger(LoggerType.CLIENT);

    private Client(String nickname) {
        try {
            String settingsFilename = "settingsClient.txt";
            if (Files.notExists(Path.of(settingsFilename))) {
                throw new FileNotFoundException("No settings file was found");
            }

            Connection connection = new Connection(this, IP_ADDRESS, Connection.getPort(settingsFilename), nickname);
            connection.sendString("CONF" + nickname);

            while (true) {
                String msg = SCANNER.nextLine();
                if (msg.equalsIgnoreCase("exit")) {
                    connection.disconnect();
                    break;
                }
                connection.sendString(msg);
                this.clientLogger.logFileSave(this.clientLogger.log(connection.toString(), msg));
            }

        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Enter your nickname:");
        String nickname = SCANNER.nextLine();
        System.out.println("Creating connection...");
        new Client(nickname);
    }

    @Override
    public void onConnectionReady(@NotNull Connection connection) {
        printMessage("Connection is ready!");
    }

    @Override
    public void onReceiveMessage(@NotNull Connection connection, @NotNull String message) {
        this.printMessage(message);
        this.clientLogger.logFileSave(this.clientLogger.log(connection.toString(), message));
    }

    @Override
    public void onDisconnect(@NotNull Connection connection) {
        printMessage("Connection closed");
    }

    @Override
    public void onException(@NotNull Connection connection, @NotNull Exception e) {
        printMessage("Connection exception: " + e);
    }

    private void printMessage(String message) {
        System.out.println(message);
    }

}
