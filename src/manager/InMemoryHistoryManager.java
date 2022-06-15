package manager;

import entity.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    protected final List<Task> historyList;

    public InMemoryHistoryManager() {
        historyList = new ArrayList<>();
    }

    @Override
    public void addTask(Task task) {
        if (historyList.size() >= 10) {
            historyList.remove(0);
        }
        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
