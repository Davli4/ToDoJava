package test;

import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    void shouldCreateTask() {
        Task task = new Task("Test Task", "Test Description");
        taskManager.addTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask);
        assertEquals(task, savedTask);
    }

    @Test
    void shouldReturnNullForNonExistentTask() {
        assertNull(taskManager.getTaskById(999));
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Original", "Description");
        taskManager.addTask(task);
        Task updated = new Task("Updated", "New Description");
        updated.setId(task.getId());
        taskManager.updateTask(updated);
        assertEquals("Updated", taskManager.getTaskById(task.getId()).getName());
    }

    @Test
    void shouldRemoveTask() {
        Task task = new Task("To remove", "Description");
        taskManager.addTask(task);
        taskManager.removeTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void shouldRemoveAllTasks() {
        taskManager.addTask(new Task("Task 1", "Desc"));
        taskManager.addTask(new Task("Task 2", "Desc"));
        taskManager.removeAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void shouldCreateEpic() {
        Epic epic = new Epic("Test Epic", "Test Description");
        taskManager.addEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(savedEpic);
        assertEquals(epic, savedEpic);
        assertEquals(TaskStatus.NEW, savedEpic.getStatus());
    }

    @Test
    void shouldCreateSubTask() {
        Epic epic = new Epic("Parent Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "Test Description", epic.getId());
        taskManager.addSubTask(subTask);
        SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());
        assertNotNull(savedSubTask);
        assertEquals(subTask, savedSubTask);
        assertEquals(epic.getId(), savedSubTask.getEpicId());
    }

    @Test
    void shouldNotAddSubTaskWithInvalidEpicId() {
        SubTask subTask = new SubTask("SubTask", "Description", 999);
        taskManager.addSubTask(subTask);
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @Test
    void shouldUpdateEpicStatusWhenSubTasksChange() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Description", epic.getId());
        subTask.setStatus(TaskStatus.DONE);
        taskManager.addSubTask(subTask);
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.DONE, savedEpic.getStatus());
    }

    @Test
    void shouldNotAddNullTasks() {
        taskManager.addTask((Task)null);
        taskManager.addEpic((Epic)null);
        taskManager.addSubTask((SubTask)null);
        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @Test
    void shouldGenerateUniqueIds() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Description");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void shouldAddTasksToHistory() {
        Task task = new Task("Task", "Description");
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        assertEquals(1, taskManager.getHistory().size());
        assertEquals(task, taskManager.getHistory().get(0));
    }

    @Test
    void shouldReturnAllTasks() {
        Task task = new Task("Task", "Description");
        Epic epic = new Epic("Epic", "Description");
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        List<Task> tasks = taskManager.getAllTasks();
        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(1, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(task, tasks.get(0));
        assertEquals(epic, epics.get(0));
    }

    @Test
    void shouldRemoveAllEpicsAndSubTasks() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Description", epic.getId());
        taskManager.addSubTask(subTask);
        taskManager.removeAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @Test
    void shouldRemoveAllSubTasksAndUpdateEpics() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Description", epic.getId());
        taskManager.addSubTask(subTask);
        taskManager.removeAllSubTasks();
        assertTrue(taskManager.getAllSubTasks().isEmpty());
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertTrue(savedEpic.getSubtaskIds().isEmpty());
        assertEquals(TaskStatus.NEW, savedEpic.getStatus());
    }

    @Test
    void shouldGetSubTasksByEpicId() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "Description", epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description", epic.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        List<SubTask> epicSubTasks = taskManager.getSubTasksByEpicId(epic.getId());
        assertEquals(2, epicSubTasks.size());
        assertTrue(epicSubTasks.contains(subTask1));
        assertTrue(epicSubTasks.contains(subTask2));
    }

    @Test
    void shouldReturnEmptyListForNonExistentEpicSubTasks() {
        List<SubTask> subTasks = taskManager.getSubTasksByEpicId(999);
        assertTrue(subTasks.isEmpty());
    }

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Different description");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void shouldHandleMultipleTaskTypesInHistory() {
        Task task = new Task("Task", "SomethingTask");
        Epic epic = new Epic("Epic", "SomethingEpic");
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertTrue(history.contains(task));
        assertTrue(history.contains(epic));
    }

    @Test
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task1 = new Task("Task1", "Description1");
        taskManager.addTask(task1);
        task1.setId(1);

        taskManager.getTaskById(task1.getId());
        assertEquals(1, taskManager.getHistory().size());

        taskManager.removeTaskById(task1.getId());

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldNotDuplicateTasksHistory() {
        Task task = new Task("Task1", "Description1");

        taskManager.addTask(task);

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());

        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void shouldNotKeepInvalidSubtaskIdsInEpic() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask1", "Description", epic.getId());
        SubTask subTask2 = new SubTask("SubTask2", "Description", epic.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.removeSubTaskById(subTask1.getId());

        assertFalse(epic.getSubtaskIds().contains(subTask1.getId()));
        assertTrue(epic.getSubtaskIds().contains(subTask2.getId()));
    }

    @Test
    void shouldCleanEpicWhenAllSubtasksRemoved() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask1", "Description", epic.getId());
        SubTask subTask2 = new SubTask("SubTask2", "Description", epic.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.removeAllSubTasks();

        assertTrue(epic.getSubtaskIds().isEmpty());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldNotAllowSubtaskToChangeEpicToNonExistent() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("SubTask", "Description", epic.getId());
        taskManager.addSubTask(subTask);

        subTask.setEpicId(999);

        taskManager.updateSubTask(subTask);

        assertNotNull(taskManager.getSubTaskById(subTask.getId()));
    }

    @Test
    void epicStatusShouldBeNewWhenAllSubTasksNew() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask1", "Description", epic.getId());
        subTask1.setStatus(TaskStatus.NEW);
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask2", "Description", epic.getId());
        subTask2.setStatus(TaskStatus.NEW);
        taskManager.addSubTask(subTask2);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.NEW, savedEpic.getStatus());
    }

    @Test
    void epicStatusShouldBeDoneWhenAllSubTasksDone() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask1", "Description", epic.getId());
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask2", "Description", epic.getId());
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.addSubTask(subTask2);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.DONE, savedEpic.getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressWhenAllSubTasksNewAndDone() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask1", "Description", epic.getId());
        subTask1.setStatus(TaskStatus.NEW);
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask2", "Description", epic.getId());
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.addSubTask(subTask2);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressWhenAnySubtaskInProgress() {
        Epic epic = new Epic("Epic", "Test epic");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description", epic.getId());
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "Description", epic.getId());
        subTask2.setStatus(TaskStatus.NEW);
        taskManager.addSubTask(subTask2);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressWhenAllSubtasksInProgress() {
        Epic epic = new Epic("Epic", "Test epic");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description", epic.getId());
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "Description", epic.getId());
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubTask(subTask2);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus());
    }

    @Test
    void subtaskShouldHaveValidEpicReference() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Test SubTask", "Description", epic.getId());
        taskManager.addSubTask(subTask);

        SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());
        assertNotNull(savedSubTask);
        assertEquals(epic.getId(), savedSubTask.getEpicId());

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertTrue(savedEpic.getSubtaskIds().contains(subTask.getId()));
    }

    @Test
    void shouldRemoveAllSubtasksWhenEpicDeleted() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description", epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description", epic.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(2, taskManager.getAllSubTasks().size());

        taskManager.removeEpicById(epic.getId());

        assertTrue(taskManager.getAllSubTasks().isEmpty());
        assertNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    void subtaskCannotReferenceNonExistentEpic() {
        SubTask subTask = new SubTask("SubTask", "Description", 999);
        taskManager.addSubTask(subTask);
        assertTrue(taskManager.getAllSubTasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldDetectTaskOverlap(){
        Task task1 = new Task("Task 1", "Description");
        task1.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        task1.setDuration(Duration.ofHours(2));

        taskManager.addTask(task1);


        Task task2 = new Task("Task 2", "Description");
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 11, 0)); // Пересекается с task1
        task2.setDuration(Duration.ofHours(1));

        assertTrue(taskManager.isTasksOverlap(task1, task2));
    }

    @Test
    void shouldNotDetectTaskNonOverlap(){
        Task task1 = new Task("Task 1", "Description");
        task1.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        task1.setDuration(Duration.ofHours(1));

        taskManager.addTask(task1);

        Task task2 = new Task("Task 2", "Description");
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0)); // Пересекается с task1
        task2.setDuration(Duration.ofHours(1));

        assertFalse(taskManager.isTasksOverlap(task1, task2));
    }

}