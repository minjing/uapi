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
