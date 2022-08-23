package managers.history;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int sizeLimit;
    private final HashMap<Integer, Node<Task>> history;

    private Node<Task> head;
    private Node<Task> tail;

    public Node<Task> linkLast(Task task) {
        Node<Task> node = new Node<>(tail, task, null);
        tail.next = node;
        return node;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    public Node<Task> removeNode(Node<Task> node) {

        Node<Task> prev = node.prev;
        Node<Task> next = node.next;

        if (node == head) {
            next.prev = null;
            head = next;
        } else if (node == tail) {
            prev.next = null;
            tail = prev;
        } else {
            prev.next = next;
            next.prev = prev;
        }
        node.prev = null;
        node.next = null;
        return node;
    }

    public InMemoryHistoryManager() {
        sizeLimit = 10;
        history = new HashMap<>();
        head = null;
        tail = null;
    }

    public InMemoryHistoryManager(int limit) {
        sizeLimit = limit;
        history = new HashMap<>();
        head = null;
        tail = null;
    }

    public List<Task> getHistory() {
        return getTasks();
    }

    public void add(Task task) {
        int id = task.getId();
        if (history.containsKey(id)) {
            Node<Task> node = removeNode(history.get(id));
            tail.next = node;
            node.prev = tail;
            tail = node;
        } else {
            if (history.size() >= sizeLimit) {
                history.remove(head.data.getId());
                removeNode(head);
            }
            Node<Task> node = new Node<>(tail, task, null);
            tail = node;
            history.put(task.getId(), node);
        }
    }

    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }
}
