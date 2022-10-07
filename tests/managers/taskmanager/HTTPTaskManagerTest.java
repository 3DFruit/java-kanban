package managers.taskmanager;


import managers.Managers;
import managers.servers.KVServer;
import org.junit.jupiter.api.*;
import tasks.EpicTask;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    KVServer server;
    @Override
    protected HTTPTaskManager createManager() {
        try {
            server = new KVServer();
            server.start();
            return (HTTPTaskManager) Managers.getDefault();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @AfterEach
    void stopServer() {
        server.stop(1);
    }

    @Test
    void loadStateTasksTest() {
        Task task1 = new Task("task1", "good description", Status.NEW, 15, null);
        Task task2 = new Task("task2", "very good description", Status.IN_PROGRESS, 30,
                LocalDateTime.of(2022, 10, 20, 15, 20));
        manager.addTask(task1);
        manager.addTask(task2);
        try {
            TaskManager loadedManager = Managers.getDefault();
            assertEquals(manager.getTasks(), loadedManager.getTasks());
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loadStateEpicsTest() {
        EpicTask epic = new EpicTask("Epic1", "very epic description");
        manager.addTask(epic);
        manager.addTask(new EpicTask("epic2", "test description"));
        try {
            TaskManager loadedManager = Managers.getDefault();
            assertEquals(manager.getEpicTasks(), loadedManager.getEpicTasks());
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loadStateSubtasksTest() {
        EpicTask epic = new EpicTask("Epic1", "very epic description");
        int epicId = manager.addTask(epic);
        new Subtask("Sub1", "desc1", Status.NEW, 15,
                LocalDateTime.of(2023, 1, 13, 8, 0), epicId);
        new Subtask("Sub2", "desc2", Status.IN_PROGRESS, 45,
                LocalDateTime.of(2022, 11, 12, 15, 30), epicId);
        new Subtask("Sub3", "desc3", Status.IN_PROGRESS, 120,
                LocalDateTime.of(2022, 11, 11, 8, 30), epicId);
        try {
            TaskManager loadedManager = Managers.getDefault();
            assertEquals(manager.getSubtasks(), loadedManager.getSubtasks());
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loadStateHistoryTest() {
        Task task1 = new Task("task1", "good description", Status.NEW, 15, null);
        EpicTask epic = new EpicTask("Epic1", "very epic description");
        manager.addTask(task1);
        int epicId = manager.addTask(epic);
        Subtask sub1 = new Subtask("Sub1", "desc1", Status.NEW, 15,
                LocalDateTime.of(2023, 1, 13, 8, 0), epicId);
        manager.addTask(sub1);
        manager.getTaskById(task1.getId());
        manager.getEpicTaskById(epicId);
        manager.getSubtaskById(sub1.getId());
        try {
            TaskManager loadedManager = Managers.getDefault();
            assertEquals(manager.getHistory(), loadedManager.getHistory());
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}