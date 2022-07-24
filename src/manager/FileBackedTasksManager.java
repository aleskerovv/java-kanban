package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;
import entity.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static entity.TaskStatus.DONE;
import static entity.TaskStatus.NEW;
import static entity.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    Task task;
    File filePath;
    private static final String HEADER_FILE = "id,type,name,status,description,epic";

    public FileBackedTasksManager(File filePath) {
        super();
        this.filePath = filePath;
    }

    public static void main(String[] args) throws ManagerSaveException, FileNotFoundException {
        //Создаем FileBackedTaskManager, создаем и записываем в него Задачи
/*        final File filePath = new File("tasks.csv");
        FileBackedTasksManager manager = new FileBackedTasksManager(filePath);
        Task task = new Task("Task", "test", NEW);
        Epic epic = new Epic("Epic1", "testEpic");
        SubTask subTask = new SubTask("SubTask1", "testSubTask", NEW, 2);
        SubTask subTaskNew = new SubTask("SubTask2", "testSubTask2", DONE, 2);
        Epic epicNew = new Epic("Epic2", "testEpic2");
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subTask);
        manager.createSubtask(subTaskNew);
        manager.createEpic(epicNew);*/
        //Считываем список задач из файла, просматриваем их. В файле отобразится история просмотров
/*        manager.createEntityFromText();
        manager.findTasksById(1);
        manager.findEpicById(2);
        manager.findSubTasksById(3);
        manager.findSubTasksById(4);
        manager.findEpicById(5);
        manager.findTasksById(1);
        manager.getHistory();
        System.out.println(manager.getAllTaskList());
        System.out.println(manager.getHistory());*/ //Если просто считать файл и запустить методы с 50 и 51 строки
        //Можно будет увидеть, что все восстановилось актуально

        //Проверяем, что сохраняется уникальность просмотра истории в файле
/*        manager.findTasksById(1);
        System.out.println(manager.getHistory());*/
        //Загружаем класс FileBackedTasksManager из файла
/*        FileBackedTasksManager testManager = Managers.loadFromFile(filePath);
        testManager.createEntityFromText();
        System.out.println(testManager.getAllTaskList());
        System.out.println(testManager.getHistory());*/
    }

    //Метод сохранения файла в *.csv
    public void saveToCsv() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8, false))) {
            bw.write(HEADER_FILE);

            for (Task t : getAllTaskList()) {
                bw.newLine();
                bw.write(taskToString(t, ","));
            }

            bw.newLine();
            bw.write(String.format("%n%s", historyToString(historyManager)));
        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Process 'Save to CSV' failure");
            } catch (ManagerSaveException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    //Метод считывания файла
    public List<String> readCsvFile(File filePath) throws ManagerSaveException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            return br.lines()
                    .skip(1)
                    .collect(Collectors.toList()); //Подобный способ работы увидел в теории, показалось удобным
        } catch (IOException e) {
            throw new ManagerSaveException("Process 'Read from CSV' failure");
        }
    }

    //Создаю сущности Задач и истории просмотров из файла
    public void createEntityFromText() throws ManagerSaveException {
        boolean isTask = true;

        for (String row : readCsvFile(filePath)) {
            if (isTask) {
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
                    row.lines().skip(1);
                    isTask = false;
                }
            } else {
                for (Integer id : stringToHistory(row)) {
                    if (tasks.containsKey(id)) {
                        findTasksById(id);
                    } else if (epics.containsKey(id)) {
                        findEpicById(id);
                    } else if (subTasks.containsKey(id)) {
                        findSubTasksById(id);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    String taskToString(Task task, String delimiter) {
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

    Task stringToTask(String row) {
        String[] array = row.split(",");

        switch (array[1]) {
            case "TASK":
                task = new Task(array[2], array[4]
                        , TaskStatus.valueOf(array[3])
                        , Integer.valueOf(array[0]));
                break;
            case "EPIC":
                task = new Epic(array[2]
                        , array[4]
                        , Integer.valueOf(array[0]));
                break;
            case "SUBTASK":
                task = new SubTask(array[2], array[4], TaskStatus.valueOf(array[3])
                        , Integer.valueOf(array[5])
                        , Integer.valueOf(array[0]));
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

        return watchedTask.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "");
    }

    static List<Integer> stringToHistory(String value) {
        String[] idList = value.split(",");
        List<Integer> historyList = new ArrayList<>();

        for (String id : idList)
            historyList.add(Integer.parseInt(id));

        return historyList;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearEpicsList() {
        super.clearEpicsList();
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createSubtask(SubTask subTask) {
        super.createSubtask(subTask);
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearSubtasksList() {
        super.clearSubtasksList();
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Task findTasksById(Integer id) {
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return super.findTasksById(id);
    }

    @Override
    public Epic findEpicById(Integer id) {
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return super.findEpicById(id);
    }

    @Override
    public SubTask findSubTasksById(Integer id) {
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return super.findSubTasksById(id);
    }

    @Override
    public List<Task> getHistory() {
        try {
            saveToCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return super.getHistory();
    }

}