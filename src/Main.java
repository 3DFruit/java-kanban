import managers.Managers;
import managers.servers.KVServer;
import managers.taskmanager.TaskManager;
import tasks.EpicTask;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        KVServer server;
        try {
            server = new KVServer();
            server.start();
            TaskManager manager = Managers.getDefault();
            Task task1 = new Task("task1", "good description", Status.NEW, 15, null);
            Task task2 = new Task("task2", "very good description", Status.IN_PROGRESS, 30,
                    LocalDateTime.of(2022, 10, 20, 15, 20));
            EpicTask epic = new EpicTask("Epic1", "very epic description");
            manager.addTask(task1);
            manager.addTask(task2);
            int epicId = manager.addTask(epic);
            Subtask sub1 = new Subtask("Sub1", "desc1", Status.NEW, 15,
                    LocalDateTime.of(2023, 1, 13, 8, 0), epicId);
            Subtask sub2 = new Subtask("Sub2", "desc2", Status.IN_PROGRESS, 45,
                    LocalDateTime.of(2022, 11, 12, 15, 30), epicId);
            Subtask sub3 = new Subtask("Sub3", "desc3", Status.IN_PROGRESS, 120,
                    LocalDateTime.of(2022, 11, 11, 8, 30), epicId);
            manager.addTask(sub1);
            manager.addTask(sub2);
            manager.addTask(sub3);
            manager.getTaskById(task1.getId());
            manager.getEpicTaskById(epicId);
            System.out.println("Созданный менеджер");
            System.out.println(manager);
            TaskManager loadedManager = Managers.getDefault();
            System.out.println("Загруженный менеджер");
            System.out.println(loadedManager);
            server.stop(1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
