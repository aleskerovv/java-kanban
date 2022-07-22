package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static entity.TaskStatus.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskManager;
    final HistoryManager historyManager = Managers.getDefaultHistory();
    int id = 0;

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
        historyManager.remove(id);
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
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
            taskManager.remove(subTask.getId());
        }
        subTasks.clear();

        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            taskManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void deleteEpicById(Integer id) {
        for (Integer subTask : epics.get(id).getSubtasks()) {
            historyManager.remove(subTask);
            taskManager.remove(subTask);
            subTasks.remove(subTask);
        }
        historyManager.remove(id);
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
        epics.get(subTask.getEpic()).setSubtasks(subTask.getId());
        setEpicStatus(subTask.getEpic());
    }

    @Override
    public List<SubTask> getSubtasks() {
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
            historyManager.remove(subTask.getId());
            epics.get(subTask.getEpic()).clearSubtasks();
            setEpicStatus(subTask.getEpic());
            taskManager.remove(subTask.getId());
        }

        subTasks.clear();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        historyManager.remove(id);
        epics.get(subTasks.get(id).getEpic()).deleteSubTaskById(id);
        setEpicStatus(subTasks.get(id).getEpic());
        subTasks.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        deleteSubTaskById(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpic()).setSubtasks(subTask.getId());
        setEpicStatus(subTask.getEpic());
        updateEpic(epics.get(subTask.getEpic()));
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

    //Определение статуса для эпика перенес в Manager
    private void setEpicStatus(Integer id) {
        if (epics.get(id).getSubtasks().isEmpty()) {
            epics.get(id).setStatus(NEW);
            return;
        }

        boolean isDone = false;
        boolean isNew = false;
        for (Integer subTask : epics.get(id).getSubtasks()) {
            switch (subTasks.get(subTask).getStatus()) {
                case NEW:
                    if (isDone) {
                        epics.get(id).setStatus(IN_PROGRESS);
                        return;
                    }
                    isNew = true;
                    break;
                case DONE:
                    if (isNew) {
                        epics.get(id).setStatus(IN_PROGRESS);
                        return;
                    }
                    isDone = true;
                    break;
                case IN_PROGRESS:
                    epics.get(id).setStatus(IN_PROGRESS);
                    return;
            }
            if (isDone) {
                epics.get(id).setStatus(DONE);
            } else if (isNew) {
                epics.get(id).setStatus(NEW);
            } else {
                epics.get(id).setStatus(IN_PROGRESS);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Integer getNextId() {
        return ++id;
    }
}