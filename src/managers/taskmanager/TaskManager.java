package managers.taskmanager;

import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    void removeAllSubtasks();

    void removeAllEpicTasks();

    void removeAllTasks();

    List<Task> getTasks();

    List<EpicTask> getEpicTasks();

    List<Subtask> getSubtasks();

    Task getTaskById(int id);

    EpicTask getEpicTaskById(int id);

    Subtask getSubtaskById(int id);

    void addTask(Task task);

    void addTask(EpicTask task);

    void addTask(Subtask task);

    void removeTaskById(int id);

    void updateTask(Task task);

    List<Subtask> getSubtasksOfEpic(int id);

    List<Task> getHistory();
}
