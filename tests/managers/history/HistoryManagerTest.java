package managers.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager;
    Task task;

    @BeforeEach
    void setManager() {
        historyManager = new InMemoryHistoryManager(10);
        task = new Task(0, "Test task for history", "test description", Status.NEW,
                15, LocalDateTime.now());
    }


    @Test
    void addTest() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История инициализирована.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addRepeatTest() {
        historyManager.add(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История инициализирована.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void getEmptyHistoryTest() {
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История инициализирована.");
        assertEquals(0, history.size(), "История пустая.");
    }

    @Test
    void removeTest() {
        Task[] tasks = new Task[7];
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new Task(i, "Test task" + i + " for history", "test description", Status.NEW,
                    15, LocalDateTime.now());
            historyManager.add(tasks[i]);
        }

        //удаление из конца
        int taskIndex = tasks.length - 1;
        int taskId = tasks[taskIndex].getId();
        historyManager.remove(taskId);
        List<Task> history = historyManager.getHistory();
        assertEquals(tasks.length - 1, history.size(), "размер списка верный");
        assertFalse(history.contains(tasks[taskIndex]), "таск отсутствует в истории");

        //удаление из середины
        taskIndex = tasks.length / 2;
        taskId = tasks[taskIndex].getId();
        historyManager.remove(taskId);
        history = historyManager.getHistory();
        assertEquals(tasks.length - 2, history.size(), "размер списка верный");
        assertFalse(history.contains(tasks[taskIndex]), "таск отсутствует в истории");

        //удаление из начала
        taskIndex = 0;
        taskId = tasks[taskIndex].getId();
        historyManager.remove(taskId);
        history = historyManager.getHistory();
        assertEquals(tasks.length - 3, history.size(), "размер списка верный");
        assertFalse(history.contains(tasks[taskIndex]), "таск отсутствует в истории");
    }
}