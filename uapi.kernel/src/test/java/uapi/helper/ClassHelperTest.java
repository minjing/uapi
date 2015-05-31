package uapi.helper;

import static org.junit.Assert.*;

import org.junit.Test;

import uapi.helper.ClassHelper;

public class ClassHelperTest {

    @Test
    public void testCollectionField() {
        assertEquals("setMessage", ClassHelper.makeSetterName("messages", true));
        assertEquals("setChild", ClassHelper.makeSetterName("children", true));
    }

}
