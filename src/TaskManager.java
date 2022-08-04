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

    void addNewTask(Task task);

    void addNewTask(EpicTask task);

    void addNewTask(Subtask task);

    void removeTaskById(int id);

    void updateTask(int id, Object task);

    HashMap<Integer, Subtask> getSubtasksOfEpic(int id);
}
