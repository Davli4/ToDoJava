package manager;

import model.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.loadFromFile();
        return manager;
    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }


    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subtask) {
        super.addSubTask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    private String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                TaskType.TASK,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                "",
                task.getDuration() != null ? task.getDuration().toMinutes() : "",
                task.getStartTime() != null ? task.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
    }

    private String toString(SubTask subTask) {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                subTask.getId(),
                TaskType.SUBTASK,
                subTask.getName(),
                subTask.getStatus(),
                subTask.getDescription(),
                subTask.getEpicId(),
                subTask.getDuration() != null ? subTask.getDuration().toMinutes() : "",
                subTask.getStartTime() != null ? subTask.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
    }



    private String toString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                epic.getId(),
                TaskType.EPIC,
                epic.getName(),
                epic.getStatus(),
                epic.getDescription(),
                "",
                epic.getDuration() != null ? epic.getDuration().toSeconds() : "",
                epic.getStartTime() != null ? epic.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
    }

    private  void loadFromFile() {
        try {

            if (!file.exists()) {
                return;
            }

            List<String> lines = Files.readAllLines(file.toPath());
            boolean flag = true;

            for (String line : lines) {
                if (flag) {
                    flag = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                Task task = fromString(line);
                if (task != null) {
                    if (task instanceof SubTask) {
                        super.addSubTask((SubTask) task);
                    } else if (task instanceof Epic) {
                        super.addEpic((Epic) task);
                    } else if (task instanceof Task) {
                        super.addTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error while loading file", e);
        }
    }



    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic,duration,startTime\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (SubTask subTask : getAllSubTasks()) {
                writer.write(toString(subTask) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error while saving file", e);
        }


    }

    private static Task fromString(String str) {
        if (str.startsWith("id,type,name,status,description,epic")) {
            return null;
        }
        String[] parts = str.split(",");

        try {
            int id = Integer.parseInt(parts[0]);
            TaskType type = TaskType.valueOf(parts[1]);
            String name = parts[2];
            TaskStatus status = TaskStatus.valueOf(parts[3]);
            String description = parts[4];

            Duration duration = null;
            LocalDateTime startTime = null;

            if(parts.length > 6 && !parts[6].isEmpty()) {
                long sec =  Long.parseLong(parts[6]);
                duration = Duration.ofSeconds(sec);
            }

            if(parts.length > 7 && !parts[7].isEmpty()) {
                startTime = LocalDateTime.parse(parts[7], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            switch (type) {
                case TASK:
                    Task task = new Task(name, description);
                    task.setId(id);
                    task.setStatus(status);
                    task.setDuration(duration);
                    task.setStartTime(startTime);
                    return task;

                case SUBTASK:
                    int epicId = Integer.parseInt(parts[5].trim());
                    SubTask subTask = new SubTask(name, description, epicId);
                    subTask.setId(id);
                    subTask.setStatus(status);
                    subTask.setDuration(duration);
                    subTask.setStartTime(startTime);
                    return subTask;
                case EPIC:
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    epic.setDuration(duration);
                    epic.setStartTime(startTime);
                    return epic;
                default:
                    throw new RuntimeException("Invalid task type" + type);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number type" + str, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid string type" + str, e);
        }
    }

}
