package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http_server.HttpTaskServer;
import manager.FileBackedTasksManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

public class HistoryHandler implements HttpHandler {
    FileBackedTasksManager manager;
    private final GsonBuilder gb = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
    private final Gson gson = gb.create();

    public HistoryHandler(FileBackedTasksManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response;
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                response = gson.toJson(manager.getHistory());
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                break;
            default:
                httpExchange.sendResponseHeaders(405, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                }
                break;
        }
    }
}
