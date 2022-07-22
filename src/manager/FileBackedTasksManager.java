package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;
import entity.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static entity.TaskStatus.DONE;
import static entity.TaskStatus.NEW;
import static entity.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    Task task;
    String filePath;
    private static final String HEADER_FILE = "id,type,name,status,description,epic";

    public FileBackedTasksManager(String filePath) {
        super();
        this.filePath = filePath;
    }

    public static void main(String[] args) throws ManagerSaveException {
        final String filePath = "tasks.csv";
        FileBackedTasksManager manager = new FileBackedTasksManager(filePath);
//        Task task = new Task("Task", "test", NEW);
//        Epic epic = new Epic("Epic1", "testEpic");
//        SubTask subTask = new SubTask("SubTask1", "testSubTask", NEW, 2);
//        SubTask subTaskNew = new SubTask("SubTask2", "testSubTask2", DONE, 2);
//        Epic epicNew = new Epic("Epic2", "testEpic2");
//        manager.createTask(task);
//        manager.createEpic(epic);
//        manager.createSubtask(subTask);
//        manager.createSubtask(subTaskNew);
//        manager.createEpic(epicNew);
/*        manager.clearTasks();
        manager.createTask(task);
        manager.findEpicById(2);
        manager.findTasksById(1);
        manager.findSubTasksById(3);
        manager.findSubTasksById(4);
        System.out.println(manager.getHistory());*/
        manager.getDataFromCsv(filePath);
        manager.findEpicById(1);
        manager.findEpicById(2);
        manager.findTasksById(4);
        manager.saveToCsv();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        saveToCsv();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        saveToCsv();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        saveToCsv();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        saveToCsv();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        saveToCsv();
    }

    @Override
    public void clearEpicsList() {
        super.clearEpicsList();
        saveToCsv();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        saveToCsv();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        saveToCsv();
    }

    @Override
    public void createSubtask(SubTask subTask) {
        super.createSubtask(subTask);
        saveToCsv();
    }

    @Override
    public void clearSubtasksList() {
        super.clearSubtasksList();
        saveToCsv();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        saveToCsv();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        saveToCsv();
    }

    public void saveToCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            bw.write(HEADER_FILE);
            for (Task t : getAllTaskList()) {
                bw.newLine();
                bw.write(taskToString(t, ","));
            }

            bw.newLine();
            bw.newLine();
            bw.write(historyToString(historyManager));

        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Process 'Save to CSV' failure");
            } catch (ManagerSaveException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public List<String> readCsvFile(String filePath) throws ManagerSaveException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            return br.lines().skip(1).collect(Collectors.toList());
        } catch (IOException e) {
            throw new ManagerSaveException("Process 'save to csv' failed");
        }
    }

    public void getDataFromCsv(String filePath) throws ManagerSaveException {
        for (String row : readCsvFile(filePath)) {
            if (!row.equals("")) {
                Task t = stringToTask(row);

                String classType = String.valueOf(t.getClass());
                switch (classType) {
                    case "class entity.Task":
                        createTask(t);
                        break;
                    case "class entity.Epic":
                        createEpic((Epic) t);
                        break;
                    case "class entity.SubTask":
                        createSubtask((SubTask) t);
                        break;
                    default:
                        System.out.println("Failed while read csv file");
                }
            } else {
                break;
            }
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

    private Task stringToTask(String row) {
        String[] array = row.split(",");

        switch (array[1]) {
            case "TASK":
                task = new Task(array[2], array[4], TaskStatus.valueOf(array[3]));
                break;
            case "EPIC":
                task = new Epic(array[2], array[4]);
                break;
            case "SUBTASK":
                task = new SubTask(array[2], array[4], TaskStatus.valueOf(array[3]), Integer.valueOf(array[5]));
                break;
            default:
                break;
        }

        return this.task;
    }

    static String historyToString(HistoryManager historyManager) {
        List<String> watchedTask = new ArrayList<>();

        for (Task t : historyManager.getHistory()) {
            watchedTask.add(String.format("%d", t.getId()));
        }
        return watchedTask.toString().replace("[", "").replace("]", "");
    }

    static List<Integer> stringToHistory(String value) {
        String[] idList = value.split(",");
        List<Integer> historyList = new ArrayList<>();
        for (String id : idList)
            historyList.add(Integer.parseInt(id));
        return historyList;
    }
}