package manager;

import entity.*;
import exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static entity.TaskStatus.*;
import static entity.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    protected File filePath;
    private static final String HEADER_FILE = "id,type,name,status,description,duration,startTime,endTime,epic";

    public FileBackedTasksManager(File filePath) {
        super();
        this.filePath = filePath;
    }

    public FileBackedTasksManager() {
    }

    public static void main(String[] args) throws ManagerSaveException, FileNotFoundException {
        //Создаем FileBackedTaskManager, создаем и записываем в него Задачи
        final File filePath = new File("tasks.csv");
        FileBackedTasksManager manager = new FileBackedTasksManager(filePath);
        Task task1 = new Task("Первая таска", "описание", NEW, 50, LocalDateTime.parse("2022-08-01T11:10:00"));
        Task task2 = new Task("Вторая таска", "описание", DONE, 50);
        Task task3 = new Task("Третья таска", "описание", IN_PROGRESS, 60, LocalDateTime.parse("2022-08-09T12:30:00"));
        Task task4 = new Task("Четвертая", "описание", IN_PROGRESS, 60, LocalDateTime.parse("2022-08-09T13:30:00"));
        Epic epic1 = new Epic("Эпик", "описание");
        SubTask subTask1 = new SubTask("Первая сабтаска", "описание", NEW, 40, LocalDateTime.parse("2022-08-05T13:50:00"), 5);
        SubTask subTask2 = new SubTask("Вторая сабтаска", "описание", NEW, 40, LocalDateTime.parse("2022-08-06T14:30:00"), 5);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(task4);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
//        manager.deleteTaskById(3);
//        System.out.println(manager.getPrioritizedTasks());
//        //Удаляем сабтаски у Эпика и проверяем, что сбросилось duration, startTime, endTime
//        manager.deleteSubTaskById(subTask.getId());
//        manager.deleteSubTaskById(subTask2.getId());
//        System.out.println(manager.findEpicById(3));
//        System.out.println(manager.getPrioritizedTasks());
        //Считываем список задач из файла, просматриваем их. В файле отобразится история просмотров
//        manager.createEntityFromText();
/*        Task another = new Task("another", "test", TASK, DONE);
        manager.createTask(another);*/
/*        manager.findTasksById(1);
        manager.findEpicById(5);
        manager.findSubTasksById(6);
        manager.findSubTasksById(7);
        manager.findEpicById(5);
        manager.findTasksById(1);
        System.out.println(manager.getAllTaskList());
        System.out.println(manager.getHistory());
        System.out.println(manager.getPrioritizedTasks());*///Если просто считать файл и запустить методы с 47 и 48 строки
        //Можно будет увидеть, что все восстановилось актуально

        //Проверяем, что сохраняется уникальность просмотра истории в файле
/*        manager.findTasksById(1);
        System.out.println(manager.getHistory());*/
        //Загружаем класс FileBackedTasksManager из файла
/*        FileBackedTasksManager testManager = Managers.loadFromFile(filePath);
        testManager.createEntityFromText();
        System.out.println(testManager.getPrioritizedTasks());*/
//        System.out.println(testManager.getAllTaskList());
//        System.out.println(testManager.getHistory());
    }

    //Метод сохранения файла в *.csv
    protected void save() {
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
    protected List<String> loadFromFile(File filePath) {
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

        for (String row : loadFromFile(filePath)) {
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
        String startTime = String.valueOf(task.getStartTime()).equals("null") ? "no info"
                : String.valueOf(task.getStartTime());
        String endTime = String.valueOf(task.getEndTime()).equals("null") ? "no info"
                : String.valueOf(task.getEndTime());
        String duration = String.valueOf(task.getDuration()).equals("null") ? "no info"
                : String.valueOf(task.getDuration());

        String stringTask = String.join(",", String.valueOf(task.getId()), String.valueOf(task.getType())
                , task.getTitle()
                , String.valueOf(task.getStatus())
                , task.getDescription()
                , duration
                , startTime
                , endTime
        );

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
        Integer duration = array[5].equals("no info") ? null : Integer.parseInt(array[5]);
        LocalDateTime startTime = array[6].equals("no info") ? null : LocalDateTime.parse(array[6]);

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
                Integer epicId = Integer.valueOf(array[8]);
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
        save();
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
    public Task findTasksById(Integer id) {
        Task task = super.findTasksById(id);
        save();
        return task;
    }

    @Override
    public Epic findEpicById(Integer id) {
        Epic epic = super.findEpicById(id);
        save();
        return epic;
    }

    @Override
    public SubTask findSubTasksById(Integer id) {
        SubTask subTask = super.findSubTasksById(id);
        save();
        return subTask;
    }
}