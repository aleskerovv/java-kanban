import entity.Task;
import entity.TaskStatus;
import entity.TaskType;
import manager.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {
    HistoryManager manager;

    @BeforeEach
    void create() {
        manager = Managers.getDefaultHistory();
    }

    @Test
    void addTask() {
        Task task = new Task("Task", "desc", TaskStatus.NEW, 50, "2022-08-08T12:00:00", 1);
        manager.addTask(task);
        Task task2 = new Task("task2", "desc", TaskStatus.DONE, 50, "2022-08-08T12:00:00", 2);
        manager.addTask(task2);

        final List<Task> returnedList = manager.getHistory();

        assertEquals(2, returnedList.size(), "History is empty");
    }

    @Test
    void removeFirst() {
        Task task = new Task("Task", "desc", TaskStatus.NEW, 50, "2022-08-08T12:00:00", 1);
        manager.addTask(task);
        Task task2 = new Task("task2", "desc", TaskStatus.DONE, 50, "2022-08-08T12:00:00", 2);
        manager.addTask(task2);
        Task task3 = new Task("task3", "desc", TaskStatus.DONE, 50, "2022-08-08T12:00:00", 3);
        manager.addTask(task3);

        manager.remove(1);

        final List<Task> returnedList = manager.getHistory();

        assertEquals(2, returnedList.size());
    }

    @Test
    void removeLast() {
        Task task = new Task("Task", "desc", TaskStatus.NEW, 50, "2022-08-08T12:00:00", 1);
        manager.addTask(task);
        Task task2 = new Task("task2", "desc", TaskStatus.DONE, 50, "2022-08-08T12:00:00", 2);
        manager.addTask(task2);
        Task task3 = new Task("task3", "desc", TaskStatus.DONE, 50, "2022-08-08T12:00:00", 3);
        manager.addTask(task3);

        manager.remove(3);

        final List<Task> returnedList = manager.getHistory();

        assertEquals(2, returnedList.size());
    }

    @Test
    void removeMiddle() {
        Task task = new Task("Task", "desc", TaskStatus.NEW, 50, "2022-08-08T12:00:00", 1);
        manager.addTask(task);
        Task task2 = new Task("task2", "desc", TaskStatus.DONE, 50, "2022-08-08T12:00:00",2);
        manager.addTask(task2);
        Task task3 = new Task("task3", "desc", TaskStatus.DONE, 50, "2022-08-08T12:00:00",3);
        manager.addTask(task3);

        manager.remove(2);

        final List<Task> returnedList = manager.getHistory();

        assertEquals(2, returnedList.size());
    }

    @Test
    void returnEmptyHistory() {
        List<Task> empty = new ArrayList<>();
        List<Task> returnedList = manager.getHistory();

        assertEquals(empty.size(), returnedList.size(), "List not empty");
    }

    @Test
    void checkDistinctHistory() {
        Task task = new Task("Task", "desc", TaskStatus.NEW, 50, "2022-08-08T12:00:00", 1);
        manager.addTask(task);
        Task task2 = new Task("task2", "desc", TaskStatus.DONE, 50, "2022-08-08T12:00:00", 2);
        manager.addTask(task2);
        Task task3 = new Task("task3", "desc", TaskStatus.DONE, 50, "2022-08-08T12:00:00", 3);
        manager.addTask(task3);
        manager.addTask(task2);
        manager.addTask(task);

        List<Task> expectedList = List.of(task3, task2, task);
        List<Task> actualList = manager.getHistory();

        assertEquals(expectedList, actualList, "Lists are not the same");
        assertEquals(expectedList.size(), actualList.size(), "Lists are not the same");
    }
}
