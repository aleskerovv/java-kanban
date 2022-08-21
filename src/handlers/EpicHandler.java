package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.Epic;
import entity.TaskType;
import manager.FileBackedTasksManager;
import exceptions.TaskValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import static handlers.QueryMapper.queryMapper;

public class EpicHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final GsonBuilder gb = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
    private final Gson gson = gb.create();
    FileBackedTasksManager manager;

    public EpicHandler(FileBackedTasksManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response;
        String method = httpExchange.getRequestMethod();
        String query = httpExchange.getRequestURI().getQuery();
        Map<String, String> queryMap = queryMapper(query);

        switch (method) {
            case "GET":
                if (query == null) {
                    response = gson.toJson(manager.getEpics());
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    int id = Integer.parseInt(queryMap.get("id"));
                    try {
                        response = gson.toJson(manager.findEpicById(id));
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (NullPointerException e) {
                        response = "Epic with id " + id + " does not exists";
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                }
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                break;
            case "DELETE":
                if (query == null) {
                    manager.clearEpicsList();
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    int id = Integer.parseInt(queryMap.get("id"));
                    try {
                        manager.deleteEpicById(id);
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (NullPointerException e) {
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                }
                try (OutputStream os = httpExchange.getResponseBody()) {
                }
                break;
            case "POST":
                InputStream is = httpExchange.getRequestBody();
                String body = new String(is.readAllBytes(), DEFAULT_CHARSET);
                if (body.contains("\"id\"")) {
                    try {
                        Epic epic = gson.fromJson(body, Epic.class);
                        epic.setType(TaskType.EPIC);
                        manager.updateEpic(epic);
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (TaskValidationException e) {
                        httpExchange.sendResponseHeaders(400, 0);
                        response = e.getMessage();
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                } else {
                    try {
                        Epic epic = gson.fromJson(body, Epic.class);
                        manager.createEpic(epic);
                        httpExchange.sendResponseHeaders(201, 0);
                    } catch (TaskValidationException e) {
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                }
                try (OutputStream os = httpExchange.getResponseBody()) {
                }
                break;
        }
    }
}
