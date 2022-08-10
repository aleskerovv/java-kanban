import entity.*;
import manager.FileBackedTasksManager;
import manager.Managers;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    public FileBackedTaskManagerTest() throws FileNotFoundException {
        super.manager = Managers.loadFromFile(new File("tests.csv"));
    }

    @Test
    void checkSaveTask() {
        manager.createTask(task1);

        manager.createEntityFromText();
        Task returnedTask = manager.findTasksById(1);

        assertEquals(task1, returnedTask);
    }

    @Test
    void checkReadFromFile() {
        manager.createTask(task1);
        manager.createEntityFromText();
        Task task = manager.findTasksById(1);

        assertEquals(task1, task);
    }

    @Test
    void checkHistorySave() {
        Task task = new Task("Task", "task", TaskStatus.NEW, 50, "2022-08-08T12:00:00");
        manager.createTask(task);
        manager.findTasksById(1);

        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void checkEpicSaveStatus() {
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEntityFromText();

        Epic epicTest1 = manager.findEpicById(1);
        Epic epicTest2 = manager.findEpicById(2);

        assertEquals(TaskStatus.NEW, epicTest1.getStatus());
        assertEquals(TaskStatus.NEW, epicTest2.getStatus());
        assertEquals(1, epicTest1.getId());
        assertEquals(2, epicTest2.getId());
    }

    @Test
    void checkEmptyHistory() {
        List<Task> returnedList = manager.getHistory();

        assertEquals(0, returnedList.size());
    }
}
