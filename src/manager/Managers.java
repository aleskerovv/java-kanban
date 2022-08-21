package manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Managers {
    private Managers() {
    }

    public static HttpTaskManager getDefault(String url) throws IOException, InterruptedException {
        return new HttpTaskManager(url);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager loadFromFile(File file) throws FileNotFoundException {
        return new FileBackedTasksManager(file);
    }
}