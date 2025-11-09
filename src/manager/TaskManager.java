package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;


public interface TaskManager {
    void addTask(Task var1);

    List<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskById(int var1);

    void updateTask(Task var1);

    void removeTaskById(int var1);

    void addEpic(Epic var1);

    List<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpicById(int var1);

    void updateEpic(Epic var1);

    void removeEpicById(int var1);

    void addSubTask(SubTask var1);

    List<SubTask> getAllSubTasks();

    void removeAllSubTasks();

    SubTask getSubTaskById(int var1);

    void updateSubTask(SubTask var1);

    void removeSubTaskById(int var1);

    List<SubTask> getSubTasksByEpicId(int var1);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean isTasksOverlap(Task var1, Task var2);
}
