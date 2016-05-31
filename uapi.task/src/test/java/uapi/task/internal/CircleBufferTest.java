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

import org.junit.Test;

/**
 * The unit test for CircleBuffer
 */
public class CircleBufferTest {

    @Test
    public void testWrite() {
        CircleBuffer<String> buffer = new CircleBuffer<>(1);
        assertNull(buffer.read());

        boolean success = buffer.write("Test");
        assertTrue(success);

        String item = buffer.read();
        assertEquals("Test", item);
    }

    @Test
    public void testReadWriteOverLimit() {
        CircleBuffer<String> buffer = new CircleBuffer<>(1);

        boolean success = buffer.write("Test");
        assertTrue(success);

        success = buffer.write("Two");
        assertFalse(success);
    }

    @Test
    public void testReadEmpty() {
        CircleBuffer<String> buffer = new CircleBuffer<>(1);

        String item = buffer.read();
        assertNull(item);
    }

    @Test
    public void testWriteEmpty() throws Exception {
        CircleBuffer<String> buffer = new CircleBuffer<>(1);
        boolean success = buffer.write("Test");
        assertTrue(success);

        success = buffer.write("Test2");
        assertFalse(success);
    }
}
