import managers.Managers;
import managers.taskmanager.TaskManager;
import tasks.EpicTask;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manager.addTask(new Task("task1","good description", Status.NEW));
        manager.addTask(new Task("task2","very good description", Status.IN_PROGRESS));
        manager.addTask(new EpicTask("Epic1", "very epic description"));
        manager.addTask(new Subtask("Sub1", "desc1", Status.NEW, 2));
        manager.addTask(new Subtask("Sub2", "desc2", Status.IN_PROGRESS, 2));
        manager.addTask(new EpicTask("Epic2", "epic description"));
        manager.addTask(new Subtask("Sub3", "desc3", Status.NEW, 5));
        System.out.println(manager.getHistory());
        manager.getTaskById(0);
        System.out.println(manager.getHistory());
        manager.getEpicTaskById(2);
        System.out.println(manager.getHistory());
        manager.getSubtaskById(3);
        System.out.println(manager.getHistory());
        manager.getEpicTaskById(5);
        System.out.println(manager.getHistory());
        //заполение истории
        for (int i = 0; i < 6; i++) {
            manager.getSubtaskById(6);
        }
        System.out.println(manager.getHistory());
        manager.getTaskById(1);
        System.out.println(manager.getHistory());
    }
}
