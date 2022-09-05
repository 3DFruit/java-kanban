package managers.history;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final int sizeLimit;
    private final Map<Integer, Node<Task>> history;

    private Node<Task> head;
    private Node<Task> tail;

    public Node<Task> linkLast(Task task) {
        if (head == null) {
            head = new Node<>(null, task, null);
            return head;
        }
        if (tail == null) {
            tail = new Node<>(head, task, null);
            head.next = tail;
            return tail;
        }
        Node<Task> node = new Node<>(tail, task, null);
        tail.next = node;
        tail = node;
        return node;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    public void removeNode(Node<Task> node) {

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
            removeNode(history.get(id));
            history.put(task.getId(), linkLast(task));
        } else {
            if (history.size() >= sizeLimit) {
                history.remove(head.data.getId());
                removeNode(head);
            }
            history.put(task.getId(), linkLast(task));
        }
    }

    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }
}
