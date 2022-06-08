package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    protected Map<Integer, Object> taskManager = new HashMap<>(); //HashMap для хранения задач всех типов

    public Object getAllTaskList() {
        if (taskManager.isEmpty()) {
            return "Список taskManager пуст";
        }
        return taskManager.values();
    }

    private Map<Integer, Task> tasks = new HashMap<>();

    public void createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        taskManager.put(task.getId(), task);
    }

    public List<Task> getTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
        }
        return (List<Task>) tasks.values();
    }

    public Task findTasksById(Integer id) {
        return tasks.get(id);
    }

    public void clearTasks() {
        for (Task task : tasks.values()) {
            taskManager.remove(task.getId());
        }
        tasks.clear();
    }

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
        taskManager.remove(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private Map<Integer, Epic> epics = new HashMap<>();

    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        taskManager.put(epic.getId(), epic);
    }

    public Object getEpics() {
        if (epics.isEmpty()) {
            return "Список Эпиков пуст";
        }
        return epics.values();
    }

    public Epic findEpicById(Integer id) {
        return epics.get(id);
    }

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

    public void deleteEpicById(Integer id) {
        for (SubTask subTask : epics.get(id).getSubtasks()) {
            taskManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
        taskManager.remove(id);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    public void createSubtask(SubTask subTask) {
        subTask.setId(getNextId());
        subTasks.put(subTask.getId(), subTask);
        taskManager.put(subTask.getId(), subTask);
        subTask.getEpic().setSubtasks(subTask);
        subTask.getEpic().setStatus();
    }

    public Object getSubtasks() {
        if (subTasks.isEmpty()) {
            return "Список Подзадач пуст";
        }
        return subTasks.values();
    }

    public SubTask findSubTasksById(Integer id) {
        return subTasks.get(id);
    }

    public void clearSubtasksList() {
        for (SubTask subTask : subTasks.values()) {
            taskManager.remove(subTask.getId());
        }
        for (SubTask subTask : subTasks.values()) {
            subTasks.get(subTask.getId()).getEpic().getSubtasks().clear();
        }
        subTasks.clear();
    }

    public void deleteSubTaskById(Integer id) {
        subTasks.get(id).getEpic().getSubtasks().remove(subTasks.get(id));
        subTasks.get(id).getEpic().setStatus();
        subTasks.remove(id);
        taskManager.remove(id);
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        subTask.getEpic().setStatus();
        updateEpic(subTask.getEpic());
    }

    public void getEpicsSubTasks(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            System.out.println("У эпика нет активных подзадач");
            return;
        }
        System.out.println("Список подзадач:");
        System.out.println(epic.getSubtasks());
    }

    private Integer getNextId() {
        return taskManager.size() + 1;
    }
}
