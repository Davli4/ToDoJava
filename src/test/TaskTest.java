package test;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void taskWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task1", "Something about1");
        Task task2 = new Task("Task2", "Something about2");
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
        Assertions.assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    public void taskWithDifferentIdShouldBeEqual() {
        Task task1 = new Task("Task1", "Something about1");
        Task task2 = new Task("Task2", "Something about2");
        task1.setId(1);
        task2.setId(2);
        Assertions.assertNotEquals(task1, task2);
        Assertions.assertNotEquals(task1.hashCode(), task2.hashCode());
    }
}
