package managers.history;

public class Node<T> {
    public Node<T> next;
    public Node<T> prev;
    public T data;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.next = next;
        this.prev = prev;
        this.data = data;
    }
}
