package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static entity.TaskStatus.*;
import static entity.TaskType.EPIC;

public class Epic extends Task {
    private LocalDateTime endTime;

    protected List<Integer> subTasks = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
        this.type = EPIC;
        this.status = NEW;
    }

    public Epic(String title, String description, Integer id) {
        super(title, description);
        this.type = EPIC;
        this.id = id;
    }

    public void addSubtasksId(Integer subTaskId) {
        this.subTasks.add(subTaskId);
    }

    public List<Integer> getSubtasks() {
        return subTasks;
    }

    public void clearSubtasks() {
        subTasks.clear();
    }

    public void deleteSubTaskById(Integer subTaskId) {
        subTasks.remove(subTaskId);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", duration=" + duration + " мин." +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime();
        if (subTasks.isEmpty())
            result = result + ", subtasks=Нет активных подзадач" + '}';
        else
            result = result + ", subtasks=" + subTasks + '}';
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(title, epic.title)
                && Objects.equals(description, epic.description)
                && Objects.equals(id, epic.id)
                && status == epic.status
                && type == epic.type
                && Objects.equals(subTasks, epic.subTasks)
                && Objects.equals(startTime, epic.startTime)
                && Objects.equals(duration, epic.duration)
                &&Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, type, subTasks, duration, startTime, endTime);
    }
}
