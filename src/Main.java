import managers.taskmanager.FileBackedTasksManager;
import managers.taskmanager.TaskManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager.main(args);
        System.out.println("\nЗагруженный FileBackedTasksManager:");
        TaskManager manager = FileBackedTasksManager.loadFromFile(new File("resources/history.csv"));
        System.out.println(manager);
    }
}
