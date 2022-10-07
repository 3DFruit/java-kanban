package managers.taskmanager;

import managers.Managers;
import managers.history.HistoryManager;
import tasks.EpicTask;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId; //поле для создания идентификатора следующей новой задачи
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, EpicTask> epicTasks;
    protected final Map<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;

    protected final TreeSet<Task> sortedTasks;

    public InMemoryTaskManager() {
        this.nextId = 0;
        this.tasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistoryManager();
        this.sortedTasks = new TreeSet<>((o1, o2) -> {
            if (o1.equals(o2)) {
                return 0;
            }
            LocalDateTime firstStart = o1.getStartTime();
            LocalDateTime secondStart = o2.getStartTime();
            if (firstStart == null) {
                return 1;
            }
            if (secondStart == null) {
                return -1;
            }
            int compareResult = firstStart.compareTo(secondStart);
            if (compareResult != 0) {
                return firstStart.compareTo(secondStart);
            }

            if (o1.getClass() == Task.class && o2.getClass() != Task.class) {
                return -1;
            }
            if (o1.getClass() == EpicTask.class && o2.getClass() == Subtask.class) {
                return -1;
            }
            return 1;
        });
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            sortedTasks.remove(subtask);
        }
        subtasks.clear();
        for (EpicTask epicTask : epicTasks.values()) { //очищаем списки подзадач для каждого эпика
            epicTask.clearSubtasks();
        }
    }

    @Override
    public void removeAllEpicTasks() {
        for (EpicTask epic : epicTasks.values()) {
            historyManager.remove(epic.getId());
            sortedTasks.remove(epic);
        }
        epicTasks.clear();
        //удаляем подзадачи, так как не останется эпиков, к котоорым они относятся
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            sortedTasks.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            sortedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {

        Task task = tasks.getOrDefault(id, null);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        EpicTask task = epicTasks.getOrDefault(id, null);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask task = subtasks.getOrDefault(id, null);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public int addTask(Task task) {
        if (task != null) {
            task.setId(nextId);
            tasks.put(nextId, task);
            sortedTasks.add(task);
            correctIntersections();
            return nextId++;
        }
        return -1;
    }

    @Override
    public int addTask(EpicTask task) {
        if (task != null) {
            task.setId(nextId);
            epicTasks.put(nextId, task);
            updateEpicStatus(nextId);
            updateEpicTime(nextId);
            sortedTasks.add(task);
            correctIntersections();
            return nextId++;
        }
        return -1;
    }

    @Override
    public int addTask(Subtask task) {
        if (task != null) {
            task.setId(nextId);
            int id = task.getEpicTaskId();
            if (epicTasks.containsKey(id)) {
                epicTasks.get(id).addSubtask(nextId);
                subtasks.put(nextId, task);
                updateEpicStatus(task.getEpicTaskId());
                updateEpicTime(task.getEpicTaskId());
                sortedTasks.add(task);
                correctIntersections();
                return nextId++;
            }
        }
        return -1;
    }

    @Override
    public int removeTaskById(int id) {
        historyManager.remove(id);
        if (tasks.containsKey(id)) {
            sortedTasks.remove(tasks.get(id));
            tasks.remove(id);
            return id;
        }
        if (epicTasks.containsKey(id)) {
            //удаляем из списка подзадач, те подзадачи, которые относятся к удаляемому эпику
            for (Integer subtask : epicTasks.get(id).getSubtasks()) {
                historyManager.remove(subtask);
                sortedTasks.remove(subtasks.get(subtask));
                subtasks.remove(subtask);
            }
            sortedTasks.remove(epicTasks.get(id));
            epicTasks.remove(id);
            return id;
        }
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicTaskId();
            epicTasks.get(epicId).removeSubtask(id); //удаляем подзадачу из эпика, к которому она относится
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
            sortedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            return id;
        }
        return -1;
    }

    @Override
    public int updateTask(Task task) {
        //обновление данных происходит, если класс передаваемого объекта соответствует классу, хранимому в HashMap
        if (task == null) {
            return -1;
        }
        int id = task.getId();
        if (tasks.containsKey(id) && task.getClass() == tasks.get(id).getClass()) {
            Task oldTask = tasks.get(id);
            sortedTasks.remove(oldTask);
            tasks.put(id, task);
            sortedTasks.add(task);
            correctIntersections();
            return id;
        }
        if (epicTasks.containsKey(id) && task.getClass() == epicTasks.get(id).getClass()) {
            EpicTask newTask = (EpicTask) task;
            for (Integer subtask : epicTasks.get(id).getSubtasks()) {
                newTask.addSubtask(subtask);
            }
            EpicTask oldTask = epicTasks.get(id);
            sortedTasks.remove(oldTask);
            epicTasks.put(id, newTask);
            sortedTasks.add(newTask);
            correctIntersections();
            updateEpicStatus(id);
            updateEpicTime(id);
            return id;
        }
        if (subtasks.containsKey(id) && task.getClass() == subtasks.get(id).getClass()) {
            Subtask newTask = (Subtask) task;
            Subtask oldTask = subtasks.get(id);
            sortedTasks.remove(oldTask);
            subtasks.put(id, newTask);
            sortedTasks.add(newTask);
            correctIntersections();
            updateEpicStatus(newTask.getEpicTaskId());
            updateEpicTime(newTask.getEpicTaskId());
            return id;
        }
        return -1;
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int id) {
        List<Subtask> result = new ArrayList<>();
        if (epicTasks.containsKey(id)) {
            for (Integer subtaskId : epicTasks.get(id).getSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                //добавляем в результирующую таблицу только существующие подзадачи
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }

    protected void updateEpicStatus(int id) {
        EpicTask epic = epicTasks.get(id);
        if (epic != null) {
            List<Subtask> subtasks = getSubtasksOfEpic(id);
            if (subtasks.size() == 0) {
                epic.setStatus(Status.NEW);
                return;
            }
            //проверка статусов подзадач
            boolean flagNew = true;
            boolean flagDone = true;
            for (Subtask subtask : subtasks) {
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
                if (!flagDone && !flagNew) {
                    break;
                }
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

    protected void updateEpicTime(int id) {
        EpicTask epic = epicTasks.get(id);
        if (epic != null) {
            sortedTasks.remove(epic);
            List<Subtask> subtasks = getSubtasksOfEpic(id);
            if (subtasks.size() == 0) {
                epic.setDuration(0);
                epic.setStartTime(null);
                epic.setEndTime((null));
                return;
            }
            LocalDateTime firstSubtaskStart = subtasks.get(0).getStartTime();
            long fullDuration = 0;
            for (Subtask subtask : subtasks) {
                fullDuration += subtask.getDuration();
                LocalDateTime start = subtask.getStartTime();
                if (start == null) {
                    continue;
                }
                if (start.isBefore(firstSubtaskStart)) {
                    firstSubtaskStart = start;
                }
            }
            epic.setStartTime(firstSubtaskStart);
            epic.setDuration(fullDuration);
            if (firstSubtaskStart != null ) {
                epic.setEndTime(firstSubtaskStart.plus(Duration.ofMinutes(fullDuration)));
            }
            sortedTasks.add(epic);
        }
    }

    public List<Task> getPrioritizedTasks() {
        //Задание:
        //Напишите новый метод getPrioritizedTasks, возвращающий список задач и подзадач в заданном порядке.
        //Сложность получения должна быть уменьшена с O(n log n) до O(n)
        //
        //Из-за пункта со сложностью, я понял задание так, что требуется преобразовывать хранимый TreeSet
        //в другую структуру данных. Ведь просто возвращение TreeSet даст сложность O(1)
        return new LinkedList<>(sortedTasks);
    }

    private void correctIntersections() {
        LocalDateTime endOfLastCheckedTask = null;
        for (Task task : sortedTasks) {
            if (task.getStartTime() == null) {
                break;
            }
            if (task instanceof EpicTask) {
                continue;
            }
            if (endOfLastCheckedTask == null) {
                endOfLastCheckedTask = task.getEndTime();
                continue;
            }
            LocalDateTime startTime = task.getStartTime();
            if (startTime.isBefore(endOfLastCheckedTask)) {
                task.setStartTime(endOfLastCheckedTask);
            }
            endOfLastCheckedTask = task.getEndTime();
        }
        for (Integer taskId : epicTasks.keySet()) {
            updateEpicTime(taskId);
        }
    }

    @Override
    public String toString() {
        return "Manager{"
                + "nextId="
                + nextId
                + ", \ntasks="
                + tasks
                + ", \nepicTasks="
                + epicTasks
                + ", \nsubtasks="
                + subtasks
                + "\nhistoryManager=" +
                historyManager.getHistory() + "}";
    }
}
