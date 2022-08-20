package entity;

import java.time.LocalDateTime;
import java.util.Objects;

import static entity.TaskType.SUBTASK;

public class SubTask extends Task {
    protected Integer epicId;

    public SubTask(String title, String description, TaskStatus status, Integer duration, LocalDateTime startTime, Integer epicId) {
        super(title, description, status, duration, startTime);
        this.epicId = epicId;
        this.type = SUBTASK;
    }

    public SubTask(String title, String description, TaskStatus status, Integer duration, LocalDateTime startTime, Integer epicId, Integer id) {
        super(title, description, status, duration, startTime, id);
        this.epicId = epicId;
        this.type = SUBTASK;
    }

    public SubTask(String title, String description, TaskStatus status, Integer duration, Integer epicId, Integer id) {
        super(title, description, status, duration, id);
        this.epicId = epicId;
        this.type = SUBTASK;
    }

    public SubTask(String title, String description, TaskStatus status, Integer duration, Integer epicId) {
        super(title, description, status, duration);
        this.epicId = epicId;
        this.type = SUBTASK;
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
                && Objects.equals(epicId, subTask.epicId)
                && Objects.equals(duration, subTask.duration)
                && Objects.equals(startTime, subTask.startTime);
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
