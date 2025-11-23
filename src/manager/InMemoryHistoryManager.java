package manager;

import model.Node;
import model.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    Node<Task> head;
    Node<Task> tail;
    private int size = 0;
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();



    @Override
    public void add(Task task){
        if(task == null){
            return;
        }
        int taskId = task.getId();

        if(historyMap.containsKey(taskId)){
            removeNode(historyMap.get(taskId));
        }
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList(getTasks());
    }
    @Override
    public void remove(int id){
        if(historyMap.containsKey(id)){
            removeNode(historyMap.get(id));
        }
    }



    private void linkLast(Task task){
        Node<Task> newNode = new Node<>(task);

        if(tail == null){
            head = newNode;
            tail = newNode;
        }else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;

        historyMap.put(task.getId(),newNode);
    }



    private List<Task> getTasks(){
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;

        while (current!= null){
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }



    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }

        node.prev = null;
        node.next = null;

        size--;

        historyMap.remove(node.data.getId());
    }
}
