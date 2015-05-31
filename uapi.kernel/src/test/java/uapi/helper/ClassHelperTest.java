package uapi.helper;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

import org.junit.Test;

import uapi.config.Config;
import uapi.helper.ClassHelper;

public class ClassHelperTest {

    @Test
    public void testCollectionField() {
        assertEquals("setMessage", ClassHelper.makeSetterName("messages", true));
        assertEquals("setChild", ClassHelper.makeSetterName("children", true));
    }

    @Test
    public void testGetInterfaceParameterizedClasses() {
        Class<?>[] paramTypes = ClassHelper.getInterfaceParameterizedClasses(FakeService.class, IFakeInterface.class);
        assertEquals(1, paramTypes.length);
        assertEquals(Config.class, paramTypes[0]);
    }

    private interface IFakeInterface<T extends Annotation> { }

    private final class FakeService implements IFakeInterface<Config> { }
}
