package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.Task;
import entity.TaskType;
import exceptions.TaskNotFoundException;
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

public class TasksHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    FileBackedTasksManager manager;

    public TasksHandler(FileBackedTasksManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();
        String query = httpExchange.getRequestURI().getQuery();
        Map<String, String> queryMap = queryMapper(query);

        switch (method) {
            case "GET":
                if (query == null) {
                    response = gson.toJson(manager.getTasks());
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    int id = Integer.parseInt(queryMap.get("id"));
                    if (gson.toJson(manager.findTasksById(id)).equals("null")) {
                        httpExchange.sendResponseHeaders(404, 0);
                    } else {
                        response = gson.toJson(manager.findTasksById(id));
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                }
                break;
            case "DELETE":
                if (query == null) {
                    manager.clearTasks();
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    int id = Integer.parseInt(queryMap.get("id"));
                    try {
                        manager.deleteTaskById(id);
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (TaskNotFoundException e) {
                        response = e.getMessage();
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                }
                break;
            case "POST":
                InputStream is = httpExchange.getRequestBody();
                String body = new String(is.readAllBytes(), DEFAULT_CHARSET);
                if (body.contains("\"id\"")) {
                    try {
                        Task task = gson.fromJson(body, Task.class);
                        task.setType(TaskType.TASK);
                        manager.updateTask(task);
                        httpExchange.sendResponseHeaders(200, 0);
                    } catch (TaskValidationException e) {
                        httpExchange.sendResponseHeaders(400, 0);
                        response = e.getMessage();
                    }
                } else {
                    try {
                        Task task = gson.fromJson(body, Task.class);
                        manager.createTask(task);
                        httpExchange.sendResponseHeaders(201, 0);
                    } catch (TaskValidationException e) {
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                }
                break;
        }
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
