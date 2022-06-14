package entity;

public class SubTask extends Task {
    protected Epic epic;

    public SubTask(String title, String description, TaskStatus status, Epic epic) {
        super(title, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "Title=" + title +
                ", Description=" + description +
                ", id=" + id +
                ", Status=" + status +
                ", Epic=" + epic.getTitle() + "}";
    }
}
