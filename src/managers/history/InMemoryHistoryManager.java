package managers.history;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int sizeLimit;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        sizeLimit = 10;
        history = new ArrayList<>();
    }

    public InMemoryHistoryManager(int limit) {
        sizeLimit = limit;
        history = new ArrayList<>();
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
}
