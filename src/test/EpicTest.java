package test;


import model.Epic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EpicTest {
    @Test
    void epicWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Epic 1", "Something");
        Epic epic2 = new Epic("Epic 2", "Something 2");
        epic1.setId(1);
        epic2.setId(1);
        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    public void shouldCalculateStatusForEpic() {
        Epic epic = new Epic("Epic1", "Description");
        epic.setId(1);
        Assertions.assertEquals(0, epic.getSubtaskIds().size());
    }
}
