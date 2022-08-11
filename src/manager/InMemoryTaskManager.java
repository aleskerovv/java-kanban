package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;

import java.time.LocalDateTime;
import java.util.*;

import static entity.TaskStatus.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskManager;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id = 0;
    protected Set<Task> sortedTasks = new TreeSet<>((t1, t2) -> (t1.getStartTime() == null)
            ? 1
            : (t2.getStartTime() == null)
            ? -1
            : ((t1.getStartTime().compareTo(t2.getStartTime())) == 0)
            ? (t1.getId() - t2.getId())
            : (t1.getStartTime().compareTo(t2.getStartTime()))
    );

    public InMemoryTaskManager() {
        this.taskManager = new HashMap<>();
    }

    void setId(int id) {
        this.id = id;
    }

    public Set<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    @Override
    public List<Task> getAllTaskList() {
        if (taskManager.isEmpty()) {
            System.out.println("Task list is empty");
        }
        return new ArrayList<>(taskManager.values());
    }

    final Map<Integer, Task> tasks = new HashMap<>();

    public void taskValidator(Task task) {
        for (Task t : sortedTasks) {
                if (isIntersectPeriod(t, task))
                    throw new TaskValidationException(String.format("Failed validation of task %s\n " +
                                    "due to time crossing with another task %s"
                            , task.getTitle(), t.getTitle()));
        }
    }

    public boolean isIntersectPeriod(Task task, Task anotherTask) {
        LocalDateTime firstStart = task.getStartTime();
        LocalDateTime firstEnd = task.getEndTime();

        LocalDateTime secondStart = anotherTask.getStartTime();
        LocalDateTime secondEnd = anotherTask.getEndTime();

        return !firstEnd.isBefore(secondStart) && !firstStart.isAfter(secondEnd);
    }

    @Override
    public void createTask(Task task) {
        taskValidator(task);

        if (task.getId() == null) {
            task.setId(getNextId());
        }
        sortedTasks.add(task);
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
            sortedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteTaskById(Integer id) {
        historyManager.remove(id);
        sortedTasks.remove(tasks.get(id));
        tasks.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        sortedTasks.remove(task);
        taskValidator(task);
        tasks.put(task.getId(), task);
        sortedTasks.add(task);
    }

    final Map<Integer, Epic> epics = new HashMap<>();

    @Override
    public void createEpic(Epic epic) {
        if (epic.getId() == null) {
            epic.setId(getNextId());
        }
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
            sortedTasks.remove(subTask);
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
            sortedTasks.remove(subTasks.get(subTask));
            subTasks.remove(subTask);
        }
        historyManager.remove(id);
        epics.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic currentEpic = epics.get(epic.getId());
        currentEpic.setTitle(epic.getTitle());
        currentEpic.setDescription(epic.getDescription());
    }

    final Map<Integer, SubTask> subTasks = new HashMap<>();

    @Override
    public void createSubtask(SubTask subTask) {
        taskValidator(subTask);
        if (subTask.getId() == null) {
            subTask.setId(getNextId());
        }
        subTasks.put(subTask.getId(), subTask);
        taskManager.put(subTask.getId(), subTask);
        epics.get(subTask.getEpic()).addSubtasksId(subTask.getId());
        setEpicsFields(subTask.getEpic());
        sortedTasks.add(subTask);
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
            setEpicsFields(subTask.getEpic());
            taskManager.remove(subTask.getId());
            sortedTasks.remove(subTask);
        }

        subTasks.clear();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        historyManager.remove(id);
        epics.get(subTasks.get(id).getEpic()).deleteSubTaskById(id);
        setEpicsFields(subTasks.get(id).getEpic());
        sortedTasks.remove(subTasks.get(id));
        subTasks.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        sortedTasks.remove(subTask);
        taskValidator(subTask);
        deleteSubTaskById(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpic()).addSubtasksId(subTask.getId());
        setEpicsFields(subTask.getEpic());
        updateEpic(epics.get(subTask.getEpic()));
        taskManager.put(subTask.getId(), subTask);
        sortedTasks.add(subTask);
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

    //Common method for fields calc
    private void setEpicsFields(Integer id) {
        setEpicStatus(id);
        setEpicsDuration(id);
        setEpicsStartTime(id);
        setEpicsEndTime(id);
    }

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

    private void setEpicsStartTime(Integer id) {
        LocalDateTime startTime = LocalDateTime.MAX;
        if (epics.get(id).getSubtasks().isEmpty()) {
            epics.get(id).setStartTime(null);
            return;
        }

        for (Integer st : epics.get(id).getSubtasks()) {
            if (subTasks.get(st).getStartTime().isBefore(startTime)) {
                startTime = subTasks.get(st).getStartTime();
            }
        }
        String dateTime = String.valueOf(startTime);
        epics.get(id).setStartTime(dateTime);
    }

    private void setEpicsEndTime(Integer id) {
        LocalDateTime endTime = LocalDateTime.MIN;
        if (epics.get(id).getSubtasks().isEmpty()) {
            epics.get(id).setEndTime(null);
            return;
        }

        for (Integer st : epics.get(id).getSubtasks()) {
            if (subTasks.get(st).getEndTime().isAfter(endTime)) {
                endTime = subTasks.get(st).getEndTime();
            }
        }
        epics.get(id).setEndTime(endTime);
    }

    private void setEpicsDuration(Integer id) {
        Long duration = 0l;

        if (epics.get(id).getSubtasks().isEmpty()) {
            epics.get(id).setDuration(0);
            return;
        }

        for (Integer st : epics.get(id).getSubtasks()) {
            duration += subTasks.get(st).getDuration();
        }

        epics.get(id).setDuration(duration);

    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    Integer getNextId() {
        return ++id;
    }
}