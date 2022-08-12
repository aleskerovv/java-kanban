package manager;

import entity.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static entity.TaskStatus.*;
import static entity.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File filePath;
    private static final String HEADER_FILE = "id,type,name,status,description,duration,startTime,endTime,epic";

    public FileBackedTasksManager(File filePath) {
        super();
        this.filePath = filePath;
    }

    public static void main(String[] args) throws ManagerSaveException, FileNotFoundException {
        //Создаем FileBackedTaskManager, создаем и записываем в него Задачи
        final File filePath = new File("tasks.csv");
        FileBackedTasksManager manager = new FileBackedTasksManager(filePath);
        Task task1 = new Task("Первая таска", "описание", NEW, 50);
        Task task2 = new Task("Втоаря таска", "описание", DONE, 30, "2022-08-09T12:50:00");
//        Epic epic1 = new Epic("Эпик", "описание");
//        SubTask subTask1 = new SubTask("Первая сабтаска", "описание", NEW, 40, "2022-08-05T13:50:00", 3);
//        SubTask subTask2 = new SubTask("Вторая сабтаска", "описание", NEW, 40, "2022-08-06T14:30:00", 3);
        manager.createTask(task1);
        manager.createTask(task2);
//        manager.createEpic(epic1);
//        manager.createSubtask(subTask1);
//        manager.createSubtask(subTask2);
//        manager.deleteTaskById(1);
        System.out.println(manager.getPrioritizedTasks());
//        //Удаляем сабтаски у Эпика и проверяем, что сбросилось duration, startTime, endTime
//        manager.deleteSubTaskById(subTask.getId());
//        manager.deleteSubTaskById(subTask2.getId());
//        System.out.println(manager.findEpicById(3));
//        System.out.println(manager.getPrioritizedTasks());
        //Считываем список задач из файла, просматриваем их. В файле отобразится история просмотров
/*        manager.createEntityFromText();
        Task another = new Task("another", "test", TASK, DONE);
        manager.createTask(another);
        manager.findTasksById(1);
        manager.findEpicById(2);
        manager.findSubTasksById(3);
        manager.findSubTasksById(4);
        manager.findEpicById(5);
        manager.findTasksById(1);
        System.out.println(manager.getAllTaskList());
        System.out.println(manager.getHistory());*/ //Если просто считать файл и запустить методы с 47 и 48 строки
                                                    //Можно будет увидеть, что все восстановилось актуально

        //Проверяем, что сохраняется уникальность просмотра истории в файле
/*        manager.findTasksById(1);
        System.out.println(manager.getHistory());*/
        //Загружаем класс FileBackedTasksManager из файла
/*        FileBackedTasksManager testManager = Managers.loadFromFile(filePath);
        testManager.createEntityFromText();
        System.out.println(testManager.getPrioritizedTasks());
        System.out.println(testManager.getAllTaskList());
        System.out.println(testManager.getHistory());*/
    }

    //Метод сохранения файла в *.csv
    private void saveToCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8, false))) {
            bw.write(HEADER_FILE);

            for (Task t : getAllTaskList()) {
                bw.newLine();
                bw.write(taskToString(t));
            }

            bw.newLine();
            bw.write(String.format("%n%s", historyToString(historyManager)));
        } catch (IOException e) {
            throw new ManagerSaveException("Process 'Save to CSV' failure");
        }
    }

    //Метод считывания файла
    private List<String> readCsvFile(File filePath) {
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
                    parseTask(row);
                } else {
                    isTask = false;
                }
            } else {
                parseHistory(row);
            }
        }

        setMaxId();
    }

    private void setMaxId() {
        int maxId = 0;
        for (Task task : getAllTaskList()) {
            if (maxId < task.getId())
                maxId = task.getId();
        }
        setId(maxId);
    }

    private void parseTask(String row) {
        Task t = stringToTask(row);
        TaskType type = t.getType();

        switch (type) {
            case TASK:
                createTask(t);
                break;
            case EPIC:
                createEpic((Epic) t);
                break;
            case SUBTASK:
                createSubtask((SubTask) t);
                break;
        }
    }

    private void parseHistory(String row) {
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

    private String taskToString(Task task) {
        String stringTask = String.join(",", String.valueOf(task.getId()), String.valueOf(task.getType())
                , task.getTitle()
                , String.valueOf(task.getStatus())
                , task.getDescription()
                , String.valueOf(task.getDuration())
                , String.valueOf(task.getStartTime())
                , String.valueOf(task.getEndTime()));


        if (SUBTASK.equals(task.getType())) {
            stringTask = String.join(",", stringTask, String.valueOf(task.getEpic()));
        }
        return stringTask;
    }

    private Task stringToTask(String row) {
        Task task = new Task();
        String[] array = row.split(",");

        String title = array[2];
        String description = array[4];
        TaskStatus status = TaskStatus.valueOf(array[3]);
        Integer id = Integer.valueOf(array[0]);
        TaskType taskType = TaskType.valueOf(array[1]);
        Long duration = Long.valueOf(array[5]);
        String startTime = array[6];

        switch (taskType) {
            case TASK:
                task = new Task(title, description
                        , status
                        , duration
                        , startTime
                        , id);
                break;
            case EPIC:
                task = new Epic(title
                        , description
                        , id);
                break;
            case SUBTASK:
                Integer epicId  = Integer.valueOf(array[8]);
                task = new SubTask(title, description
                        , status
                        , duration
                        , startTime
                        , epicId
                        , id);
                break;
            default:
                break;
        }

        return task;
    }

    static String historyToString(HistoryManager historyManager) {
        List<String> watchedTask = new ArrayList<>();

        for (Task t : historyManager.getHistory()) {
            watchedTask.add(String.format("%d", t.getId()));
        }

        return String.join(",", watchedTask);
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

    @Override
    public Task findTasksById(Integer id) {
        Task task = super.findTasksById(id);
        saveToCsv();
        return task;
    }

    @Override
    public Epic findEpicById(Integer id) {
        Epic epic = super.findEpicById(id);
        saveToCsv();
        return epic;
    }

    @Override
    public SubTask findSubTasksById(Integer id) {
        SubTask subTask = super.findSubTasksById(id);
        saveToCsv();
        return subTask;
    }
}