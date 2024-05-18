package Polyaeva;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class LoggerBase implements LoggerInterface {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd ' ' hh:mm:ss a zzz ");
    private final String loggerFile;

    public LoggerBase(@NotNull String loggerFile) {
        this.loggerFile = loggerFile;
    }

    public static void createDir(@NotNull String dirname) {
        new File(dirname).mkdir();
    }

    public static void createFile(@NotNull String filename) {
        File file = new File(filename);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            System.out.println("Unable to create file " + filename + ": " + ex.getMessage());
        }
    }

    public static String getLoggerDirectory() {
        return "LogFiles";
    }

    public StringBuilder log(@NotNull String connection, @NotNull String msg) {
        StringBuilder line = new StringBuilder();
        line.append(dateFormat.format(new Date()))
                .append(connection)
                .append(" sent message: ")
                .append(msg);
        return line;
    }

    public void logFileSave(@NotNull StringBuilder line) {
        try (FileWriter writer = new FileWriter(getLoggerDirectory() + "\\" + this.loggerFile, true)) {
            writer.write(String.valueOf(line));
            writer.append('\n');
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}