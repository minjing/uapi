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

public class BufferTest {

    @Test
    public void testReadWrite() {
        Buffer<String> buffer = new Buffer<>();
        assertNull(buffer.read());

        boolean success = buffer.write("Test");
        assertTrue(success);

        String item = buffer.read();
        assertEquals("Test", item);
    }

    @Test
    public void testReadWriteOverLimit() {
        Buffer<String> buffer = new Buffer<>(1);

        boolean success = buffer.write("Test");
        assertTrue(success);

        success = buffer.write("Two");
        assertFalse(success);
    }

    @Test
    public void testReadEmpty() {
        Buffer<String> buffer = new Buffer<>();

        String item = buffer.read();
        assertNull(item);
    }

    @Test
    public void testReadWait() throws Exception {
        Buffer<String> buffer = new Buffer<>();

        Reader<String> reader = new Reader<>();
        reader._buffer = buffer;
        Writer<String> writer = new Writer<>();
        writer._buffer = buffer;
        writer._item = "Test";

        new Thread(reader).start();
        new Thread(writer).start();

        Thread.sleep(1000);
        assertEquals("Test", reader.item);
    }

    @Test
    public void testWriteWait() throws Exception {
        Buffer<String> buffer = new Buffer<>(1);

        Reader1<String> reader = new Reader1<>();
        reader._buffer = buffer;
        Writer1<String> writer = new Writer1<>();
        writer._buffer = buffer;
        writer._item1 = "Test1";
        writer._item2 = "Test2";

        new Thread(writer).start();
        new Thread(reader).start();

        Thread.sleep(1000);
        assertEquals("Test1", reader.item1);
        assertEquals("Test2", reader.item2);
    }

    private class Reader<T> implements Runnable {

        private Buffer<T> _buffer;
        private T item;

        @Override
        public void run() {
            try {
                this.item = this._buffer.read(true);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    private class Writer<T> implements Runnable {

        private Buffer<T> _buffer;
        private T _item;

        @Override
        public void run() {
            try {
                Thread.sleep(500);
                this._buffer.write(this._item);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }
    }

    private class Reader1<T> implements Runnable {

        private Buffer<T> _buffer;
        private T item1;
        private T item2;

        @Override
        public void run() {
            try {
                this.item1 = this._buffer.read(true);
                Thread.sleep(500);
                this.item2 = this._buffer.read(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Writer1<T> implements Runnable {

        private Buffer<T> _buffer;
        private T _item1;
        private T _item2;

        @Override
        public void run() {
            try {
                this._buffer.write(this._item1, true);
                this._buffer.write(this._item2, true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
