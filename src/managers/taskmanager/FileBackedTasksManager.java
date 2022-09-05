package managers.taskmanager;

import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

public class FileBackedTasksManager extends InMemoryTaskManager{

    private void save() {

    }

    @Override
    public int addTask(Subtask subtask) {
        int id = super.addTask(subtask);
        save();
        return id;
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addTask(EpicTask epic) {
        int id = super.addTask(epic);
        save();
        return id;
    }

    @Override
    public int removeTaskById(int id) {
        int result = super.removeTaskById(id);
        save();
        return result;
    }

    @Override
    public int updateTask(Task task) {
        int id = super.updateTask(task);
        save();
        return id;
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllEpicTasks();
        save();
    }
}
