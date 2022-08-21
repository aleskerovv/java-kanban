package manager;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import entity.Epic;
import entity.SubTask;
import entity.Task;
import kv_task_client.KvTaskClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    ManagerToJson mtj;
    String url;
    KvTaskClient client;
    Gson gson;
    String json;
    static final String KEY = "taskManager";

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        mtj = new ManagerToJson();
        this.url = url;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(HistoryManager.class, new HistoryManagerAdapter())
                .create();
        this.client = new KvTaskClient(url);
    }

    @Override
    protected void save() {
        this.fillPipeline();
        json = gson.toJson(mtj);
        client.put(KEY, json);
    }

    private void fillPipeline() {
        List<Integer> history = new ArrayList<>();
        this.getHistory()
                .stream()
                .mapToInt(Task::getId)
                .forEach(history::add);
        mtj.setTasks(this.getTasks());
        mtj.setEpics(this.getEpics());
        mtj.setSubTasks(this.getSubtasks());
        mtj.setHistory(history);
    }

    public HttpTaskManager loadFromServer(String key) throws IOException, InterruptedException {
        HttpTaskManager httpManager = Managers.getDefault(url);
        String json = client.load(key);

        JsonElement jsonElement = JsonParser.parseString(json);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        JsonObject source = jsonElement.getAsJsonObject();

        JsonArray tasks = source.get("tasks").getAsJsonArray();
        tasks.forEach(e -> {
            JsonObject jsonTask = e.getAsJsonObject();
            Task task = gson.fromJson(jsonTask, Task.class);
            httpManager.tasks.put(task.getId(), task);
            httpManager.taskManager.put(task.getId(), task);
            httpManager.sortedTasks.add(task);
        });

        JsonArray epics = source.get("epics").getAsJsonArray();
        epics.forEach(e -> {
            JsonObject jsonEpic = e.getAsJsonObject();
            Epic epic = gson.fromJson(jsonEpic, Epic.class);
            httpManager.epics.put(epic.getId(), epic);
            httpManager.taskManager.put(epic.getId(), epic);
        });

        JsonArray subTasks = source.get("subTasks").getAsJsonArray();
        subTasks.forEach(e -> {
            JsonObject jsonSubTask = e.getAsJsonObject();
            SubTask subTask = gson.fromJson(jsonSubTask, SubTask.class);
            httpManager.subTasks.put(subTask.getId(), subTask);
            httpManager.taskManager.put(subTask.getId(), subTask);
            httpManager.sortedTasks.add(subTask);
        });

        JsonArray history = source.get("history").getAsJsonArray();

        for (int i = 0; i < history.size(); i++) {
            int id = history.get(i).getAsInt();
            if (httpManager.tasks.containsKey(id)) {
                httpManager.historyManager.addTask(httpManager.tasks.get(id));
            } else if (httpManager.subTasks.containsKey(id)) {
                httpManager.historyManager.addTask(httpManager.subTasks.get(id));
            } else if (httpManager.epics.containsKey(id)) {
                httpManager.historyManager.addTask(httpManager.epics.get(id));
            }
        }
        return httpManager;
    }

    static class HistoryManagerAdapter extends TypeAdapter<HistoryManager> {
        @Override
        public void write(JsonWriter jsonWriter, HistoryManager manager) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("HISTORY");
            jsonWriter.beginArray();

            manager.getHistory().stream()
                    .mapToInt(Task::getId)
                    .forEach(value -> {
                        try {
                            jsonWriter.value(value);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

            jsonWriter.endArray();
            jsonWriter.endObject();
        }

        @Override
        public HistoryManager read(JsonReader jsonReader) throws IOException {
            return null;
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
}