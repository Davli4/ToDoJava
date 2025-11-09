import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.io.File;


public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        //Ниже код это не опционалдьно заданрие, а я испольщзовал для своей отладки

       /*
        File testFile = new File("test_tasks.csv");
        System.out.println("Файл для теста: " + testFile.getAbsolutePath());

        FileBackedTaskManager manager = new FileBackedTaskManager(testFile);


        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(TaskStatus.IN_PROGRESS);
        manager.addTask(task);



        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        manager.addEpic(epic);

        SubTask subTask = new SubTask("Тестовая подзадача", "Описание подзадачи", epic.getId());
        subTask.setStatus(TaskStatus.DONE);
        manager.addSubTask(subTask);

    }*/
    }
}



