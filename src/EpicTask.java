import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<Integer> subtasks; //список идентификаторов для подзадач

    public EpicTask(String title, String description, ArrayList<Integer> subtasks) {
        super(title, description);
        this.subtasks = subtasks;
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public void removeSubtask(Integer id) {
        subtasks.remove(id);
    }

    public void addSubtask(Integer id) {
        subtasks.add(id);
    }
}
