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

    int addTask(Task task);

    int addTask(EpicTask task);

    int addTask(Subtask task);

    int removeTaskById(int id);

    int updateTask(Task task);

    List<Subtask> getSubtasksOfEpic(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
