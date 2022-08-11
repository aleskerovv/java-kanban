package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static entity.TaskType.TASK;

public class Task {
    protected String title;
    protected String description;
    protected Integer id;
    protected TaskStatus status;
    protected TaskType type;
    protected long duration;
    protected LocalDateTime startTime;
//    protected LocalDateTime endTime;

    public Task() {

    }

    public Task(String title, String description, TaskStatus status, long duration, String startTime) {
        this(title, description);
        this.duration = duration;
        this.status = status;
        this.startTime = LocalDateTime.parse(startTime);
        this.type = TASK;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.type = TASK;
    }

    public Task(String title, String description, long duration, String startTime) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.startTime = LocalDateTime.parse(startTime);
        this.type = TASK;
    }

    public Task(String title, String description, TaskStatus status, long duration, String startTime, Integer id) {
        this(title, description);
        this.status = status;
        this.type = TASK;
        this.duration = duration;
        this.startTime = LocalDateTime.parse(startTime);
        this.id = id;
    }

    public Task(String title, String description, TaskStatus status, long duration, Integer id) {
        this(title, description);
        this.status = status;
        this.type = TASK;
        this.duration = duration;
        this.id = id;
    }
    public Task(String title, String description, TaskStatus status, long duration) {
        this(title, description);
        this.status = status;
        this.type = TASK;
        this.duration = duration;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return getStartTime().plusMinutes(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        if (startTime == null) {
            this.startTime = null;
        } else {
            this.startTime = LocalDateTime.parse(startTime);
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", duration=" + duration + " мин." +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && Objects.equals(id, task.id)
                && status == task.status
                && type == task.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, type);
    }
}
