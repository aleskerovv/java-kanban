package entity;

public class Task {
    protected String title;
    protected String description;
    protected Integer id;
    protected TaskStatus status;
    protected TaskType type;

    public Task() {

    }

    public Task(String title, String description, TaskType type, TaskStatus status) {
        this(title, description, type);
        this.status = status;
    }

    public Task(String title, String description, TaskType type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }

    public Task(String title, String description, TaskType type,TaskStatus status, Integer id) {
        this(title, description, type);
        this.status = status;
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return type;
    }

    public Integer getEpic() {
        return null;
    }
    @Override
    public String toString() {
        return "Task{" +
                "Title=" + title +
                ", Description=" + description +
                ", id=" + id +
                ", Status=" + status + "}";
    }
}
