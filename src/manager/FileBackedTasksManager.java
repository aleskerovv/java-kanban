package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static entity.TaskStatus.*;
import static entity.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    String filePath;
    private static String HEADER_FILE = "id,type,name,status,description,epic";

    public FileBackedTasksManager(String filePath) {
        super();
        this.filePath = filePath;
    }

    public static void main(String[] args) throws ManagerSaveException {
        FileBackedTasksManager manager = new FileBackedTasksManager("tasks.csv");
/*        Task task = new Task("Task", "test", NEW);
        Epic epic = new Epic("Epic1", "testEpic");
        SubTask subTask = new SubTask("SubTask1", "testSubTask", NEW, 2);
        SubTask subTaskNew = new SubTask("SubTask2", "testSubTask2", DONE, 2);
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subTask);
        manager.createSubtask(subTaskNew);
        manager.clearTasks();
        manager.createTask(task);
        manager.findEpicById(2);
        manager.findTasksById(1);
        manager.findSubTasksById(3);
        manager.findSubTasksById(4);
        System.out.println(manager.getHistory());*/
        System.out.println(manager.read("tasks.csv"));
    }

    @Override
    public List<Task> getAllTaskList() {
        return super.getAllTaskList();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public List<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public Task findTasksById(Integer id) {
        return super.findTasksById(id);
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public Epic findEpicById(Integer id) {
        return super.findEpicById(id);
    }

    @Override
    public void clearEpicsList() {
        super.clearEpicsList();
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void createSubtask(SubTask subTask) {
        super.createSubtask(subTask);
        save();
    }

    @Override
    public List<SubTask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public SubTask findSubTasksById(Integer id) {
        return super.findSubTasksById(id);
    }

    @Override
    public void clearSubtasksList() {
        super.clearSubtasksList();
        save();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void getEpicsSubTasks(Epic epic) {
        super.getEpicsSubTasks(epic);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            bw.write(HEADER_FILE);
            for(Task task : getAllTaskList()) {
                bw.newLine();
                bw.write(taskToString(task, ","));
            }
        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Process 'save to csv' failed");
            } catch (ManagerSaveException ex) {
                ex.getMessage();
            }
        }
    }

    public List<String> read(String filePath) throws ManagerSaveException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            return br.lines().skip(1).collect(Collectors.toList());
        } catch (IOException e) {
            throw new ManagerSaveException("ОшибОчка");
        }
    }

    private String taskToString(Task task, String delimiter) {
        String classType = String.valueOf(task.getClass());

        String stringTask = "";
        switch (classType) {
            case "class entity.Epic":
                stringTask = String.format("%d%s%s%s%s%s%s%s%s", task.getId(), delimiter
                    , EPIC, delimiter, task.getTitle(), delimiter, task.getStatus()
                    , delimiter, task.getDescription());
                break;
            case "class entity.Task":
                stringTask = String.format("%d%s%s%s%s%s%s%s%s", task.getId(), delimiter
                        , TASK, delimiter, task.getTitle(), delimiter, task.getStatus()
                        , delimiter, task.getDescription());
                break;
            case "class entity.SubTask":
                stringTask = String.format("%d%s%s%s%s%s%s%s%s%s%s", task.getId(), delimiter
                        , SUBTASK, delimiter, task.getTitle(), delimiter, task.getStatus()
                        , delimiter, task.getDescription(), delimiter, task.getEpic());
                break;
        }
        return stringTask;
    }

    private Task stringToTask(String row, String delimiter) {
        String[] array = row.split(delimiter);
        return new Task(array[2], array[3]);
    }


}
