package managers.taskmanager;

import managers.history.HistoryManager;
import tasks.EpicTask;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");
    private final File filePath;



    private FileBackedTasksManager() { //используется для тестирования
        super();
        this.filePath = new File("resources/history.csv");
    }

    public FileBackedTasksManager(File filePath) {
        super();
        try {
            if (filePath.createNewFile()) {
                System.out.println("Файл создан");
            }
        }
        catch  (IOException exception) {
            exception.printStackTrace();
        }
        this.filePath = filePath;
        load();
    }

    public static void main(String[] args) {
        TaskManager manager = new FileBackedTasksManager();
        manager.addTask(new Task("task1", "good description", Status.NEW, 15, null));
        manager.addTask(new Task("task2", "very good description", Status.IN_PROGRESS, 30, LocalDateTime.of(2022, 11, 12, 15, 20)));
        int epicId = manager.addTask(new EpicTask("Epic1", "very epic description"));
        manager.addTask(new EpicTask("Epic2", "epic description"));
        manager.addTask(new Subtask("Sub1", "desc1", Status.NEW, 15, LocalDateTime.of(2023, 1, 13, 8, 0), epicId));
        manager.addTask(new Subtask("Sub2", "desc2", Status.IN_PROGRESS, 45, LocalDateTime.of(2022, 11, 12, 15, 30), epicId));
        manager.addTask(new Subtask("Sub3", "desc3", Status.IN_PROGRESS, 120, LocalDateTime.of(2022, 11, 12, 15, 31), epicId));

        manager.getTaskById(0);
        manager.getEpicTaskById(2);
        manager.getTaskById(1);
        manager.getSubtaskById(4);
        manager.getSubtaskById(5);
        manager.getEpicTaskById(2);
        System.out.println("Созданный FileBackedTasksManager:");
        System.out.println(manager);
        List<Task> tasks = manager.getPrioritizedTasks();
        System.out.println("Отсортированные задачи:");
        for (Task task : tasks) {
            System.out.println(task.toString());
        }
    }

        private void load() throws ManagerSaveException {
        try {
            String dataStream = Files.readString(Path.of(filePath.toString()), StandardCharsets.UTF_8);
            if (dataStream.isBlank()) {
                return;
            }
            int index = dataStream.lastIndexOf("\n\n"); //индекс на котором заканчиваются данные тасков
            String[] taskLines = dataStream.substring(0, index).split("\r?\n");

            int maxId = -1; //следим за загружаемыми имндексами, чтобы корректно выдавать индексы после загрузки
            for (int i = 1; i < taskLines.length; i++) {
                String line = taskLines[i];
                Task task = fromString(line);
                int id = task != null ? task.getId() : -1;
                if (id > maxId) {
                    maxId = id + 1;
                }
                //метод addTask автоматически присваивает id, который может не совпадать с прочитанным
                //поэтому используем вспомогательный метод
                if (task != null) {
                    this.addTaskWithCustomId(task);
                }
            }
            nextId = Integer.max(maxId, 0);

            String historyDataStream = dataStream.substring(index + 2);
            if (historyDataStream.isBlank()) {
                return;
            }
            List<Integer> history = historyFromString(historyDataStream);
            for (Integer element : history) {
                if (subtasks.containsKey(element)) {
                    getSubtaskById(element);
                } else if (epicTasks.containsKey(element)) {
                    getEpicTaskById(element);
                } else if (tasks.containsKey(element)) {
                    getTaskById(element);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }
    }

    private void addTaskWithCustomId(Task task) {
        int id = task.getId();
        if (task instanceof EpicTask) {
            epicTasks.put(id, (EpicTask) task);
            updateEpicStatus(id);
            updateEpicTime(id);
            sortedTasks.add(task);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpicTaskId();
            if (epicTasks.containsKey(epicId)) {
                epicTasks.get(epicId).addSubtask(id);
                subtasks.put(id, subtask);
                updateEpicStatus(subtask.getEpicTaskId());
                updateEpicTime(subtask.getEpicTaskId());
                sortedTasks.add(subtask);
            }
        } else {
            task.setId(id);
            tasks.put(id, task);
            sortedTasks.add(task);
        }
    }

    private void save() throws ManagerSaveException {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,start_time,duration,epic\n");
            for (Task task : this.getTasks()) {
                writer.write(toString(task));
                writer.write("\n");
            }
            for (EpicTask task : this.getEpicTasks()) {
                writer.write(toString(task));
                writer.write("\n");
            }
            for (Subtask task : this.getSubtasks()) {
                writer.write(toString(task));
                writer.write("\n");
            }
            writer.write("\n");
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }
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
        super.removeAllEpicTasks();
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    private String toString(Task task) {
        return String.join(",",
                String.valueOf(task.getId()),
                task.getClass().getSimpleName().toUpperCase(),
                task.getTitle(),
                String.valueOf(task.getStatus()),
                task.getDescription(),
                task.getStartTime() == null ? "null" : task.getStartTime().format(DATE_TIME_FORMATTER),
                String.valueOf(task.getDuration()),
                String.valueOf(task instanceof Subtask ? ((Subtask) task).getEpicTaskId() : "")
        );
    }

    private Task fromString(String value) {
        String[] items = value.split(",");
        int id = Integer.parseInt(items[0]);
        TaskType type = TaskType.valueOf(items[1]);
        String name = items[2];
        Status status = Status.valueOf(items[3]);
        String description = items[4];
        LocalDateTime startTime = items[5].equals("null") ? null : LocalDateTime.parse(items[5], DATE_TIME_FORMATTER);
        long duration = Long.parseLong(items[6]);
        switch (type) {
            case SUBTASK:
                int epicId = Integer.parseInt(items[7]);
                return new Subtask(id, name, description, status, duration, startTime, epicId);
            case EPICTASK:
                return new EpicTask(id, name, description);
            case TASK:
                return new Task(id, name, description, status, duration, startTime);
            default:
                return null;
        }
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> list = manager.getHistory();
        StringBuilder builder = new StringBuilder();
        for (Task task : list) {
            builder.append(task.getId()).append(",");
        }
        //после цикла в конце остается лишняя запятая
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        String[] items = value.split(",");
        for (String item : items) {
            history.add(Integer.parseInt(item));
        }
        return history;
    }

    @Override
    public Task getTaskById(int id) {
        Task result = super.getTaskById(id);
        save();
        return result;
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        EpicTask result = super.getEpicTaskById(id);
        save();
        return result;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask result = super.getSubtaskById(id);
        save();
        return result;
    }
}
