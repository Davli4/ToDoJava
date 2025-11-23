package test;

import manager.FileBackedTaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @TempDir
    Path tempDir;
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = Files.createTempFile(tempDir, "test", ".csv").toFile();
        this.taskManager = new FileBackedTaskManager(testFile);
    }

    @Test
    void shouldSaveMultipleTasks()  {
        Task task = new Task("TestTask", "TestDescription");
        Epic epic = new Epic("TestEpic", "TestDescription");
        SubTask subTask = new SubTask("TestSubTask", "TestDescription", 2);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        assertTrue(testFile.exists(), "Файл должен существовать");
        assertTrue(testFile.length() > 0, "Файл не должен быть пустым");

        try {
            String content = Files.readString(testFile.toPath());
            assertTrue(content.contains("TASK"), "Файл должен содержать задачи");
            assertTrue(content.contains("EPIC"), "Файл должен содержать эпики");
            assertTrue(content.contains("SUBTASK"), "Файл должен содержать подзадачи");
        } catch (IOException e) {
            fail("Не удалось прочитать файл", e);
        }
    }

    @Test
    void shouldSaveTaskStatus() {
        Task task = new Task("Test Task", "Description");
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Task loadedTask = loadedManager.getAllTasks().get(0);

        assertEquals(TaskStatus.IN_PROGRESS, loadedTask.getStatus(), "Статус должен сохраниться");
    }


    @Test
    void shouldThrowExceptionWhenFileHasInvalidTaskData() throws IOException {
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("id,type,name,status,description,epic\n");
            writer.write("abc,TASK,Invalid Task,NEW,Description,\n");
        }

        assertThrows(RuntimeException.class, () -> {
            FileBackedTaskManager.loadFromFile(testFile);
        });
    }

    @Test
    void shouldHandleEmptyFileWithoutException() throws IOException {
        Files.write(testFile.toPath(), new byte[0]);

        assertDoesNotThrow(() -> {
            FileBackedTaskManager.loadFromFile(testFile);
        });
    }
}