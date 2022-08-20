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
import manager.Managers;
import org.junit.jupiter.api.*;

import java.io.File;
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
import java.util.Map;

import static entity.TaskStatus.IN_PROGRESS;
import static entity.TaskStatus.NEW;
import static java.lang.Thread.sleep;

class HttpTaskServerTest {
    HttpClient client = HttpClient.newHttpClient();
    URI taskUri = URI.create("http://localhost:8080/tasks/task");
    URI epicUri = URI.create("http://localhost:8080/tasks/epic");
    URI subtaskUri = URI.create("http://localhost:8080/tasks/subtask");
    Task task;
    Epic epic;
    SubTask subTask;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @BeforeAll
    static void startServer() throws IOException, InterruptedException {
        new HttpTaskServer().start();
        sleep(1000);
    }

    @AfterAll
    static void stopServer() throws IOException {
        new HttpTaskServer().stop();
    }

    @BeforeEach
    void removeAllTasks() {
        HttpRequest request1 = HttpRequest.newBuilder().uri(taskUri).DELETE().build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(epicUri).DELETE().build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(subtaskUri).DELETE().build();
        client.sendAsync(request1, HttpResponse.BodyHandlers.ofString());
        client.sendAsync(request2, HttpResponse.BodyHandlers.ofString());
        client.sendAsync(request3, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void shouldReturnTasksList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(taskUri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<Task>>() {}.getType();
        List<Task> fromJson = gson.fromJson(response.body(), listType);
        List<Task> listOf = List.of(task);

        Assertions.assertEquals(fromJson, listOf);
    }

    @Test
    void shouldReturnEpicList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(epicUri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<Epic>>() {}.getType();
        List<Epic> fromJson = gson.fromJson(response.body(), listType);
        List<Task> listOf = List.of(epic);

        Assertions.assertEquals(fromJson, listOf);
    }

//    @Test
//    void shouldReturnSubTaskList() throws IOException, InterruptedException {
//        HttpRequest request = HttpRequest.newBuilder().uri(subtaskUri).GET().build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        Type listType = new TypeToken<List<SubTask>>() {}.getType();
//        List<SubTask> fromJson = gson.fromJson(response.body(), listType);
//        subTask.setId(3);
//        List<Task> listOf = List.of(subTask);
//
//        Assertions.assertEquals(fromJson, listOf);
//    }

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


