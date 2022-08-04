import java.util.HashMap;

public interface TaskManager {

    void removeAllSubtasks();

    void removeAllEpicTasks();

    void removeAllTasks();

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, EpicTask> getEpicTasks();

    HashMap<Integer, Subtask> getSubtasks();

    Task getTaskById(int id);

    EpicTask getEpicTaskById(int id);

    Subtask getSubtaskById(int id);

    void addTask(Task task);

    void addTask(EpicTask task);

    void addTask(Subtask task);

    void removeTaskById(int id);

    void updateTask(Task task);

    HashMap<Integer, Subtask> getSubtasksOfEpic(int id);

}
