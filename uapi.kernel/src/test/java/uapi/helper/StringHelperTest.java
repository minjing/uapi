package uapi.helper;

import org.junit.Assert;
import org.junit.Test;

import uapi.helper.StringHelper;

public class StringHelperTest {

    private final String[] msgs = new String[] {
        "Invalid String - {}",
        "Invalid arguemnt [{}] at class {}",
        "{} is invalid",
        "{ we are one team } the member are {}, {}",
        "{ we are one team, the member are {}, {}}"
    };
    private final Object[][] args = new Object[][] {
        new Object[] { "argument" },
        new Object[] { "test", "uapi.kernel.Class" },
        new Object[] { "test" },
        new Object[] { "a", "b" },
        new Object[] { "a", "b" }
    };
    private final String[] expects = new String[] {
        "Invalid String - argument",
        "Invalid arguemnt [test] at class uapi.kernel.Class",
        "test is invalid",
        "{ we are one team } the member are a, b",
        "{ we are one team, the member are a, b}"
    };

    @Test
    public void testMakeString() {
        for (int i = 0; i < msgs.length; i++) {
            Assert.assertEquals(expects[i], StringHelper.makeString(msgs[i], args[i]));
        }
    }
}
