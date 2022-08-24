package tasks;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final List<Integer> subtasks; //список идентификаторов для подзадач

    public EpicTask(String title, String description) {
        this.title = title;
        this.description = description;
        this.subtasks = new ArrayList<>();
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public void removeSubtask(Integer id) {
        subtasks.remove(id);
    }

    public void addSubtask(Integer id) {
        if (!subtasks.contains(id)) {
            subtasks.add(id);
        }
    }

    @Override
    public String toString() {
        return "tasks.EpicTask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks=" + subtasks +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
