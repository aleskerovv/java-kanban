package manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    static FileBackedTasksManager loadFromFile(File file) throws FileNotFoundException {
        FileReader reader = new FileReader(file);
        return new FileBackedTasksManager(file);
    }
}