package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTaskList();

    void createTask(Task task);

    List<Task> getTasks();

    Task findTasksById(Integer id);

    void clearTasks();

    void deleteTaskById(Integer id);

    void updateTask(Task task);

    void createEpic(Epic epic);

    List<Epic> getEpics();

    Epic findEpicById(Integer id);

    void clearEpicsList();

    void deleteEpicById(Integer id);

    void updateEpic(Epic epic);

    void createSubtask(SubTask subTask);

    List<SubTask> getSubtasks();

    SubTask findSubTasksById(Integer id);

    void clearSubtasksList();

    void deleteSubTaskById(Integer id);

    void updateSubTask(SubTask subTask);

    void getEpicsSubTasks(Epic epic);

    List<Task> getHistory();
}
