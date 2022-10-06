import managers.servers.KVServer;
import managers.taskmanager.KVTaskClient;

public class Main {

    public static void main(String[] args) {

        try {
            new KVServer().start();
            KVTaskClient client = new KVTaskClient("http://localhost:8078");
            client.put("one", "punch man");
            client.put("two", "morrow");
            System.out.println(client.load("one"));
            System.out.println(client.load("two"));
            client.put("one", "saitama");
            System.out.println(client.load("one"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            HttpTaskServer server = new HttpTaskServer(new InetSocketAddress(8080));
            server.start();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }*/
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
