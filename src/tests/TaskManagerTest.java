package tests;

import managers.taskmanager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    protected abstract T createManager();

    @BeforeEach
    void getManager(){
        manager = createManager();
    }

    @Test
    void addTaskTest() {
        Task task = new Task("Test task", "test description", Status.NEW, 15, LocalDateTime.now());
        final int taskId = manager.addTask(task);

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "задача не найдена");
        assertEquals(task, savedTask, "задачи не совпадают");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addEpicTaskTest() {
        EpicTask epicTask = new EpicTask("Test epicTask", "test description");
        final int taskId = manager.addTask(epicTask);

        epicTask.setStatus(Status.NEW);

        final EpicTask savedTask = manager.getEpicTaskById(taskId);

        assertNotNull(savedTask, "задача не найдена");
        assertEquals(epicTask, savedTask, "задачи не совпадают");

        final List<EpicTask> epicTasks = manager.getEpicTasks();

        assertNotNull(epicTasks, "Задачи не возвращаются.");
        assertEquals(1, epicTasks.size(), "Неверное количество задач.");
        assertEquals(epicTask, epicTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addSubtaskTest() {
        Subtask subtask = new Subtask("Test subtask for history",
                "test description",
                Status.NEW,
                15,
                LocalDateTime.now(),
                0
        );
        int taskId = manager.addTask(subtask);

        assertEquals(-1, taskId, "неверный идентификатор эпика");

        EpicTask epicTask = new EpicTask("Test epicTask", "test description");
        final int epicId = manager.addTask(epicTask);
        subtask = new Subtask("Test subtask for history",
                "test description",
                Status.NEW,
                15,
                LocalDateTime.now(),
                epicId
        );

        taskId = manager.addTask(subtask);

        final Subtask savedTask = manager.getSubtaskById(taskId);

        assertNotNull(savedTask, "задача не найдена");
        assertEquals(subtask, savedTask, "задачи не совпадают");

        final List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpicStatusTest() {
        EpicTask epicTask = new EpicTask("TestStatus epicTask", "test description");
        final int epicId = manager.addTask(epicTask);
        //для пустой коллекции
        assertEquals(Status.NEW, manager.getEpicTaskById(epicId).getStatus());

        Subtask subtask = new Subtask("Test subtask",
                "test description",
                Status.NEW,
                15,
                LocalDateTime.now(),
                epicId
        );
        manager.addTask(subtask);
        //все подзадачи со статусом NEW
        assertEquals(Status.NEW, manager.getEpicTaskById(epicId).getStatus());

        subtask.setStatus(Status.DONE);
        manager.updateTask(epicTask);
        //все подзадачи со статусом DONE
        assertEquals(Status.DONE, manager.getEpicTaskById(epicId).getStatus());

        manager.addTask(new Subtask("Test subtask2",
                "test description",
                Status.NEW,
                15,
                LocalDateTime.now(),
                epicId
        ));
        //у подзадач статусы NEW и DONE
        assertEquals(Status.IN_PROGRESS, manager.getEpicTaskById(epicId).getStatus());

        manager.addTask(new Subtask("Test subtask3",
                "test description",
                Status.IN_PROGRESS,
                15,
                LocalDateTime.now(),
                epicId
        ));
        //у подзадач статусы NEW, DONE, IN_PROGRESS
        assertEquals(Status.IN_PROGRESS, manager.getEpicTaskById(epicId).getStatus());
    }

    @Test
    void updateEpicTimeTest() {
        EpicTask epicTask = new EpicTask("TestStatus epicTask", "test description");
        final int epicId = manager.addTask(epicTask);
        //для пустой коллекции
        assertEquals(0, manager.getEpicTaskById(epicId).getDuration());
        assertNull(manager.getEpicTaskById(epicId).getStartTime());
        assertNull(manager.getEpicTaskById(epicId).getEndTime());

        Subtask subtask = new Subtask("Test subtask",
                "test description",
                Status.NEW,
                15,
                LocalDateTime.of(2022, 9, 22, 22, 30),
                epicId
        );
        manager.addTask(subtask);
        assertEquals(15, manager.getEpicTaskById(epicId).getDuration());
        assertEquals(LocalDateTime.of(2022, 9, 22, 22, 30),
                manager.getEpicTaskById(epicId).getStartTime()
        );
        assertEquals(LocalDateTime
                .of(2022, 9, 22, 22, 30)
                .plus(Duration.ofMinutes(15)),
                manager.getEpicTaskById(epicId).getEndTime()
        );
        manager.addTask(new Subtask("Test subtask2",
                "test description",
                Status.NEW,
                40,
                LocalDateTime.of(2022, 9, 21, 14, 30),
                epicId
        ));

        assertEquals(55, manager.getEpicTaskById(epicId).getDuration());
        assertEquals(LocalDateTime.of(2022, 9, 21, 14, 30),
                manager.getEpicTaskById(epicId).getStartTime()
        );
        assertEquals(LocalDateTime
                        .of(2022, 9, 21, 14, 30)
                        .plus(Duration.ofMinutes(55)),
                manager.getEpicTaskById(epicId).getEndTime()
        );
    }

    @Test
    void getEmptyListOfTasks() {
        assertArrayEquals(new Task[0], manager.getTasks().toArray(new Task[0]));
    }

    @Test
    void getEmptyListOfEpicTasks() {
        assertArrayEquals(new EpicTask[0], manager.getEpicTasks().toArray(new EpicTask[0]));
    }

    @Test
    void getEmptyListOfSubtasks() {
        assertArrayEquals(new Subtask[0], manager.getSubtasks().toArray(new Subtask[0]));
    }

    @Test
    void updateTaskTest() {
        Task task = new Task("Test task",
                "test description",
                Status.NEW,
                15,
                LocalDateTime.now()
        );
        int taskId =  manager.addTask(task);
        Task updatedTask = new Task(taskId,
                "updated1 task",
                "test description",
                Status.IN_PROGRESS,
                15,
                LocalDateTime.now()
        );

        //стандартное поведение
        manager.updateTask(updatedTask);
        assertEquals(updatedTask, manager.getTaskById(taskId));

        //неверный идентификатор
        updatedTask = new Task(taskId + 1,
                "updated2 task",
                "test description",
                Status.IN_PROGRESS,
                15,
                LocalDateTime.now());
        taskId = manager.updateTask(updatedTask);
        assertEquals(-1, taskId);
    }

    @Test
    void getSubtasksOfEpicTest() {
        EpicTask epicTask = new EpicTask("Test epicTask", "test description");
        final int epicId = manager.addTask(epicTask);
        //для пустой коллекции
        assertArrayEquals(new Subtask[0], manager.getSubtasksOfEpic(epicId).toArray(new Subtask[0]));

        List<Subtask> subtasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Subtask sub = new Subtask("Test subtask" + i,
                    "test description",
                    Status.NEW,
                    15,
                    LocalDateTime.now(),
                    epicId
            );
            int taskId = manager.addTask(sub);
            sub.setId(taskId);
            subtasks.add(sub);
        }

        assertArrayEquals(subtasks.toArray(), manager.getSubtasksOfEpic(epicId).toArray());
    }

    @Test
    void shouldReturnEmptyListAfterRemoveAllSubtasks() {
        manager.removeAllSubtasks();
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void shouldReturnEmptyListAfterRemoveAllEpicTasks() {
        manager.removeAllEpicTasks();
        assertEquals(0, manager.getEpicTasks().size());
    }

    @Test
    void shouldReturnEmptyListAfterRemoveAllTasks() {
        manager.removeAllTasks();
        assertEquals(0, manager.getTasks().size());
    }


    @Test
    void removeByIdTest() {
        assertEquals(-1, manager.removeTaskById(2));

        Task task = new Task("Test task", "test description", Status.NEW, 15, LocalDateTime.now());
        int taskId =  manager.addTask(task);
        manager.removeTaskById(taskId);
        assertEquals(0, manager.getTasks().size());
    }
}