package uapi.task.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import uapi.task.ITask;
import uapi.test.MockitoTest;


/**
 * Unit test for TaskTransfer
 */
public class TaskTransferTest
    extends MockitoTest {

    @Mock IReadableBuffer<ITask> _readableBuffer;
    @Mock IWritableBuffer<ITask> _writableBuffer;
    @Mock ITask _task;
    @Mock TaskEmitter _taskEmitter;
    @Mock TaskRunner _taskRunner;

    @Captor ArgumentCaptor<ITask> _taskCaptor;

    private TaskTransfer _taskTransfer;

    @Before
    public void before() {
        super.before();

        this._taskTransfer = new TaskTransfer();
    }

    @Test
    public void testTransferTask() {
        this._taskTransfer.addTaskEmitter(this._taskEmitter);
        this._taskTransfer.addTaskRunner(this._taskRunner);

        when(this._taskEmitter.getBuffer()).thenReturn(this._readableBuffer);
        when(this._readableBuffer.read()).thenReturn(this._task);
        when(this._taskRunner.getBuffer()).thenReturn(this._writableBuffer);

        this._taskTransfer.start();

        //verify(this._taskEmitter, atLeastOnce()).getBuffer();
        verify(this._readableBuffer, atLeastOnce()).read();
        verify(this._taskRunner, atLeastOnce()).getBuffer();
        verify(this._writableBuffer, atLeastOnce()).write(this._taskCaptor.capture());
        assertEquals(this._task, this._taskCaptor.getValue());

        this._taskTransfer.stop();
    }
}
