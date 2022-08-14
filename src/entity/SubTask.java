package entity;

import java.time.LocalDateTime;
import java.util.Objects;

import static entity.TaskType.SUBTASK;

public class SubTask extends Task {
    protected Integer epicId;

    public SubTask(String title, String description, TaskStatus status, long duration, LocalDateTime startTime, Integer epicId) {
        super(title, description, status, duration, startTime);
        this.type = SUBTASK;
        this.epicId = epicId;
    }

    public SubTask(String title, String description, TaskStatus status, long duration, LocalDateTime startTime, Integer epicId, Integer id) {
        super(title, description, status, duration, startTime, id);
        this.type = SUBTASK;
        this.epicId = epicId;
    }

    public SubTask(String title, String description, TaskStatus status, long duration, Integer epicId, Integer id) {
        super(title, description, status, duration, id);
        this.type = SUBTASK;
        this.epicId = epicId;
    }

    public SubTask(String title, String description, TaskStatus status, long duration, Integer epicId) {
        super(title, description, status, duration);
        this.type = SUBTASK;
        this.epicId = epicId;
    }

    @Override
    public Integer getEpic() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(title, subTask.title)
                && Objects.equals(description, subTask.description)
                && Objects.equals(id, subTask.id)
                && status == subTask.status
                && type == subTask.type
                && Objects.equals(epicId, subTask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, type, epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", duration=" + duration +
                ", startTime=" + startTime + " мин." +
                ", endTime=" + getEndTime() +
                ", epicId=" + epicId +
                '}';
    }
}
