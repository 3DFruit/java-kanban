package managers.history;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int sizeLimit;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        sizeLimit = 10;
        history = new LinkedList<>();
    }

    public InMemoryHistoryManager(int limit) {
        sizeLimit = limit;
        history = new LinkedList<>();
    }

    public List<Task> getHistory() {
        return history;
    }

    public void add(Task task) {
        if (history.size() >= sizeLimit) {
            history.remove(0);
            history.add(task);
        } else {
            history.add(task);
        }
    }

    public void remove(int id) {
        history.removeIf(task -> task.getId() == id);
    }
}
