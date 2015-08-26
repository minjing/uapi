package uapi.task.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import uapi.log.ILogger;
import uapi.task.INotifier;
import uapi.task.ITask;
import uapi.test.TestBase;

public class TaskManagerTest extends TestBase {

    @Mock private ILogger _logger;
    @Mock private ITaskTransfer _taskTrans;
    @Mock private INotifier _notifier;
    @Mock private ITask _task;

    @Captor private ArgumentCaptor<StatefulTask> _statefulTaskCaptor;
    @Captor private ArgumentCaptor<ITask> _taskCaptor;

    private TaskManager _taskMgr;

    @Before
    public void before() {
        super.before();

        this._taskMgr = new TaskManager();
        this._taskMgr.setLogger(this._logger);
        this._taskMgr.setTaskTransfer(this._taskTrans);
    }

    @Test
    public void testAddTask() {
        when(this._task.getDescription()).thenReturn("Test Task1");
        when(this._task.getPriority()).thenReturn(0);

        this._taskMgr.addTask(this._task);

        verify(this._taskTrans, times(1)).transferTask(this._taskCaptor.capture());
        assertEquals("Test Task1", this._taskCaptor.getValue().getDescription());
        assertEquals(0, this._taskCaptor.getValue().getPriority());
    }

    @Test
    public void testAddNotifiableTask() {
        when(this._task.getDescription()).thenReturn("Test Task");
        when(this._task.getPriority()).thenReturn(-1);
        
        this._taskMgr.addTask(this._task, this._notifier);

        verify(this._taskTrans, times(1)).transferTask(this._statefulTaskCaptor.capture());
        assertEquals("Test Task", this._statefulTaskCaptor.getValue().getDescription());
        assertEquals(-1, this._statefulTaskCaptor.getValue().getPriority());
        assertNotNull(this._statefulTaskCaptor.getValue().getWatcher());
    }
}
