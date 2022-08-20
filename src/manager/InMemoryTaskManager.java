package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;

import java.time.LocalDateTime;
import java.util.*;

import static entity.TaskStatus.*;
import static entity.TaskType.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskManager;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id = 0;
    protected Set<Task> sortedTasks = new TreeSet<>((o1, o2) -> {
        if (o1.getId().equals(o2.getId())) {
            return 0;
        }
        if (o1.getStartTime() == null && o2.getStartTime() == null) {
            return o1.getId() - o2.getId();
        } else if (o1.getStartTime() == null) {
            return 1;
        } else if (o2.getStartTime() == null) {
            return -1;
        } else {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    });

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
        return new ArrayList<>(taskManager.values());
    }

    protected Map<Integer, Task> tasks = new HashMap<>();

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

        if (firstStart == null) {
            return false;
        }

        if (secondStart == null) {
            return false;
        }

        return !firstEnd.isBefore(secondStart) && !firstStart.isAfter(secondEnd)
                && !firstStart.isEqual(secondEnd) && !secondStart.isEqual(firstEnd);
    }

    @Override
    public void createTask(Task task) {
        taskValidator(task);

        if (task.getId() == null) {
            task.setId(getNextId());
        }
        task.setType(TASK);
        sortedTasks.add(task);
        tasks.put(task.getId(), task);
        taskManager.put(task.getId(), task);
    }

    @Override
    public List<Task> getTasks() {
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
        final Task currentTask = tasks.get(task.getId());
        sortedTasks.remove(currentTask);
        taskValidator(task);
        tasks.put(task.getId(), task);
        sortedTasks.add(task);
    }

    protected Map<Integer, Epic> epics = new HashMap<>();

    @Override
    public void createEpic(Epic epic) {
        if (epic.getId() == null) {
            epic.setId(getNextId());
        }
        epic.setType(EPIC);
        epic.setStatus(NEW);
        epics.put(epic.getId(), epic);
        taskManager.put(epic.getId(), epic);
    }

    @Override
    public List<Epic> getEpics() {
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
        if (epics.get(id).getSubtasks() != null) {
            for (Integer subTask : epics.get(id).getSubtasks()) {
                historyManager.remove(subTask);
                taskManager.remove(subTask);
                sortedTasks.remove(subTasks.get(subTask));
                subTasks.remove(subTask);
            }
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

    protected Map<Integer, SubTask> subTasks = new HashMap<>();

    @Override
    public void createSubtask(SubTask subTask) {
        taskValidator(subTask);
        if (subTask.getId() == null) {
            subTask.setId(getNextId());
        }
        subTask.setType(SUBTASK);
        subTasks.put(subTask.getId(), subTask);
        taskManager.put(subTask.getId(), subTask);
        epics.get(subTask.getEpic()).addSubtasksId(subTask.getId());
        setEpicsFields(epics.get(subTask.getEpic()));
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
            setEpicsFields(epics.get(subTask.getEpic()));
            taskManager.remove(subTask.getId());
            sortedTasks.remove(subTask);
        }

        subTasks.clear();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        historyManager.remove(id);
        epics.get(subTasks.get(id).getEpic()).deleteSubTaskById(id);
        setEpicsFields(epics.get(subTasks.get(id).getEpic()));
        sortedTasks.remove(subTasks.get(id));
        subTasks.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        final SubTask currentSubTask = subTasks.get(subTask.getId());
        sortedTasks.remove(currentSubTask);
        taskValidator(subTask);
        deleteSubTaskById(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpic()).addSubtasksId(subTask.getId());
        setEpicsFields(epics.get(subTask.getEpic()));
        updateEpic(epics.get(subTask.getEpic()));
        taskManager.put(subTask.getId(), subTask);
        sortedTasks.add(subTask);
    }

    @Override
    public List<SubTask> getEpicsSubTasks(Integer id) {
        List<SubTask> listOfSubTasks = new ArrayList<>();
        epics.get(id).getSubtasks().stream()
                .map(subTasks::get)
                .forEach(listOfSubTasks::add);

        return listOfSubTasks;
    }

    //Common method for fields calc
    private void setEpicsFields(Epic epic) {
        List<Integer> subtasksId = epic.getSubtasks();

        Optional.ofNullable(subtasksId)
                .flatMap(id -> id.stream()
                        .map(subTasks::get)
                        .map(SubTask::getStartTime)
                        .filter(Objects::nonNull)
                        .min(LocalDateTime::compareTo))
                .ifPresentOrElse(epic::setStartTime, () -> epic.setStartTime(null));

        Optional.ofNullable(subtasksId)
                .flatMap(id -> id.stream()
                        .map(subTasks::get)
                        .map(SubTask::getEndTime)
                        .filter(Objects::nonNull)
                        .max(LocalDateTime::compareTo))
                .ifPresentOrElse(epic::setEndTime, () -> epic.setEndTime(null));

        if (subtasksId == null || subtasksId.size() == 0) {
            epic.setDuration(null);
        } else {
            int duration = subtasksId.stream()
                    .map(subTasks::get)
                    .map(SubTask::getDuration)
                    .filter(Objects::nonNull)
                    .mapToInt(num -> num)
                    .sum();
            epic.setDuration(duration != 0 ? duration : null);
        }

        setEpicStatus(epic.getId());
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    Integer getNextId() {
        return ++id;
    }
}