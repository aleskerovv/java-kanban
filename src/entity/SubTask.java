package entity;

import java.util.Objects;

public class SubTask extends Task {
    protected Integer epicId;

    public SubTask(String title, String description, TaskStatus status, Integer epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, TaskStatus status, Integer epicId, Integer id) {
        super(title, description, status);
        this.epicId = epicId;
        this.id = id;
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
        return Objects.equals(getTitle(), subTask.getTitle())
                && Objects.equals(getDescription(), subTask.getDescription())
                && Objects.equals(getStatus(), subTask.getStatus())
                && Objects.equals(getId(), subTask.getId())
                && Objects.equals(getEpic(), subTask.getEpic());
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status, id, epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "Title=" + title +
                ", Description=" + description +
                ", id=" + id +
                ", Status=" + status +
                ", EpicId=" + epicId + "}";
    }
}
