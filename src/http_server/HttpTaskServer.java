package http_server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import entity.Epic;
import entity.SubTask;
import entity.Task;
import entity.TaskType;
import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskValidationException;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static FileBackedTasksManager manager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer server;

    static {
        try {
            manager = Managers.loadFromFile(new File("httpManager.csv"));
            manager.createEntityFromText();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new PrioritizesTasksHandler());
        server.createContext("/tasks/task", new TasksHandler());
        server.createContext("/tasks/epic", new EpicHandler());
        server.createContext("/tasks/subtask", new SubTaskHandler());
        server.createContext("/tasks/history", new HistoryHandler());
    }

    static class PrioritizesTasksHandler implements HttpHandler {
        private final GsonBuilder gb = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        private final Gson gson = gb.create();

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;

            httpExchange.sendResponseHeaders(200, 0);
            response = gson.toJson(manager.getPrioritizedTasks());

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class TasksHandler implements HttpHandler {
        private final GsonBuilder gb = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        private final Gson gson = gb.create();

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
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
                        try {
                            response = gson.toJson(manager.findTasksById(id));
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (NullPointerException e) {
                            response = "Task with id " + id + " does not exists";
                            httpExchange.sendResponseHeaders(400, 0);

                        }
                    }
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.clearTasks();
                        httpExchange.sendResponseHeaders(200, 0);
//                        try (OutputStream os = httpExchange.getResponseBody()) {
//                        }
                    } else {
                        int id = Integer.parseInt(queryMap.get("id"));
                        try {
                            manager.deleteTaskById(id);
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                            }
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
                            Task task = gson.fromJson(body, Task.class);
                            task.setType(TaskType.TASK);
                            manager.updateTask(task);
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                            }
                        } catch (TaskValidationException e) {
                            httpExchange.sendResponseHeaders(400, 0);
                            response = e.getMessage();
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    } else {
                        try {
                            Task task = gson.fromJson(body, Task.class);
                            manager.createTask(task);
                            httpExchange.sendResponseHeaders(201, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                            }
                        } catch (TaskValidationException e) {
                            httpExchange.sendResponseHeaders(400, 0);
                            response = e.getMessage();
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }
                    break;
            }
        }
    }

    static class EpicHandler implements HttpHandler {
        private GsonBuilder gb = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        private final Gson gson = gb.create();

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
//                        try (OutputStream os = httpExchange.getResponseBody()) {
//                            os.write(response.getBytes());
//                        }
                    } else {
                        int id = Integer.parseInt(queryMap.get("id"));
                        try {
                            response = gson.toJson(manager.findEpicById(id));
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                                os.write(response.getBytes());
//                            }
                        } catch (NullPointerException e) {
                            response = "Epic with id " + id + " does not exists";
                            httpExchange.sendResponseHeaders(400, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                                os.write(response.getBytes());
//                            }
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
//                        try (OutputStream os = httpExchange.getResponseBody()) {
//                        }
                    } else {
                        int id = Integer.parseInt(queryMap.get("id"));
                        try {
                            manager.deleteEpicById(id);
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                            }
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
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                            }
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
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                            }
                        } catch (TaskValidationException e) {
                            httpExchange.sendResponseHeaders(400, 0);
                            response = e.getMessage();
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }
                    break;
            }
        }
    }

    static class SubTaskHandler implements HttpHandler {
        private GsonBuilder gb = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        private final Gson gson = gb.create();

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();
            Map<String, String> queryMap = queryMapper(query);

            switch (method) {
                case "GET":
                    String key = httpExchange.getRequestURI().getPath().substring("/epic/".length());
                    if (query == null) {
                        response = gson.toJson(manager.getSubtasks());
                        httpExchange.sendResponseHeaders(200, 0);
//                        try (OutputStream os = httpExchange.getResponseBody()) {
//                            os.write(response.getBytes());
//                        }
                    } else if (key.contains("epic")) {
                        int id = Integer.parseInt(queryMap.get("id"));
                        try {
                            response = gson.toJson(manager.getEpicsSubTasks(id));
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                                os.write(response.getBytes());
//                            }
                        } catch (NullPointerException e) {
                            response = "Subtask with id " + id + " does not exists";
                            httpExchange.sendResponseHeaders(400, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                                os.write(response.getBytes());
//                            }
                        }
                    } else {
                        int id = Integer.parseInt(queryMap.get("id"));
                        try {
                            response = gson.toJson(manager.findSubTasksById(id));
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                                os.write(response.getBytes());
//                            }
                        } catch (NullPointerException e) {
                            response = "Subtask with id " + id + " does not exists";
                            httpExchange.sendResponseHeaders(400, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                                os.write(response.getBytes());
//                            }
                        }
                    }
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.clearSubtasksList();
                        httpExchange.sendResponseHeaders(200, 0);
//                        try (OutputStream os = httpExchange.getResponseBody()) {
//                        }
                    } else {
                        int id = Integer.parseInt(queryMap.get("id"));
                        try {
                            manager.deleteSubTaskById(id);
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                            }
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
                            SubTask subTask = gson.fromJson(body, SubTask.class);
                            subTask.setType(TaskType.SUBTASK);
                            manager.updateSubTask(subTask);
                            httpExchange.sendResponseHeaders(200, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                            }
                        } catch (TaskValidationException e) {
                            httpExchange.sendResponseHeaders(400, 0);
                            response = e.getMessage();
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    } else {
                        try {
                            SubTask subTask = gson.fromJson(body, SubTask.class);
                            manager.createSubtask(subTask);
                            httpExchange.sendResponseHeaders(201, 0);
//                            try (OutputStream os = httpExchange.getResponseBody()) {
//                            }
                        } catch (TaskValidationException e) {
                            httpExchange.sendResponseHeaders(400, 0);
                            response = e.getMessage();
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    }
                    break;
            }
        }
    }

    static class HistoryHandler implements HttpHandler {
        private final GsonBuilder gb = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        private final Gson gson = gb.create();

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
                    httpExchange.sendResponseHeaders(404, 0);
//                    try (OutputStream os = httpExchange.getResponseBody()) {
//                    }
                    break;
            }
        }
    }

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime != null) {
                jsonWriter.value(localDateTime.format(formatter));
            } else {
                jsonWriter.nullValue();
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), formatter);
        }
    }

    //Проверка параметров строки запроса
    private static Map<String, String> queryMapper(String query) {
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
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