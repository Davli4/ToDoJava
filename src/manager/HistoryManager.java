package manager;

import model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task var1);
    List<Task> getHistory();
    void remove(int id);
}
