/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.task.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import uapi.task.ITask;
import uapi.test.MockitoTest;

public class TaskOrderTest extends MockitoTest {

    @Mock private ITask _task1;
    @Mock private ITask _task2;

    @Before
    public void before() {
        super.before();
    }

    @Test
    public void test() {
        TaskOrder taskOrder = new TaskOrder();

        assertEquals(0, taskOrder.compare(this._task1, this._task1));

        when(this._task1.getPriority()).thenReturn(1);
        when(this._task2.getPriority()).thenReturn(2);
        assertEquals(-1, taskOrder.compare(this._task1, this._task2));

        when(this._task1.getPriority()).thenReturn(2);
        when(this._task2.getPriority()).thenReturn(1);
        assertEquals(1, taskOrder.compare(this._task1, this._task2));

        when(this._task1.getPriority()).thenReturn(1);
        when(this._task2.getPriority()).thenReturn(1);
        assertEquals(0, taskOrder.compare(this._task1, this._task2));
        assertEquals(0, taskOrder.compare(this._task2, this._task1));
    }
}
