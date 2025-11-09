package manager;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap();
    private final HashMap<Integer, Epic> epics = new HashMap();
    private final HashMap<Integer, SubTask> subtasks = new HashMap();
    private final HistoryManager historyManager;
    private int nextId = 1;
    private final Set<Task> prioritizeTasks = new TreeSet(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public void addTask(Task task) {
        if (task != null) {
            if(isTaskOverlappingWithExisting(task)) {
                throw new RuntimeException("Task already exists");
            }
            task.setId(this.generateId());
            this.tasks.put(task.getId(), task);
            addToPrioritized(task);
        }
    }

    public List<Task> getAllTasks() {
        return this.tasks.values().stream().collect(Collectors.toList());
    }

    public void removeAllTasks() {
        for (Task task : this.tasks.values()) {
            removeFromPrioritized(task);
        }
        this.tasks.clear();
    }

    public Task getTaskById(int id) {
        Task task = (Task)this.tasks.get(id);
        if (task != null) {
            this.historyManager.add(task);
        }
        return task;
    }

    public void updateTask(Task task) {
        if (task != null && this.tasks.containsKey(task.getId())) {
            if(isTaskOverlappingWithExisting(task)) {
                throw new RuntimeException("Task already exists");
            }
            removeFromPrioritized(this.tasks.get(task.getId()));
            this.tasks.put(task.getId(), task);
            addToPrioritized(task);
        }
    }

    public void removeTaskById(int id) {
        Task task = this.tasks.get(id);
        if (task != null) {
            removeFromPrioritized(task); // ДОБАВЛЕНО
            this.tasks.remove(id);
            historyManager.remove(id);
        }
    }

    public void addEpic(Epic epic) {
        if (epic != null) {
            epic.setId(this.generateId());
            this.epics.put(epic.getId(), epic);
        }
    }

    public List<Epic> getAllEpics() {
        return this.epics.values().stream().collect(Collectors.toList());
    }

    public void removeAllEpics() {
        for(Epic epic : this.epics.values()) {
            for(Integer subtaskId : epic.getSubtaskIds()) {
                SubTask subtask = this.subtasks.get(subtaskId);
                if (subtask != null) {
                    removeFromPrioritized(subtask);
                }
                this.subtasks.remove(subtaskId);
            }
        }
        this.epics.clear();
    }

    public Epic getEpicById(int id) {
        Epic epic = (Epic)this.epics.get(id);
        if (epic != null) {
            this.historyManager.add(epic);
        }
        return epic;
    }

    public void updateEpic(Epic epic) {
        if (epic != null && this.epics.containsKey(epic.getId())) {
            this.epics.put(epic.getId(), epic);
            this.updateEpicStatus(epic);
        }
    }

    public void removeEpicById(int id) {
        Epic epic = (Epic)this.epics.get(id);
        if (epic != null) {
            for(Integer subtaskId : epic.getSubtaskIds()) {
                SubTask subtask = this.subtasks.get(subtaskId);
                if (subtask != null) {
                    removeFromPrioritized(subtask);
                }
                this.subtasks.remove(subtaskId);
            }
            this.epics.remove(id);
        }
    }

    public void addSubTask(SubTask subTask) {
        if (subTask != null) {
            Epic epic = (Epic)this.epics.get(subTask.getEpicId());
            if (epic != null) {
                if(isTaskOverlappingWithExisting(subTask)) {
                    throw new RuntimeException("Task already exists");
                }
                subTask.setId(this.generateId());
                this.subtasks.put(subTask.getId(), subTask);
                epic.addSubtaskId(subTask.getId());
                if (subTask.getStartTime() != null && subTask.getDuration() != null) {
                    epic.calculateEndTime(
                            subTask.getStartTime(),
                            subTask.getDuration(),
                            subTask.getEndTime()
                    );
                }
                this.updateEpicStatus(epic);
                addToPrioritized(subTask);
            }
        }
    }

    public List<SubTask> getAllSubTasks() {
        return this.subtasks.values().stream().collect(Collectors.toList());
    }

    public void removeAllSubTasks() {
        for (SubTask subTask : this.subtasks.values()) {
            removeFromPrioritized(subTask);
        }

        for(Epic epic : this.epics.values()) {
            epic.getSubtaskIds().clear();
            epic.clearTime();
            this.updateEpicStatus(epic);
        }


        this.subtasks.clear();
    }

    public SubTask getSubTaskById(int id) {
        SubTask subTask = (SubTask)this.subtasks.get(id);
        if (subTask != null) {
            this.historyManager.add(subTask);
        }
        return subTask;
    }

    public void updateSubTask(SubTask subTask) {
        if (subTask != null && this.subtasks.containsKey(subTask.getId())) {
            if(isTaskOverlappingWithExisting(subTask)){
                throw new RuntimeException("Task already exists");
            }
            removeFromPrioritized(this.subtasks.get(subTask.getId()));
            this.subtasks.put(subTask.getId(), subTask);
            addToPrioritized(subTask);
            Epic epic = (Epic)this.epics.get(subTask.getEpicId());
            if (epic != null) {
                if (subTask.getStartTime() != null && subTask.getDuration() != null) {
                    epic.calculateEndTime(
                            subTask.getStartTime(),
                            subTask.getDuration(),
                            subTask.getEndTime()
                    );
                }
                this.updateEpicStatus(epic);
            }
        }
    }

    public void removeSubTaskById(int id) {
        SubTask subTask = (SubTask)this.subtasks.get(id);
        if (subTask != null) {
            removeFromPrioritized(subTask); // ДОБАВЛЕНО

            Epic epic = (Epic)this.epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                recalculateEpicTime(epic);
                this.updateEpicStatus(epic);
            }
            this.subtasks.remove(id);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks(){
        return new ArrayList<>(prioritizeTasks);
    }

    public List<SubTask> getSubTasksByEpicId(int epicId) {
        Epic epic = this.epics.get(epicId);
        return epic == null ? new ArrayList<>() :
                epic.getSubtaskIds().stream()
                        .map(this.subtasks::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }

    public List<Task> getHistory() {
        return this.historyManager.getHistory();
    }


    @Override
    public boolean isTasksOverlap(Task var1, Task var2){
        if(var1 == null || var2 == null ||
                var1.getStartTime() == null || var2.getStartTime() == null ||
                var1.getEndTime() == null || var2.getEndTime() == null){
            return false;
        }
        LocalDateTime startTime1 = var1.getStartTime();
        LocalDateTime endTime1 = var1.getEndTime();
        LocalDateTime startTime2 = var2.getStartTime();
        LocalDateTime endTime2 = var2.getEndTime();

        return !(endTime1.isBefore(startTime2) || endTime2.isBefore(startTime1));
    }

    private boolean isTaskOverlappingWithExisting(Task newTask) {
        if(newTask == null || newTask.getStartTime() == null || newTask.getEndTime() == null){
            return false;
        }

        return prioritizeTasks.stream()
                .filter(task -> task != null && task.getStartTime() != null && task.getDuration() != null)
                .filter(task -> !task.equals(newTask))
                .filter(task -> task.getId()!= newTask.getId())
                .anyMatch(existingTask -> isTasksOverlap(newTask, existingTask));
    }
    private int generateId() {
        return this.nextId++;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic != null) {
            List<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds.isEmpty()) {
                epic.setStatus(TaskStatus.NEW);
            } else {

                List<SubTask> subTaskList = subtaskIds.stream()
                        .map(this.subtasks::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());



                boolean allDONE = subTaskList.stream()
                        .allMatch(task -> task.getStatus() == TaskStatus.DONE);

                boolean allNEW = subTaskList.stream()
                        .allMatch(task -> task.getStatus() == TaskStatus.NEW);

                if (allDONE) {
                    epic.setStatus(TaskStatus.DONE);
                } else if (allNEW) {
                    epic.setStatus(TaskStatus.NEW);
                } else {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                }
            }
        }
    }

    private void addToPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizeTasks.add(task);
        }
    }

    private void removeFromPrioritized(Task task) {
        if (task != null) {
            prioritizeTasks.remove(task);
        }
    }


    private void recalculateEpicTime(Epic epic) {
        epic.clearTime();

        epic.getSubtaskIds().stream()
                .map(this.subtasks::get)
                .filter(Objects::nonNull)
                .filter(subTask -> subTask.getStartTime() != null && subTask.getDuration() != null)
                .forEach(subTask -> epic.calculateEndTime(
                        subTask.getStartTime(),
                        subTask.getDuration(),
                        subTask.getEndTime()
                ));
    }
}