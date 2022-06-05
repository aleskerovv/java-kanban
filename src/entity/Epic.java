package entity;

import java.util.List;

public class Epic extends Task {
    private String title;
    private String description;
    private int id;
    private String status;
    private List<SubTask> subtasks;

    public String setStatus() {
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

        if (isDone)
            return this.status = "DONE";
        else if (isNew)
            return this.status = "NEW";
        else
            return this.status = "IN_PROGRESS";
//        for (SubTask subTask : subtasks) {
//            if (subTask.getStatus().equals("IN_PROGRESS")) {
//                this.status = "IN_PROGRESS";
//                return;
//            }
//            if (subtasks.size() > 1) {
//                if (subTask.getStatus().equals("DONE") && !subTask.checkSubTaskStatus(subtasks.indexOf(subTask) - 1)) {
//                    this.status = "IN_PROGRESS";
//                    return;
//                }
//            }
//        }
//        if (subtasks.get(0).getStatus().equals("NEW")) {
//            this.status = "NEW";
//        } else if (subtasks.get(0).getStatus().equals("DONE")) {
//            this.status = "DONE";
//        }
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
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
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
                "title=" + title +
                ", description=" + description +
                ", id=" + id +
                ", status=" + getStatus() + '\'';
        if(!subtasks.isEmpty()) {
            result = result + ", subtasks=" + subtasks + '}';
        } else {
            result = result + ", subtasks=Нет активных подзадач}";
        }

        return result;
    }
}
