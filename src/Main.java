import managers.taskmanager.HttpTaskServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {

        try {
            HttpTaskServer server = new HttpTaskServer(new InetSocketAddress(8080));
            server.start();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        /*FileBackedTasksManager.main(args);
        System.out.println("\nЗагруженный FileBackedTasksManager:");
        TaskManager manager = Managers.loadFromFile(new File("resources/history.csv"));
        System.out.println(manager);

        List<Task> tasks = manager.getPrioritizedTasks();
        System.out.println("Отсортированные задачи:");
        for (Task task : tasks) {
            System.out.println(task.toString());
        }*/
    }
}
