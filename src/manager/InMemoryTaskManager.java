package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> taskManager;
    public static final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        this.taskManager = new HashMap<>();
    }

    @Override
    public List<Task> getAllTaskList() {
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
            System.out.println("Список задач пуст");
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
            taskManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteTaskById(Integer id) {
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
            System.out.println("Список Эпиков пуст");
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
                taskManager.remove(subTask.getId());
            }
            taskManager.remove(epic.getId());
        }
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteEpicById(Integer id) {
        for (SubTask subTask : epics.get(id).getSubtasks()) {
            taskManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
        }
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
            System.out.println("Список Подзадач пуст");
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
        }
        for (SubTask subTask : subTasks.values()) {
            subTasks.get(subTask.getId()).getEpic().getSubtasks().clear();
        }
        subTasks.clear();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        subTasks.get(id).getEpic().getSubtasks().remove(subTasks.get(id));
        subTasks.get(id).getEpic().setStatus();
        subTasks.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        subTask.getEpic().setStatus();
        updateEpic(subTask.getEpic());
    }

    @Override
    public void getEpicsSubTasks(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            System.out.println("У эпика нет активных подзадач");
            return;
        }
        System.out.println("Список подзадач:");
        System.out.println(epic.getSubtasks());
    }

    @Override
    public Integer getNextId() {
        return taskManager.size() + 1;
    }
}