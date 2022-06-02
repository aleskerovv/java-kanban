package entity;

public class Task {
    private String title;
    private String description;
    private int id;
    private String status;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        String result = "Task{" +
                "title=" + title +
                ", description=" + description +
                ", id=" + id +
                ", status=" + status + '}';

        return result;
    }
}
