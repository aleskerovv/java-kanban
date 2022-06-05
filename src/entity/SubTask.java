package entity;

import java.util.Objects;

public class SubTask extends Task {
    private String title;
    private String description;
    private int id;
    private String status;

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

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public boolean checkSubTaskStatus(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(status, subTask.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        String result = "SubTask{" +
                "title=" + title +
                ", description=" + description +
                ", id=" + id +
                ", status=" + status + '}';

        return result;
    }
}
