import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.addNewTask(new Task("task1","good description", Status.NEW));
        manager.addNewTask(new Task("task2","very good description", Status.IN_PROGRESS));
        manager.addNewTask(new EpicTask("Epic1", "very epic description"));
        manager.addNewTask(new Subtask("Sub1", "desc1", Status.NEW, 2));
        manager.addNewTask(new Subtask("Sub2", "desc2", Status.NEW, 2));
        manager.addNewTask(new EpicTask("Epic2", "epic description"));
        manager.addNewTask(new Subtask("Sub3", "desc3", Status.NEW, 5));
        HashMap<Integer, EpicTask> epics = manager.getEpicTasks();
        EpicTask epic = epics.get(2);
        epic.addSubtask(15);
        System.out.println(manager);
    }
}
