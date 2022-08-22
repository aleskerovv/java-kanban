import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import entity.Epic;
import entity.SubTask;
import entity.Task;
import http_server.HttpTaskServer;
import manager.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static entity.TaskStatus.IN_PROGRESS;
import static entity.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {
    HttpClient client = HttpClient.newHttpClient();
    FileBackedTasksManager manager = new FileBackedTasksManager(new File("testing.csv"));
    HttpTaskServer server = new HttpTaskServer(manager);
    HttpRequest request;
    HttpResponse<String> response;

    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        server.start();
    }

    @AfterEach
    public void afterEach() {
        server.stop();
        manager.clearEpicsList();
        manager.clearTasks();
        manager.clearSubtasksList();
    }

    @Test
    void shouldDeleteAllTasks() throws InterruptedException, IOException {
        Task task = new Task("task", "test", NEW, 50);
        manager.createTask(task);

        URI url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Запрос удаление вернул не верный код.");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("[]", response.body(), "Запрос вернул не пустой список.");
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException {
        Task task = new Task("task", "test", NEW, 50);
        URI url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        URI taskById = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder().uri(taskById).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task fromJson = gson.fromJson(response.body(), Task.class);
        assertEquals(manager.findTasksById(1), fromJson);
    }

    @Test
    void shouldReturnHistory() throws IOException, InterruptedException {
        Task task = new Task("task", "test", NEW, 50);
        manager.createTask(task);
        manager.findTasksById(1);

        URI url = URI.create("http://localhost:8080/tasks/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type listType = new TypeToken<List<Task>>() {}.getType();
        List<Task> fromJson = gson.fromJson(response.body(), listType);
        assertEquals(manager.getHistory(), fromJson);
    }

    @Test
    void shouldCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("test epic", "epic's desc");
        URI url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        URI epicById = URI.create("http://localhost:8080/tasks/epic/?id=1");
        request = HttpRequest.newBuilder().uri(epicById).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic fromJson = gson.fromJson(response.body(), Epic.class);
        assertEquals(manager.findEpicById(1), fromJson);
    }

    @Test
    void shouldCreateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("test epic", "epic's desc");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("testSubtask", "subtask desc", NEW, 50
                , LocalDateTime.parse("2022-09-01T12:00"), 1);
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        URI subTaskById = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        request = HttpRequest.newBuilder().uri(subTaskById).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask fromJson = gson.fromJson(response.body(), SubTask.class);
        assertEquals(manager.findSubTasksById(2), fromJson);
    }

    @Test
    void shouldReturnPrioritizedTaskList() throws IOException, InterruptedException {
        Task task = new Task("task", "test", NEW, 50, LocalDateTime.parse("2022-09-02T15:00"));
        Epic epic = new Epic("test epic", "epic's desc");
        SubTask subTask = new SubTask("testSubtask", "subtask desc", NEW, 50
                , LocalDateTime.parse("2022-09-01T12:00"), 2);
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subTask);
        URI url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type listType = new TypeToken<List<Task>>() {}.getType();
        List<Task> fromJson = gson.fromJson(response.body(), listType);

        assertEquals(manager.getPrioritizedTasks().size(), fromJson.size());
        assertNotNull(fromJson);
    }

    @Test
    void shouldReturnEpicsSubtasksList() throws IOException, InterruptedException {
        Epic epic = new Epic("test epic", "epic's desc");
        SubTask subTask = new SubTask("testSubtask", "subtask desc", NEW, 50
                , LocalDateTime.parse("2022-09-01T12:00"), 1);
        manager.createEpic(epic);
        manager.createSubtask(subTask);

        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type listType = new TypeToken<List<Task>>() {}.getType();
        List<Task> fromJson = gson.fromJson(response.body(), listType);

        assertEquals(manager.getEpicsSubTasks(1).size(), fromJson.size());
        assertNotNull(fromJson);
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("task", "test", NEW, 50, LocalDateTime.parse("2022-09-02T15:00"));
        manager.createTask(task);

        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        task.setStatus(IN_PROGRESS);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task fromJson = gson.fromJson(response.body(), Task.class);

        assertEquals(IN_PROGRESS, fromJson.getStatus());
    }

    @Test
    void shouldUpdateEpicAndSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("test epic", "epic's desc");
        SubTask subTask = new SubTask("testSubtask", "subtask desc", NEW, 50
                , LocalDateTime.parse("2022-09-01T12:00"), 1);
        manager.createEpic(epic);
        manager.createSubtask(subTask);

        URI epicUri = URI.create("http://localhost:8080/tasks/epic/?id=1");
        URI subtaskUri = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        subTask.setStatus(IN_PROGRESS);
        request = HttpRequest.newBuilder()
                .uri(subtaskUri).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(epicUri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromJson = gson.fromJson(response.body(), Epic.class);
        request = HttpRequest.newBuilder().uri(subtaskUri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask subTaskFromJson = gson.fromJson(response.body(), SubTask.class);

        assertEquals(IN_PROGRESS, epicFromJson.getStatus());
        assertEquals(IN_PROGRESS, subTaskFromJson.getStatus());
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
}