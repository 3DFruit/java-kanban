import managers.taskmanager.FileBackedTasksManager;
import managers.taskmanager.TaskManager;
import tasks.Task;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager.main(args);
        System.out.println("\nЗагруженный FileBackedTasksManager:");
        TaskManager manager = FileBackedTasksManager.loadFromFile(new File("resources/history.csv"));
        //System.out.println(manager);

        List<Task> tasks = manager.getPrioritizedTasks();
        System.out.println("Отсортированные задачи:");
        for (Task task : tasks) {
            System.out.println(task.toString());
        }
    }
}
