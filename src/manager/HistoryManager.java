package manager;

import entity.Task;

import java.util.LinkedList;
import java.util.List;

public interface HistoryManager {

    void addTask(Task task);

    void remove(int id);

    List<Task> getHistory();
}
