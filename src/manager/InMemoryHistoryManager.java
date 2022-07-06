package manager;

import entity.Task;

import java.util.*;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager {

    protected List<Task> historyList;
    protected Map<Integer, Node<T>> nodeMap;

    Node<T> first;
    Node<T> last;

    public InMemoryHistoryManager() {
        this.historyList = new ArrayList<>();
        this.nodeMap = new HashMap<>();
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
        nodeMap.remove(id);
    }

    public void linkLast(T task) {
        final Node<T> l = last;
        final Node<T> newNode = new Node<>(l, task, null);
        last = newNode;

        if (l == null)
            first = newNode;
        else
            l.next = newNode;

        nodeMap.put(task.getId(), newNode);
    }

//    public List<Task> getTasks() {
//        for (Map.Entry<Integer, Node<T>> entry : nodeMap.entrySet()) {
//            historyList.add(entry.getValue().task);
//        }
//        return historyList;
//    }

    public void removeNode(Node<T> node) {
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
