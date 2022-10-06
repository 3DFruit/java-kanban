package managers.taskmanager;


import managers.Managers;
import managers.servers.KVServer;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;

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
}