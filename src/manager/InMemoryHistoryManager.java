package manager;

import entity.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> historyList;
    private final Map<Integer, Node> nodeMap;

    protected int size = 0;
    private Node tail;
    private Node head;

    public InMemoryHistoryManager() {
        nodeMap = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            historyList.clear();
            removeNode(nodeMap.get(task.getId()));
        }
        linkLast(task);
    }

    private void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);

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

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        historyList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            historyList.add(node.task);
            node = node.next;
        }

        return historyList;
    }

    @Override
    public void remove(int id) {
        removeNode(nodeMap.get(id));
        nodeMap.remove(id);
    }

    private void removeNode(Node node) {
        //Для всех, кроме первого и последнего элементов
        if (node.prev != null && node.next != null) {
            Node nodePrev = node.prev;
            Node nodeNext = node.next;
            nodeNext.prev = node.prev;
            nodePrev.next = node.next;
        } else {
            // Для первого элемента
            if (node.prev == null) {
                Node nodeNext = node.next;
                if (nodeNext != null) {
                    nodeNext.prev = null;
                    //Если лист из 1 объекта - зачищаем и хвост
                } else {
                    tail = null;
                }
                head = nodeNext;
                // Для последнего элемента
            } else {
                Node nodePrev = node.prev;
                nodePrev.next = null;
                tail = nodePrev;
            }
        }
        size--;
    }

    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

    }
}
