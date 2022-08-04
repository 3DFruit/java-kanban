package managers.taskmanager;

import managers.Managers;
import managers.history.HistoryManager;
import tasks.EpicTask;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int nextId; //поле для создания идентификатора следующей новой задачи
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager history;

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    public InMemoryTaskManager() {
        nextId = 0;
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
        history = Managers.getDefaultHistoryManager();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (EpicTask epicTask : epicTasks.values()) { //очищаем списки подзадач для каждого эпика
            epicTask.clearSubtasks();
        }
    }

    @Override
    public void removeAllEpicTasks() {
        epicTasks.clear();
        subtasks.clear(); //удаляем подзадачи, так как не останется эпиков, к котоорым они относятся
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        EpicTask task = epicTasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask task = subtasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            task.setId(nextId);
            tasks.put(nextId, task);
            nextId++;
        }
    }

    @Override
    public void addTask(EpicTask task) {
        if (task != null) {
            task.setId(nextId);
            epicTasks.put(nextId, task);
            updateEpicStatus(nextId);
            nextId++;
        }
    }

    @Override
    public void addTask(Subtask task) {
        if (task != null) {
            task.setId(nextId);
            int id = task.getEpicTaskId();
            if (epicTasks.containsKey(id)) {
                epicTasks.get(id).addSubtask(nextId);
                subtasks.put(nextId, task);
                updateEpicStatus(task.getEpicTaskId());
                nextId++;
            }
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return;
        }
        if (epicTasks.containsKey(id)) {
            //удаляем из списка подзадач, те подзадачи, которые относятся к удаляемому эпику
            for (Integer subtask : epicTasks.get(id).getSubtasks()) {
                subtasks.remove(subtask);
            }
            epicTasks.remove(id);
            return;
        }
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicTaskId();
            epicTasks.get(epicId).removeSubtask(id); //удаляем подзадачу из эпика, к которому она относится
            updateEpicStatus(epicId);
            subtasks.remove(id);
        }
    }

    @Override
    public void updateTask(Task task) {
        //обновление данных происходит, если класс передаваемого объекта соответствует классу, хранимому в HashMap
        if (task == null) {
            return;
        }
        int id = task.getId();
        if (tasks.containsKey(id) && task.getClass() == tasks.get(id).getClass()) {
            tasks.put(id, task);
            return;
        }
        if (epicTasks.containsKey(id) && task.getClass() == epicTasks.get(id).getClass()) {
            EpicTask newTask = (EpicTask) task;
            for (Integer subtask : epicTasks.get(id).getSubtasks()) {
                newTask.addSubtask(subtask);
            }
            epicTasks.put(id, newTask);
            updateEpicStatus(id);
            return;
        }
        if (subtasks.containsKey(id) && task.getClass() == subtasks.get(id).getClass()) {
            Subtask newTask = (Subtask) task;
            subtasks.put(id, newTask);
            updateEpicStatus(newTask.getEpicTaskId());
        }
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasksOfEpic(int id) {
        HashMap<Integer, Subtask> result = new HashMap<>();
        if (epicTasks.containsKey(id)) {
            for (Integer subtaskId : epicTasks.get(id).getSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                //добавляем в результирующую таблицу только существующие подзадачи
                if (subtask != null) {
                    result.put(subtaskId, subtask);
                }
            }
        }
        return result;
    }

    private void updateEpicStatus(int id) {
        EpicTask epic = epicTasks.get(id);
        if (epic != null) {
            HashMap<Integer, Subtask> subtasks = getSubtasksOfEpic(id);
            if (subtasks.size() == 0) {
                epic.setStatus(Status.NEW);
                return;
            }
            //проверка статусов подзадач
            boolean flagNew = true;
            boolean flagDone = true;
            for (Subtask subtask : subtasks.values()) {
                if (subtask == null) {
                    continue;
                }
                if (subtask.getStatus() != Status.DONE) {
                    flagDone = false;
                }
                if (subtask.getStatus() != Status.NEW) {
                    flagNew = false;
                }
                //если оба флага приняли значение false, то не имеет смысла продолжать цикл
                if (!flagDone && !flagNew) break;
            }
            if (flagNew) {
                epic.setStatus(Status.NEW);
                return;
            }
            if (flagDone) {
                epic.setStatus(Status.DONE);
                return;
            }
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "Manager{" + "nextId=" + nextId + ", \ntasks=" + tasks + ", \nepicTasks=" + epicTasks + ", \nsubtasks=" + subtasks + '}';
    }
}
