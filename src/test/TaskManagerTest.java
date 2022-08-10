import entity.*;
import manager.TaskManager;
import manager.TaskValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static entity.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    Task task1;
    Task task2;
    Epic epic1;
    Epic epic2;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;

    @BeforeEach
    void create() {
        task1 = new Task("Первая таска", "описание", NEW, 50, "2022-08-11T15:00:00");
        task2 = new Task("Втоаря таска", "описание", DONE, 30, "2022-08-09T12:50:00");
        epic1 = new Epic("Эпик", "описание");
        subTask1 = new SubTask("Первая сабтаска", "описание", NEW, 40, "2022-08-10T13:50:00", 3);
        subTask2 = new SubTask("Вторая сабтаска", "описание", NEW, 40, "2022-08-10T14:30:00", 3);
        epic2 = new Epic("Второй эпик", "описание");
        subTask3 = new SubTask("Третья сабтаска", "описание", NEW, 60, "2022-08-10T15:30:00", 6);
    }

    @AfterEach
    void clear() {
        manager.clearTasks();
        manager.clearEpicsList();
        manager.clearSubtasksList();
    }

    @Test
    void shouldReturnStatusNEW() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        //Если пустой Epic
        assertEquals(NEW, manager.findEpicById(3).getStatus());
        //Если subTask в статусе NEW
        assertEquals(NEW, manager.findEpicById(6).getStatus());
    }

    @Test
    void shouldReturnStatusDONE() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        subTask1.setStatus(DONE);
        subTask2.setStatus(DONE);
        subTask3.setStatus(DONE);
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);
        //Если subTask в статусе DONE
        assertEquals(TaskStatus.DONE, manager.findEpicById(3).getStatus());
        //Если все subTask в статусе DONE
        assertEquals(TaskStatus.DONE, manager.findEpicById(6).getStatus());
    }

    @Test
    void shouldReturnStatusINPROGRESS() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        subTask1.setStatus(NEW);
        subTask2.setStatus(DONE);
        subTask3.setStatus(IN_PROGRESS);
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        //Если subTask в статусе IN_PROGRESS
        assertEquals(TaskStatus.IN_PROGRESS, manager.findEpicById(3).getStatus());
        //Если subTask в статусе DONE и NEW
        assertEquals(TaskStatus.IN_PROGRESS, manager.findEpicById(6).getStatus());
    }

    @Test
    void createTask() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        final Task returnedTask = manager.findTasksById(1);

        assertNotNull(returnedTask, "Task not found");
        assertEquals("Первая таска", returnedTask.getTitle(), "Tasks does not match");

        final List<Task> tasks = manager.getTasks();
        assertNotNull(tasks, "Empty task list");
        assertEquals(2, tasks.size(), "Incorrect tasks count");
        assertEquals(task2, tasks.get(1), "Tasks does not match");
    }

    @Test
    void createEpic() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        final Epic returnedEpic = manager.findEpicById(3);

        assertNotNull(returnedEpic, "Epic not found");
        assertEquals("Эпик", returnedEpic.getTitle(), "Epic does not match");

        final Epic anotherReturnedEpic = manager.findEpicById(6);

        assertEquals("Второй эпик", anotherReturnedEpic.getTitle(), "Epic does not match");
        assertNotNull(anotherReturnedEpic, "Epic2 not found");

        final List<Epic> epics = manager.getEpics();
        assertNotNull(epics, "Empty task list");
        assertEquals(2, epics.size(), "Incorrect tasks count");
    }

    @Test
    void createSubTask() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        final SubTask returnedSubTask = manager.findSubTasksById(4);
        final SubTask secondReturnedSubTask = manager.findSubTasksById(5);
        final SubTask thirdReturnedSubTask = manager.findSubTasksById(7);

        assertNotNull(returnedSubTask, "SubTask not found");
        assertEquals("Первая сабтаска", returnedSubTask.getTitle(), "SubTasks does not match");

        final List<SubTask> subTasks = manager.getSubtasks();
        assertNotNull(subTasks, "Empty task list");
        assertEquals(3, subTasks.size(), "Incorrect tasks count");
    }

    @Test
    void getAllTaskList() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        final List<Task> tasks = manager.getAllTaskList();

        assertNotNull(tasks, "Tasks list is empty");
        assertEquals(7, tasks.size());
    }

    @Test
    void findTaskById() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        final Task returnedTask = manager.findTasksById(2);

        assertNotNull(returnedTask, "Task not found");
        assertEquals(2, returnedTask.getId(), "Tasks does not match");
    }

    @Test
    void findEpicById() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        final Epic returnedEpic = manager.findEpicById(6);

        assertNotNull(returnedEpic, "Epic not found");
        assertEquals(6, returnedEpic.getId(), "Epics does not match");
        assertEquals(epic2, returnedEpic, "Epics does not match");
    }

    @Test
    void findSubTaskById() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        final SubTask returnedSubTask = manager.findSubTasksById(4);

        assertNotNull(returnedSubTask, "subTask not found");
        assertEquals(subTask1, returnedSubTask, "subTask does not match");
    }

    @Test
    void updateTask_shouldReturnStatusDone() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);
        Task returnedTask = manager.findTasksById(1);

        assertEquals(TaskStatus.DONE, returnedTask.getStatus(), "Status incorrect");
    }

    @Test
    void updateEpic_shouldReturnNewDescription() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        epic1.setDescription("changed Desc");
        manager.updateEpic(epic1);
        Task returnedEpic = manager.findEpicById(3);

        assertEquals("changed Desc", returnedEpic.getDescription(), "Description incorrect");
    }

    @Test
    void updateSubTask_shouldReturnNewDescription() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);
        subTask2.setDescription("new desc");
        manager.updateSubTask(subTask2);

        final SubTask returnedSubTask = manager.findSubTasksById(5);

        assertEquals("new desc", returnedSubTask.getDescription(), "Description incorrect");
    }

    @Test
    void deleteTaskById_shouldBe0() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);

        manager.deleteTaskById(1);
        manager.deleteTaskById(2);
        final List<Task> returnedList = manager.getTasks();

        assertEquals(0, returnedList.size(), "List is not empty");
    }

    @Test
    void deleteEpicById_shouldBe0() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);

        manager.deleteEpicById(3);
        manager.deleteEpicById(6);
        final List<Epic> returnedList = manager.getEpics();

        assertEquals(0, returnedList.size(), "List is not empty");
    }

    @Test
    void deleteSubTaskById_shouldReturnEmptyList() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);

        manager.deleteSubTaskById(4);
        manager.deleteSubTaskById(5);
        manager.deleteSubTaskById(7);

        final List<SubTask> subTasks = manager.getSubtasks();
        final List<Integer> epicsSubTaskList = manager.findEpicById(3).getSubtasks();

        assertEquals(0, subTasks.size(), "List is not empty");
        assertEquals(0, epicsSubTaskList.size(), "List is not empty");
    }

    @Test
    void clearTaskList_shouldBe0() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);

        manager.clearTasks();
        final List<Task> returnedList = manager.getTasks();

        assertEquals(0, returnedList.size(), "List is not empty");
    }

    @Test
    void clearEpicList_shouldBe0() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);

        manager.clearEpicsList();
        final List<Epic> returnedList = manager.getEpics();

        assertEquals(0, returnedList.size(), "List is not empty");
    }

    @Test
    void clearSubTaskList_shouldBe0() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);

        manager.clearSubtasksList();

        final List<SubTask> returnedSubTaskList = manager.getSubtasks();
        assertEquals(0, returnedSubTaskList.size(), "List is not empty");
    }

    @Test
    void shouldReturnDuration80() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);

        Epic returnedEpic = manager.findEpicById(3);

        assertEquals(80, returnedEpic.getDuration(), "Duration not match");
    }

    @Test
    void shouldReturnEndTimeNotNull() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createEpic(epic2);
        manager.createSubtask(subTask3);

        Epic returnedEpic = manager.findEpicById(3);
        Task returnedTask = manager.findTasksById(1);


        assertNotNull(returnedEpic.getEndTime(), "endTime is null");
        assertEquals(LocalDateTime.parse("2022-08-10T15:10:00"), returnedEpic.getEndTime(), "EndTime not match");

        assertNotNull(returnedTask.getEndTime(), "endTime is null");
        assertEquals(LocalDateTime.parse("2022-08-11T15:50:00"), returnedTask.getEndTime());
    }

    @Test
    void shouldReturnNull() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);

        Epic returnedEpic = manager.findEpicById(3);

        assertNull(returnedEpic.getEndTime(), "EndTime not null");
    }

    @Test
    void shouldThrowTaskValidationException() {
        manager.createTask(task1);
        manager.createTask(task2);
        task1.setStartTime("2022-08-09T12:50:00");

        TaskValidationException exception = assertThrows(TaskValidationException.class, () -> manager.updateTask(task1));

        assertEquals("Failed validation of task Первая таска" +
                "\n due to time crossing with another task Втоаря таска: startTime=2022-08-09T12:50", exception.getMessage());
    }
}