package entity;

import java.util.List;

public class Epic extends Task {
    private String title;
    private String description;
    private Long id;
    private String status;
    private List<SubTask> subtasks;

    public void setStatus() {
        checkStatus();
    }

    //Произвожу расчет статуса Epic при всех возможных статусах SubTask
    public String checkStatus() {
        if (subtasks.isEmpty()) return this.status = "NEW";

        boolean isDone = false;
        boolean isNew = false;
        for (SubTask subTask : subtasks) {
            switch (subTask.getStatus()) {
                case "NEW":
                    if (isDone) return this.status = "IN_PROGRESS";
                    isNew = true;
                    break;
                case "DONE":
                    if (isNew) return this.status = "IN_PROGRESS";
                    isDone = true;
                    break;
                case "IN_PROGRESS":
                    return this.status = "IN_PROGRESS";
            }
        }

        if (isDone) {
            return this.status = "DONE";
        } else if (isNew) {
            return this.status = "NEW";
        } else {
            return this.status = "IN_PROGRESS";
        }
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setSubtasks(List<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    public List<SubTask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "Title=" + title +
                ", Description=" + description +
                ", id=" + id +
                ", Status=" + getStatus();
        if (!subtasks.isEmpty()) {
            result = result + ", Subtasks=" + subtasks + "}";
        } else {
            result = result + ", Subtasks=Нет активных подзадач}";
        }
        return result;
    }
}
