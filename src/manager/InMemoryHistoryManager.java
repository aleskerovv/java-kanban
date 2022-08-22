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
        if (task == null) return;
        if (nodeMap.containsKey(task.getId())) {
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
        } else {
            oldTail.next = newNode;
        }

        nodeMap.put(task.getId(), newNode);
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
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
            nodeMap.remove(id);
        }
    }

    private void removeNode(Node node) {
        //Для всех, кроме первого и последнего элементов
        Node nodeNext = node.next;
        Node nodePrev = node.prev;
        if (nodePrev != null && nodeNext != null) { //Для всех, кроме первого и последнего элементов
            nodeNext.prev = nodePrev;
            nodePrev.next = nodeNext;
        } else if (nodePrev == null) {
            if (nodeNext != null) {
                nodeNext.prev = null;
            } else {
                tail = null; //Если лист из 1 объекта - зачищаем и хвост
            }
            head = nodeNext;
        } else { // Для последнего элемента
            nodePrev.next = null;
            tail = nodePrev;
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
