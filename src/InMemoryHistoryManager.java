import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> history;

    public InMemoryHistoryManager(){
        history = new ArrayList<>();
    }

    public List<Task> getHistory(){
        return history;
    }

    public void add(Task task){
        if(history.size() > 10) {
            history.remove(0);
            history.add(task);
        }
        else {
            history.add(task);
        }
    }
}
