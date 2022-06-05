package entity;

public class Task {
    private String title;
    private String description;
    private Long id;
    private String status;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        String result = "Task{" +
                "Title=" + title +
                ", Description=" + description +
                ", id=" + id +
                ", Status=" + status + "}";
        return result;
    }
}
