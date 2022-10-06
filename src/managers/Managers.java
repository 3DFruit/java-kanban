package managers;

import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.taskmanager.HTTPTaskManager;
import managers.servers.KVServer;
import managers.taskmanager.TaskManager;

import java.io.IOException;

public class Managers {
    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HTTPTaskManager("http://localhost:" + KVServer.PORT);
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager(10);
    }
}
