package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {

    EpicTask epicTask;

    @BeforeEach
    void createTask() {
        epicTask = new EpicTask(0, "test epic", "test description");
    }

    @Test
    void clearSubtasksTest() {
        epicTask.addSubtask(1);
        epicTask.addSubtask(15);
        epicTask.addSubtask(25);
        epicTask.addSubtask(33);
        epicTask.clearSubtasks();
        assertArrayEquals(new Integer[0], epicTask.getSubtasks().toArray(new Integer[0]));
    }

    @Test
    void removeSubtaskTest() {
        epicTask.addSubtask(1);
        epicTask.addSubtask(15);
        epicTask.addSubtask(25);
        epicTask.addSubtask(33);
        epicTask.removeSubtask(25);
        assertArrayEquals(new Integer[]{1, 15, 33}, epicTask.getSubtasks().toArray(new Integer[0]));
    }

    @Test
    void removeSubtaskIncorrectId() {
        epicTask.addSubtask(1);
        epicTask.addSubtask(15);
        epicTask.addSubtask(25);
        epicTask.addSubtask(33);
        epicTask.removeSubtask(26);
        assertArrayEquals(new Integer[]{1, 15, 25, 33}, epicTask.getSubtasks().toArray(new Integer[0]));
    }

    @Test
    void addSubtaskTest() {
        epicTask.addSubtask(1);
        epicTask.addSubtask(15);
        assertArrayEquals(new Integer[]{1, 15}, epicTask.getSubtasks().toArray(new Integer[0]));
    }
}