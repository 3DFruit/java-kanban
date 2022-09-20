package tasks;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicTaskId;

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public Subtask(String title, String description, Status status, long duration, LocalDateTime startTime, int epicTaskId) {
        super(title, description, status, duration, startTime);
        this.epicTaskId = epicTaskId;
    }

    public Subtask(int id, String title, String description, Status status, long duration, LocalDateTime startTime, int epicTaskId) {
        super(id, title, description, status, duration, startTime);
        this.epicTaskId = epicTaskId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", epicTaskId=" + epicTaskId +
                '}';
    }
}
