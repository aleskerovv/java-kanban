package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;

import java.util.List;

public class ManagerToJson {
    List<Task> tasks;
    List<Epic> epics;
    List<SubTask> subTasks;
    List<Integer> history;
    public ManagerToJson() {
    }

    void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setEpics(List<Epic> epics) {
        this.epics = epics;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public void setHistory(List<Integer> history) {
        this.history = history;
    }
}
