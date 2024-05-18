package Polyaeva;

import org.jetbrains.annotations.NotNull;

public interface ConnectionEventsManager {
    void onConnectionReady(@NotNull Connection connection);

    void onReceiveMessage(@NotNull Connection connection, @NotNull String message);

    void onDisconnect(@NotNull Connection connection);

    void onException(@NotNull Connection connection, @NotNull Exception e);
}