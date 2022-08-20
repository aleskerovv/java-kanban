import entity.Epic;
import entity.SubTask;
import entity.Task;
import kv_server.KVServer;
import manager.HttpTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.LocalDateTime;

import static entity.TaskStatus.DONE;
import static entity.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<TaskManager> {
    public HttpTaskManagerTest() throws IOException {
        manager = Managers.getDefault("http://localhost:8078");
    }
    @BeforeAll
    static void start() throws IOException {
        new KVServer().start();
    }

    @Test
    void shouldReturnHttpManagerState() throws IOException {
        //creating new manager by URL
        HttpTaskManager newManager = Managers.getDefault("http://localhost:8078");
        newManager.createTask(task1);
        //load manager state by httpManager method
        HttpTaskManager test = newManager.loadFromServer("taskManager");

        assertEquals(test.findTasksById(1), newManager.findTasksById(1));
        assertEquals(test.getTasks(), newManager.getTasks());
        assertEquals(test.getAllTaskList(), newManager.getAllTaskList());
    }

    @Test
    void shouldReturnEpicsList() throws IOException {
        //creating new manager by URL
        HttpTaskManager newManager = Managers.getDefault("http://localhost:8078");
        newManager.createEpic(epic1);
        newManager.createEpic(epic2);
        //load manager state by httpManager method
        HttpTaskManager test = newManager.loadFromServer("taskManager");

        assertEquals(test.getEpics(), newManager.getEpics());
        assertEquals(test.getAllTaskList(), newManager.getAllTaskList());
    }
}
