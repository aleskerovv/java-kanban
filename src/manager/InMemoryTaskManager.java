package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskManager;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        this.taskManager = new HashMap<>();
    }

    @Override
    public List<Task> getAllTaskList() {
        if (taskManager.isEmpty()) {
            System.out.println("Task list is empty");
        }
        return new ArrayList<>(taskManager.values());
    }

    private final Map<Integer, Task> tasks = new HashMap<>();

    @Override
    public void createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        taskManager.put(task.getId(), task);
    }

    @Override
    public List<Task> getTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task findTasksById(Integer id) {
        historyManager.addTask(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            taskManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteTaskById(Integer id) {
        historyManager.remove(tasks.get(id).getId());
        tasks.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private final Map<Integer, Epic> epics = new HashMap<>();

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        taskManager.put(epic.getId(), epic);
    }

    @Override
    public List<Epic> getEpics() {
        if (epics.isEmpty()) {
            System.out.println("Epic list is empty");
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic findEpicById(Integer id) {
        historyManager.addTask(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void clearEpicsList() {
        for (Epic epic : epics.values()) {
            for (SubTask subTask : subTasks.values()) {
                historyManager.remove(subTasks.get(subTask.getId()).getId());
                taskManager.remove(subTask.getId());
            }
            historyManager.remove(epics.get(epic.getId()).getId());
            taskManager.remove(epic.getId());
        }
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteEpicById(Integer id) {
        for (SubTask subTask : epics.get(id).getSubtasks()) {
            historyManager.remove(subTasks.get(subTask.getId()).getId());
            taskManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
        }
        historyManager.remove(epics.get(id).getId());
        epics.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    @Override
    public void createSubtask(SubTask subTask) {
        subTask.setId(getNextId());
        subTasks.put(subTask.getId(), subTask);
        taskManager.put(subTask.getId(), subTask);
        subTask.getEpic().setSubtasks(subTask);
        subTask.getEpic().setStatus();
    }

    @Override
    public List<SubTask> getSubtasks() {
        if (subTasks.isEmpty()) {
            System.out.println("Subtasks list is empty");
        }
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public SubTask findSubTasksById(Integer id) {
        historyManager.addTask(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void clearSubtasksList() {
        for (SubTask subTask : subTasks.values()) {
            taskManager.remove(subTask.getId());
            subTasks.get(subTask.getId()).getEpic().clearSubtasks();
        }

        subTasks.clear();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        subTasks.get(id).getEpic().deleteSubTaskById(subTasks.get(id));
        subTasks.get(id).getEpic().setStatus();
        subTasks.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        deleteSubTaskById(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        subTask.getEpic().setSubtasks(subTask);
        subTask.getEpic().setStatus();
        updateEpic(subTask.getEpic());
        taskManager.put(subTask.getId(), subTask);
    }

    @Override
    public void getEpicsSubTasks(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            System.out.println("No active subtasks for this epic");
            return;
        }
        System.out.println("Subtasks list:");
        System.out.println(epic.getSubtasks());
    }

    @Override
    public List<Task> getHistory() {
       return historyManager.getHistory();
    }

    private Integer getNextId() {
        return taskManager.size() + 1;
    }
}