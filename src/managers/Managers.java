package managers;

import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.taskmanager.FileBackedTasksManager;
import managers.taskmanager.InMemoryTaskManager;
import managers.taskmanager.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager(10);
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        return new FileBackedTasksManager(file);
    }
}
