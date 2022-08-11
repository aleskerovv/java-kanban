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
    void checkSaveTaskAndFromCsvReading() throws FileNotFoundException {
        FileBackedTasksManager test = Managers.loadFromFile(new File("fortests.csv"));
        test.createEntityFromText();
        Epic epicTest1 = test.findEpicById(3);

        assertEquals(epic1, epicTest1);
    }

    @Test
    void checkHistorySave() {
        manager.findTasksById(1);

        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void checkEpicSaveStatus() throws FileNotFoundException {
        FileBackedTasksManager test = Managers.loadFromFile(new File("fortests.csv"));
        test.createEntityFromText();

        Epic epicTest1 = test.findEpicById(3);

        assertEquals(TaskStatus.NEW, epicTest1.getStatus());
        assertEquals(3, epicTest1.getId());
    }

    @Test
    void checkEmptyHistory() {
        List<Task> returnedList = manager.getHistory();

        assertEquals(0, returnedList.size());
    }
}
