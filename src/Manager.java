import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int nextId; //поле для создания идентификатора следующей новой задачи
    private HashMap<Integer, Task> tasks;
    private HashMap <Integer, EpicTask> epicTasks;
    private HashMap <Integer, Subtask> subtasks;

    public Manager() {
        nextId = 0;
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (EpicTask epicTask : epicTasks.values()){ //очищаем списки подзадач для каждого эпика
            epicTask.clearSubtasks();
        }
    }

    public void removeAllEpicTasks() {
        epicTasks.clear();
        subtasks.clear(); //удаляем подзадачи, так как не останется эпиков, к котоорым они относятся
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public EpicTask getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void addNewTask (Task task) {
        tasks.put(nextId, task);
        nextId++;
    }

    public void addNewTask (EpicTask task) {
        epicTasks.put(nextId, task);
        updateEpicStatus(nextId);
        nextId++;
    }

    public void addNewTask (Subtask task) {
        if (task != null) {
            int id = task.getEpicTaskId();
            if (epicTasks.containsKey(id)) {
                epicTasks.get(id).addSubtask(nextId);
                subtasks.put(nextId, task);
                updateEpicStatus(nextId);
                nextId++;
            }
        }
    }

    public void removeTaskById (int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return;
        }
        if (epicTasks.containsKey(id)) {
            //удаляем из списка подзадач, те подзадачи, которые относятся к удаляемому эпику
            for (Integer subtask : epicTasks.get(id).getSubtasks()){
                subtasks.remove(id);
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

    public void updateTask (int id, Object task) {
        //обновление данных происходит, если класс передаваемого объекта соответствует классу, хранимому в HashMap
        if (tasks.containsKey(id) && task.getClass() == tasks.get(id).getClass()) {
            tasks.put(id, (Task) task);
            return;
        }
        if (epicTasks.containsKey(id) && task.getClass() == epicTasks.get(id).getClass()) {
            epicTasks.put(id, (EpicTask) task);
            updateEpicStatus(id);
            return;
        }
        if (subtasks.containsKey(id) && task.getClass() == subtasks.get(id).getClass()) {
            Subtask subtask = (Subtask) task;
            subtasks.put(id, subtask);
            updateEpicStatus(subtask.getEpicTaskId());
        }
    }

    public HashMap<Integer, Subtask> getSubtasksOfEpic (int id){
        HashMap<Integer, Subtask> result = new HashMap<>();
        if (epicTasks.containsKey(id)) {
            for (Integer subtaskId : epicTasks.get(id).getSubtasks()) {
                result.put(subtaskId, subtasks.get(subtaskId));
            }
        }
        return result;
    }

    public void updateEpicStatus(int id) {
        EpicTask epic = epicTasks.get(id);
        if (epic != null) {
            HashMap<Integer, Subtask> subtasks = getSubtasksOfEpic(id);
            if (subtasks.size() == 0){
                epic.setStatus(Status.NEW);
                return;
            }
            //проверка статусов подзадач
            boolean flagNew = true;
            boolean flagDone = true;
            for (Subtask subtask : subtasks.values()) {
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
}
