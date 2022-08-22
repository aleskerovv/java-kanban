package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.FileBackedTasksManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

public class HistoryHandler implements HttpHandler {
    private final FileBackedTasksManager manager;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HistoryHandler(FileBackedTasksManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();
        httpExchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        switch (method) {
            case "GET":
                response = gson.toJson(manager.getHistory());
                httpExchange.sendResponseHeaders(200, 0);
                break;
            default:
                httpExchange.sendResponseHeaders(405, 0);
                break;
        }
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
