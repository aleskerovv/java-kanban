package entity;

public class SubTask extends Task {
    private String title;
    private String description;
    private Long id;
    private String status;

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        String result = "SubTask{" +
                "Title=" + title +
                ", Description=" + description +
                ", id=" + id +
                ", Status=" + status + "}";

        return result;
    }
}
