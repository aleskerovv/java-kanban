package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    Map<Integer, Object> taskManager = new HashMap<>();

    Map<Integer, Task> tasks = new HashMap<>();

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
        taskManager.put(taskManager.size() + 1, task);
    }

    public String getAllTaskList() {
        return String.valueOf(taskManager.values());
    }

    public String getTasks() {
        return String.valueOf(tasks.values());
    }

    public Object findTasksById(int id) {
        return tasks.get(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void updateTask(Task task) {
        tasks.remove(task.getId());
        tasks.put(task.getId(), task);
    }

    Map<Integer, Epic> epics = new HashMap<>();
    Map<Integer, SubTask> subTasks = new HashMap<>();

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        taskManager.put(taskManager.size() + 1, epic);
    }

    public void createSubtask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        taskManager.put(taskManager.size() + 1, subTask);
    }

    public String getEpics() {
        return String.valueOf(epics.values());
    }

    public String getSubtasks() {
        return String.valueOf(subTasks.values());
    }

    public Object findEpicsById(int id) {
        return epics.get(id);
    }

    public Object findSubTasksById(int id) {
        return subTasks.get(id);
    }

    public void clearEpicsList() {
        epics.clear();
    }

    public void clearSubtasksList() {
        subTasks.clear();
    }

    public void deleteEpicsById(int id) {
        epics.remove(id);
    }

    public void deleteSubTaskById(int id) {
        subTasks.remove(id);
    }

    public void updateEpic(Epic epic) {
        epics.remove(epic.getId());
        epics.put(epic.getId(), epic);
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
