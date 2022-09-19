package managers.taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    protected abstract T createManager();

    @BeforeEach
    void getManager(){
        manager = createManager();
        manager.addTask(new Task("task1", "good description", Status.NEW));
        manager.addTask(new Task("task2", "very good description", Status.IN_PROGRESS));
        int epicId = manager.addTask(new EpicTask("Epic1", "very epic description"));
        manager.addTask(new Subtask("Sub1", "desc1", Status.NEW, epicId));
        manager.addTask(new Subtask("Sub2", "desc2", Status.IN_PROGRESS, epicId));
        manager.addTask(new Subtask("Sub3", "desc3", Status.IN_PROGRESS, epicId));
        manager.addTask(new EpicTask("Epic2", "epic description"));
    }

    @Test
    void shouldReturnEmptyListAfterRemoveAllSubtasks() {
        manager.removeAllSubtasks();
        Assertions.assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void shouldReturnEmptyListAfterRemoveAllEpicTasks() {
        manager.removeAllEpicTasks();
        Assertions.assertEquals(0, manager.getEpicTasks().size());
    }

    @Test
    void shouldReturnEmptyListAfterRemoveAllTasks() {
        manager.removeAllTasks();
        Assertions.assertEquals(0, manager.getTasks().size());
    }

    @Test
    void shouldReturn2AfterGetTasks() {
        Assertions.assertEquals(2, manager.getTasks().size());
    }

    @Test
    void shouldReturn2AfterGetEpicTasks() {
        Assertions.assertEquals(2, manager.getEpicTasks().size());
    }

    @Test
    void shouldReturn3AfterGetSubtasks() {
        Assertions.assertEquals(3, manager.getSubtasks().size());
    }

    @Test
    void getTaskById() {
        Task task = new Task("test task", "" , Status.NEW);
        int id = manager.addTask(task);
        Assertions.assertEquals(task, manager.getTaskById(id));
    }

    @Test
    void getEpicTaskById() {
        EpicTask task = new EpicTask("test task", "" );
        int id = manager.addTask(task);
        Assertions.assertEquals(task, manager.getEpicTaskById(id));
    }

    @Test
    void getSubtaskById() {
        int epicId = manager.addTask(new EpicTask("test EpicTask", "" ));
        Subtask task = new Subtask("test task", "" , Status.NEW, epicId);
        int id = manager.addTask(task);
        Assertions.assertEquals(task, manager.getSubtaskById(id));
    }

    @Test
    void addTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = manager.addTask(task);

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void testAddTask() {
    }

    @Test
    void testAddTask1() {
    }

    @Test
    void removeTaskById() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void getSubtasksOfEpic() {
    }

    @Test
    void getHistory() {
    }
}