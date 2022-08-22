package http_server;

import com.sun.net.httpserver.HttpServer;
import handlers.*;
import manager.FileBackedTasksManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(FileBackedTasksManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new PrioritizedTasksHandler(manager));
        server.createContext("/tasks/task", new TasksHandler(manager));
        server.createContext("/tasks/epic", new EpicHandler(manager));
        server.createContext("/tasks/subtask", new SubTaskHandler(manager));
        server.createContext("/tasks/history", new HistoryHandler(manager));
    }

    public void start() {
        System.out.println("Запускаем TaskServer сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(1);
    }
}