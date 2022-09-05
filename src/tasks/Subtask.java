package tasks;

public class Subtask extends Task {
    private final int epicTaskId;

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public Subtask(String title, String description, Status status, int epicTaskId) {
        super(title, description, status);
        this.epicTaskId = epicTaskId;
    }

    public Subtask(int id, String title, String description, Status status, int epicTaskId) {
        super(id, title, description, status);
        this.epicTaskId = epicTaskId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicTaskId=" + epicTaskId +
                '}';
    }
}
