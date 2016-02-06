package uapi.helper;

import org.junit.Assert;
import org.junit.Test;

public class StringHelperTest {

    private final String[] msgs = new String[] {
            "Invalid String - {}",
            "Invalid argument [{}] at class {}",
            "{} is invalid",
            "{ we are one team } the member are {}, {}",
            "{ we are one team, the member are {}, {}}",
            "Invalid String - {0}",
            "Test {0} is {1}",
            "{0} is test",
            "{1} index is not start from {}"
    };
    private final Object[][] args = new Object[][] {
            new Object[] { "argument" },
            new Object[] { "test", "uapi.kernel.Class" },
            new Object[] { "test" },
            new Object[] { "a", "b" },
            new Object[] { "a", "b" },
            new Object[] { "argument" },
            new Object[] { "test", "uapi.kernel.Class" },
            new Object[] { "a" },
            new Object[] { "test", "un-index", "0" }
    };
    private final String[] expects = new String[] {
            "Invalid String - argument",
            "Invalid argument [test] at class uapi.kernel.Class",
            "test is invalid",
            "{ we are one team } the member are a, b",
            "{ we are one team, the member are a, b}",
            "Invalid String - argument",
            "Test test is uapi.kernel.Class",
            "a is test",
            "un-index index is not start from 0"
    };

    @Test
    public void testMakeString() {
        for (int i = 0; i < msgs.length; i++) {
            Assert.assertEquals(expects[i], StringHelper.makeString(msgs[i], args[i]));
        }
    }
}
