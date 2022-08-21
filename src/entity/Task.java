package entity;

import java.time.LocalDateTime;
import java.util.Objects;

import static entity.TaskType.TASK;

public class Task {
    protected String title;
    protected String description;
    protected Integer id;
    protected TaskStatus status;
    protected TaskType type;
    protected Integer duration;
    protected LocalDateTime startTime;

    public Task() {

    }

    public Task(String title, String description, TaskStatus status, Integer duration, LocalDateTime startTime) {
        this(title, description);
        this.duration = duration;
        this.status = status;
        this.startTime = startTime;
        this.type = TASK;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.type = TASK;
    }


    public Task(String title, String description, TaskStatus status, Integer duration, LocalDateTime startTime, Integer id) {
        this(title, description);
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.id = id;
        this.type = TASK;
    }

    public Task(String title, String description, TaskStatus status, Integer duration, Integer id) {
        this(title, description);
        this.status = status;
        this.duration = duration;
        this.id = id;
        this.type = TASK;
    }

    public Task(String title, String description, TaskStatus status, Integer duration) {
        this(title, description);
        this.status = status;
        this.duration = duration;
        this.type = TASK;
    }

    public void setType(TaskType type) {
        this.type = type;
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

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Integer getEpic() {
        return null;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (getStartTime() == null) {
            return null;
        }
        return getStartTime().plusMinutes(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

//    public void setStartTime(String startTime) {
//        if (startTime == null) {
//            this.startTime = null;
//        } else {
//            this.startTime = LocalDateTime.parse(startTime);
//        }
//    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", duration=" + duration +
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
                && type == task.type
                && Objects.equals(duration, task.duration)
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, type, duration, startTime);
    }
}
