package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    Map<Long, Object> taskManager = new HashMap<>(); //HashMap для хранения задач всех типов

    public String getAllTaskList() {
        return String.valueOf(taskManager.values());
    }

    Map<Long, Task> tasks = new HashMap<>();

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
        taskManager.put(taskManager.size() + 1L, task);
    }

    public String getTasks() {
        return String.valueOf(tasks.values());
    }

    public Task findTasksById(Long id) {
        return tasks.get(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void deleteTaskById(Long id) {
        tasks.remove(id);
    }

    public void updateTask(Task task) {
        tasks.remove(task.getId());
        tasks.put(task.getId(), task);
    }

    Map<Long, Epic> epics = new HashMap<>();

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        taskManager.put(taskManager.size() + 1L, epic);
    }

    public String getEpics() {
        return String.valueOf(epics.values());
    }

    public Epic findEpicsById(Long id) {
        return epics.get(id);
    }

    public void clearEpicsList() {
        epics.clear();
    }

    public void deleteEpicsById(Long id) {
        epics.remove(id);
    }

    public void updateEpic(Epic epic) {
        epics.remove(epic.getId());
        epics.put(epic.getId(), epic);
    }

    Map<Long, SubTask> subTasks = new HashMap<>();

    public void createSubtask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        taskManager.put(taskManager.size() + 1L, subTask);
    }

    public String getSubtasks() {
        return String.valueOf(subTasks.values());
    }

    public SubTask findSubTasksById(Long id) {
        return subTasks.get(id);
    }

    public void clearSubtasksList() {
        subTasks.clear();
    }

    public void deleteSubTaskById(Long id) {
        subTasks.remove(id);
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.remove(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
    }

    public List<SubTask> getEpicsSubTasks(Epic epic) {
        System.out.println("Список подзадач:");
        return epic.getSubtasks();
    }
}
