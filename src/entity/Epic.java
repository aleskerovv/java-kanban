package entity;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<SubTask> subTasks = new ArrayList<>();
    static final TaskStatus NEW = TaskStatus.NEW;
    static final TaskStatus IN_PROGRESS = TaskStatus.IN_PROGRESS;
    static final TaskStatus DONE = TaskStatus.DONE;

    public Epic(String title, String description) {
        super(title, description);
        setStatus();
    }

    public void setStatus() {
        checkStatus();
    }

    //Произвожу расчет статуса Epic при всех возможных статусах SubTask
    public void checkStatus() {
        if (subTasks.isEmpty()) {
            this.status = NEW;
            return;
        }

        boolean isDone = false;
        boolean isNew = false;
        for (SubTask subTask : subTasks) {
            switch (subTask.getStatus()) {
                case NEW:
                    if (isDone) {
                        this.status = IN_PROGRESS;
                        return;
                    }
                    isNew = true;
                    break;
                case DONE:
                    if (isNew) {
                        this.status = IN_PROGRESS;
                        return;
                    }
                    isDone = true;
                    break;
                case IN_PROGRESS:
                    this.status = IN_PROGRESS;
                    return;
            }
        }

        if (isDone) {
            this.status = DONE;
        } else if (isNew) {
            this.status = NEW;
        } else {
            this.status = IN_PROGRESS;
        }
    }

    public void setSubtasks(SubTask subTask) {
        this.subTasks.add(subTask);
    }

    public List<SubTask> getSubtasks() {
        return subTasks;
    }

    public void clearSubtasks() {
        subTasks.clear();
    }

    public void deleteSubTaskById(SubTask subTask) {
        subTasks.remove(subTask);
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
