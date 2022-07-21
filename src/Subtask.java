public class Subtask extends Task {
    private int epicTaskId; //id задачи, к которой относится данная подзадача

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public Subtask(String title, String description, Status status, int epicTaskId) {
        super(title, description, status);
        this.epicTaskId = epicTaskId;
    }
}
