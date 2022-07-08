package manager;

import entity.Task;

import java.util.*;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager {

    private List<Task> historyList;
    private final Map<Integer, Node<T>> nodeMap;

    protected int size = 0;
    private Node<T> tail;
    private Node<T> head;

    public InMemoryHistoryManager() {
        historyList = new ArrayList<>();
        nodeMap = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            historyList.remove(task);
            removeNode(nodeMap.get(task.getId()));
        }
        historyList.add(task);
        linkLast((T) task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void remove(int id) {
        historyList.remove(nodeMap.get(id).task);
    }

    private void linkLast(T task) {
        Node<T> oldTail = tail;
        Node<T> newNode = new Node<>(oldTail, task, null);

        tail = newNode;

        if (oldTail == null) {
            head = newNode;
            nodeMap.put(task.getId(), head);
        } else {
            oldTail.next = newNode;
            nodeMap.put(task.getId(), oldTail.next);
        }
        size++;
    }

    private void removeNode(Node<T> node) {
        if (size > 1) {
            node.next.prev = node.prev;
        }
        nodeMap.remove(node.task.getId());
    }

    private static class Node<T> {
        T task;
        Node<T> next;
        Node<T> prev;

        Node(Node<T> prev, T task, Node<T> next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }
}
