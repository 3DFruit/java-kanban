public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.addNewTask(new Task("task1","good description", Status.NEW));
        manager.addNewTask(new Task("task2","very good description", Status.IN_PROGRESS));
        manager.addNewTask(new EpicTask("Epic1", "very epic description"));
        manager.addNewTask(new Subtask("Sub1", "desc1", Status.NEW, 2));
        manager.addNewTask(new Subtask("Sub2", "desc2", Status.IN_PROGRESS, 2));
        manager.addNewTask(new EpicTask("Epic2", "epic description"));
        manager.addNewTask(new Subtask("Sub3", "desc3", Status.NEW, 5));
        System.out.println(manager);
        manager.updateTask(0, new Task("task2","very good description", Status.IN_PROGRESS));
        manager.updateTask(1, new Task("task2","very good description", Status.DONE));
        manager.updateTask(2, new EpicTask("Epic1", "new very epic description"));
        manager.updateTask(3, new Subtask("Sub1", "desc1", Status.IN_PROGRESS, 2));
        manager.updateTask(4, new Subtask("Sub2", "desc2", Status.DONE, 2));
        manager.updateTask(5, new EpicTask("Epic2", "new epic description"));
        manager.updateTask(6, new Subtask("Sub3", "desc3", Status.DONE, 5));
        System.out.println(manager);
        manager.removeTaskById(0);
        manager.removeTaskById(2);
        System.out.println(manager);
    }
}
