package entity;

import java.util.ArrayList;
import java.util.List;

import static entity.TaskStatus.*;

public class Epic extends Task {
    protected List<Integer> subTasks = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
        this.status = NEW;
    }

    public void setSubtasks(Integer subTaskId) {
        this.subTasks.add(subTaskId);
    }

    public List<Integer> getSubtasks() {
        return subTasks;
    }

    public void clearSubtasks() {
        subTasks.clear();
    }

    public void deleteSubTaskById(Integer subTaskId) {
        subTasks.remove(subTaskId);
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "Title=" + title +
                ", Description=" + description +
                ", id=" + id +
                ", Status=" + getStatus();
        if (subTasks.isEmpty()) {
            result = result + ", Subtasks=Нет активных подзадач}";
        } else {
            result = result + ", Subtasks=" + subTasks + "}";
        }
        return result;
    }
}
