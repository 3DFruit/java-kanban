package managers;

import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.taskmanager.InMemoryTaskManager;
import managers.taskmanager.TaskManager;

public class Managers {
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager(){
        return new InMemoryHistoryManager();
    }
}
