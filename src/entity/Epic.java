package entity;

import java.util.List;

public class Epic extends Task {
    private String title;
    private String description;
    private int id;
    private String status;
    private List<SubTask> subtasks;

    public void setStatus() {

        if (subtasks.isEmpty()) {
            this.status = "NEW";
        }
//        for (SubTask subTask : subtasks) {
//            if (subTask.getStatus().equals("NEW")) {
//                this.status = "NEW";
//            } else if (subTask.getStatus().equals("DONE")) {
//                this.status = "DONE";
//            } else {
//                this.status = "IN_PROGRESS";
//            }
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
