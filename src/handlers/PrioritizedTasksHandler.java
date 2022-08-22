package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.FileBackedTasksManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

public class PrioritizedTasksHandler implements HttpHandler {
    private final GsonBuilder gb = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
    private final Gson gson = gb.create();
    private final FileBackedTasksManager manager;

    public PrioritizedTasksHandler(FileBackedTasksManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET":
                httpExchange.sendResponseHeaders(200, 0);
                response = gson.toJson(manager.getPrioritizedTasks());
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
