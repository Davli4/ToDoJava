package test;

import model.SubTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubTaskTest {
    @Test
    public void subTaskWithSameIdShouldBeEquals() {
        SubTask subTask1 = new SubTask("SubTask1", "Description1", 1);
        SubTask subTask2 = new SubTask("SubTask2", "Description2", 1);
        subTask1.setId(1);
        subTask2.setId(1);
        Assertions.assertEquals(subTask1, subTask2);
    }
}
