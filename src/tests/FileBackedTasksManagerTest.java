package tests;

import managers.taskmanager.FileBackedTasksManager;
import managers.taskmanager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Override
    public FileBackedTasksManager createManager() {
        return getClearManager(new File("resources/testFileBackedManager.csv"));
    }

    @Test
    public void testLoadEmptyHistory() {
        manager = FileBackedTasksManager.loadFromFile(new File("resources/testEmptyHistory.csv"));
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void testLoadEmptyList() {
        manager = FileBackedTasksManager.loadFromFile(new File("resources/testEmptyList.csv"));
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void testLoadEpicWithoutSubtasksList() {
        manager = FileBackedTasksManager.loadFromFile(new File("resources/testEpicWithoutSubtasks.csv"));
        assertEquals(0, manager.getSubtasksOfEpic(2).size());
    }

    @Test
    public void testSaveEmptyHistory() {
        File file = new File("resources/testEmptyHistory.csv");
        manager = getClearManager(file);
        manager.addTask(new Task("task1", "good description", Status.NEW, 15, LocalDateTime.now()));
        manager.addTask(new Task("task2", "very good description", Status.IN_PROGRESS, 15, LocalDateTime.now()));
        int epicId = manager.addTask(new EpicTask("Epic1", "very epic description"));
        manager.addTask(new Subtask("Sub1", "desc1", Status.NEW, 15, LocalDateTime.now(), epicId));
        manager.addTask(new Subtask("Sub2", "desc2", Status.IN_PROGRESS, 15, LocalDateTime.now(), epicId));
        manager.addTask(new Subtask("Sub3", "desc3", Status.IN_PROGRESS, 15, LocalDateTime.now(), epicId));
        manager.addTask(new EpicTask("Epic2", "epic description"));

        TaskManager savedManager = new FileBackedTasksManager(file.getPath());
        assertEquals(0, savedManager.getHistory().size());
    }

    @Test
    public void testSaveEmptyList() {
        File file = new File("resources/testEmptyHistory.csv");
        manager = getClearManager(file);
        int epicId = manager.addTask(new EpicTask("Epic1", "very epic description"));
        manager.addTask(new Subtask("Sub1", "desc1", Status.NEW, 15, LocalDateTime.now(), epicId));
        manager.addTask(new Subtask("Sub2", "desc2", Status.IN_PROGRESS, 15, LocalDateTime.now(), epicId));
        manager.addTask(new Subtask("Sub3", "desc3", Status.IN_PROGRESS, 15, LocalDateTime.now(), epicId));
        manager.addTask(new EpicTask("Epic2", "epic description"));

        TaskManager savedManager = new FileBackedTasksManager(file.getPath());
        assertEquals(0, savedManager.getTasks().size());
    }

    @Test
    public void testSaveEpicWithoutSubtasksList() {
        manager = getClearManager(new File("resources/testEpicWithoutSubtasks.csv"));
        File file = new File("resources/testEmptyHistory.csv");
        manager = getClearManager(file);
        manager.addTask(new Task("task1", "good description", Status.NEW, 15, LocalDateTime.now()));
        manager.addTask(new Task("task2", "very good description", Status.IN_PROGRESS, 15, LocalDateTime.now()));
        int epicId = manager.addTask(new EpicTask("Epic1", "very epic description"));

        TaskManager savedManager = new FileBackedTasksManager(file.getPath());
        assertEquals(0, savedManager.getSubtasksOfEpic(epicId).size());
    }

    private FileBackedTasksManager getClearManager(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file.getPath());
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpicTasks();
        return manager;
    }
}