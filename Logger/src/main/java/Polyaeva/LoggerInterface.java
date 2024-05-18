package Polyaeva;

import Polyaeva.implementation.ClientLogger;
import Polyaeva.implementation.ServerLogger;
import org.jetbrains.annotations.NotNull;


public interface LoggerInterface {
    @NotNull
    static LoggerBase getLogger(@NotNull LoggerType type) {
        return switch (type) {
            case CLIENT -> ClientLogger.createInstance();
            case SERVER -> ServerLogger.createInstance();
        };
    }
}
