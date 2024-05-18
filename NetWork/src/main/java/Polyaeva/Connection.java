package Polyaeva;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class Connection {
    private final Socket socket;
    private final Thread thread;
    private final ConnectionEventsManager eventsManager;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private String nickname;

    public Connection(@NotNull ConnectionEventsManager eventsManager, @NotNull String ipAddress, int port, @NotNull String nickname) throws IOException {
        this(eventsManager, new Socket(ipAddress, port), nickname);
    }

    public Connection(@NotNull ConnectionEventsManager eventsManager, @NotNull Socket socket) throws IOException {
        this(eventsManager, socket, null);
    }

    public Connection(@NotNull ConnectionEventsManager eventsManager, @NotNull Socket socket, @Nullable String nickname) throws IOException {
        this.nickname = nickname;
        this.eventsManager = eventsManager;
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.thread = new Thread(new Runnable() {
            public void run() {
                try {
                    eventsManager.onConnectionReady(Connection.this);
                    while (!thread.isInterrupted()) {
                        String line = reader.readLine();
                        if (line != null)
                            eventsManager.onReceiveMessage(Connection.this, line);
                    }
                } catch (IOException e) {
                    eventsManager.onException(Connection.this, e);
                } finally {
                    eventsManager.onDisconnect(Connection.this);
                }
            }
        });
        thread.start();
    }

    public static int getPort(@NotNull String settingsFilename) {
        int port = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(settingsFilename))) {
            port = Integer.parseInt(br.readLine());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return port;
    }

    public synchronized void sendString(@NotNull String message) {
        try {
            if (this.nickname != null)
                this.writer.write(this.nickname + ": " + message + "\r\n");
            this.writer.flush();
        } catch (IOException e) {
            this.eventsManager.onException(Connection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventsManager.onException(Connection.this, e);
        }
    }

    public @Nullable String getNickname() {
        return this.nickname;
    }

    public void setNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        if (this.nickname == null) {
            return this.socket.getInetAddress() + ":" + this.socket.getPort();
        } else {
            return nickname + " (" + socket.getInetAddress() + ":" + socket.getPort() + ")";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection connection = (Connection) o;
        return this.thread.equals(connection.thread);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thread);
    }
}
