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
import org.junit.jupiter.api.*;

import java.io.IOException;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static entity.TaskStatus.IN_PROGRESS;
import static entity.TaskStatus.NEW;

class HttpTaskServerTest {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();;
    HttpClient client = HttpClient.newHttpClient();
    Task task = new Task("Task", "desc", IN_PROGRESS, 500, LocalDateTime.parse("2022-09-01T12:00:00"));
    Epic epic = new Epic("Epic", "desc");
    SubTask subTask = new SubTask("SubTask", "desc", NEW, 250, LocalDateTime.parse("2022-10-01T12:00:00"), 2);
    URI taskUri = URI.create("http://localhost:8080/tasks/task");
    URI epicUri = URI.create("http://localhost:8080/tasks/epic");
    URI subtaskUri = URI.create("http://localhost:8080/tasks/subtask");

    @BeforeAll
    static void start() throws IOException {
        new HttpTaskServer().start();
    }

    @BeforeEach
    void create() throws IOException, InterruptedException {
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(subtaskUri).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    void delete() throws IOException, InterruptedException {
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(taskUri).DELETE().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(epicUri).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(subtaskUri).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void shouldReturnTask() throws IOException, InterruptedException {
        URI toTest = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(toTest).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task fromJson = gson.fromJson(response.body(), Task.class);
        task.setId(1);

        Assertions.assertEquals(task, fromJson);
    }

    @Test
    void shouldReturnTasksList() throws IOException, InterruptedException {
        URI toTest = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(toTest).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<Task>>() {}.getType();
        List<Task> fromJson = gson.fromJson(response.body(), listType);
        task.setId(1);
        List<Task> listOf = List.of(task);

        Assertions.assertEquals(fromJson, listOf);
    }

    @Test
    void shouldReturnEpic() throws IOException, InterruptedException {
        URI toTest = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(toTest).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic fromJson = gson.fromJson(response.body(), Epic.class);
        epic.setId(2);
        epic.addSubtasksId(3);
        epic.setDuration(250);
        epic.setStartTime(LocalDateTime.parse("2022-10-01T12:00"));
        epic.setEndTime(LocalDateTime.parse("2022-10-01T16:10"));

        Assertions.assertEquals(epic, fromJson);
    }

    @Test
    void shouldReturnSubTask() throws IOException, InterruptedException {
        URI toTest = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(toTest).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask fromJson = gson.fromJson(response.body(), SubTask.class);
        subTask.setId(3);

        Assertions.assertEquals(subTask, fromJson);
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


