package manager;

import entity.Epic;
import entity.SubTask;
import entity.Task;
import entity.TaskType;

import java.time.LocalDateTime;
import java.util.*;

import static entity.TaskStatus.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskManager;
    final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id = 0;
    protected Set<Task> sortedTask = new TreeSet<>((t1, t2) -> (t1.getStartTime() == null)
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
        return sortedTask;
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
        if (!sortedTask.isEmpty()) {
            for (Task t : sortedTask) {
                if(!t.equals(task)) {
                    if (isIntersectPeriod(t, task))
                        throw new TaskValidationException(String.format("Failed validation of task %s\n " +
                                        "due to time crossing with another task %s"
                                , task.getTitle(), t.getTitle()));
                }
            }
        }
    }

    public boolean isIntersectPeriod(Task task, Task anotherTask) {
        LocalDateTime firstStart = task.getStartTime();
        LocalDateTime firstEnd = task.getEndTime();
        
        LocalDateTime secondStart = anotherTask.getStartTime();
        LocalDateTime secondEnd = anotherTask.getEndTime();
        
        if(firstStart.equals(secondStart) ||
                firstEnd.equals(secondEnd))
            return true;

        if(firstStart.isBefore(secondStart)) {
            if(firstEnd.isBefore(secondEnd))
                return true;

            if(firstEnd.isAfter(secondEnd))
                return true;
        }
        else {
            if(secondEnd.isAfter(firstStart) &&
                    secondEnd.isBefore(firstEnd))
                return true;

            if(secondEnd.isAfter(firstEnd))
                return true;
        }

        return false;
    }

    @Override
    public void createTask(Task task) {
        taskValidator(task);

        if (task.getId() == null) {
            task.setId(getNextId());
        }
        sortedTask.add(task);
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
            sortedTask.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteTaskById(Integer id) {
        historyManager.remove(id);
        sortedTask.remove(tasks.get(id));
        tasks.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        taskValidator(task);
        tasks.put(task.getId(), task);
        sortedTask.add(task);
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
            subTasks.remove(subTask);
        }
        historyManager.remove(id);
        epics.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
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
        epics.get(subTask.getEpic()).setSubtasks(subTask.getId());
        epics.get(subTask.getEpic()).setDuration(subTask.getDuration());
        setEpicStatus(subTask.getEpic());
        setEpicsStartTime(subTask.getEpic());
        setEpicsEndTime(subTask.getEpic());
        sortedTask.add(subTask);
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
            epics.get(subTask.getEpic()).setDuration(0);
            setEpicStatus(subTask.getEpic());
            setEpicsStartTime(subTask.getEpic());
            setEpicsEndTime(subTask.getEpic());
            taskManager.remove(subTask.getId());
        }

        subTasks.clear();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        historyManager.remove(id);
        epics.get(subTasks.get(id).getEpic()).deleteSubTaskById(id);
        epics.get(subTasks.get(id).getEpic()).setDuration(-subTasks.get(id).getDuration());
        setEpicStatus(subTasks.get(id).getEpic());
        setEpicsStartTime(subTasks.get(id).getEpic());
        setEpicsEndTime(subTasks.get(id).getEpic());
        sortedTask.remove(subTasks.get(id));
        subTasks.remove(id);
        taskManager.remove(id);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        taskValidator(subTask);
        deleteSubTaskById(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpic()).setSubtasks(subTask.getId());
        setEpicStatus(subTask.getEpic());
        setEpicsStartTime(subTask.getEpic());
        setEpicsEndTime(subTask.getEpic());
        updateEpic(epics.get(subTask.getEpic()));
        taskManager.put(subTask.getId(), subTask);
        sortedTask.add(subTask);
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

    //Определение статуса для эпика перенес в Manager
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

    //Calculate Epic's startTime
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

    //Calculate Epic's endTime
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    Integer getNextId() {
        return ++id;
    }
}