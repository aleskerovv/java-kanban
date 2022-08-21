import kv_server.KVServer;
import manager.HttpTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<TaskManager> {
    public HttpTaskManagerTest() throws IOException, InterruptedException {
        super.manager = Managers.getDefault("http://localhost:8078");
    }
    @BeforeAll
    static void start() throws IOException {
        new KVServer().start();
    }

    @Test
    void shouldReturnHttpManagerState() throws IOException, InterruptedException {
        //creating new manager by URL
        HttpTaskManager newManager = Managers.getDefault("http://localhost:8078");
        newManager.createTask(super.task1);
        //load manager state by httpManager method
        HttpTaskManager test = newManager.loadFromServer("taskManager");

        assertEquals(test.findTasksById(1), newManager.findTasksById(1));
        assertEquals(test.getTasks(), newManager.getTasks());
        assertEquals(test.getAllTaskList(), newManager.getAllTaskList());
    }

    @Test
    void shouldReturnEpicsList() throws IOException, InterruptedException {
        //creating new manager by URL
        HttpTaskManager newManager = Managers.getDefault("http://localhost:8078");
        newManager.createEpic(super.epic1);
        newManager.createEpic(super.epic2);
        //load manager state by httpManager method
        HttpTaskManager test = newManager.loadFromServer("taskManager");

        assertEquals(test.getEpics(), newManager.getEpics());
        assertEquals(test.getAllTaskList(), newManager.getAllTaskList());
    }
}
