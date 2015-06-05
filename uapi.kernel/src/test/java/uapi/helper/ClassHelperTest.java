package uapi.helper;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import uapi.config.Config;
import uapi.helper.ClassHelper;

public class ClassHelperTest {

    @Ignore
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

    @Test
    public void testGetElementType() throws Exception {
        ChangeableBoolean isCollection = new ChangeableBoolean();
        Field listField = CollectionService.class.getDeclaredField("listField");
        assertNotNull(listField);
        assertEquals(String.class, ClassHelper.getElementType(listField.getType(), listField.getGenericType(), isCollection));
        assertTrue(isCollection.get());
        
        isCollection = new ChangeableBoolean();
        Field mapField = CollectionService.class.getDeclaredField("mapField");
        assertNotNull(mapField);
        assertEquals(Integer.class, ClassHelper.getElementType(mapField.getType(), mapField.getGenericType(), isCollection));
        assertTrue(isCollection.get());
        
        isCollection = new ChangeableBoolean();
        Field mixField = CollectionService.class.getDeclaredField("mixField");
        assertNotNull(mixField);
        assertEquals(Float.class, ClassHelper.getElementType(mixField.getType(), mixField.getGenericType(), isCollection));
        assertTrue(isCollection.get());
        
        isCollection = new ChangeableBoolean();
        Field mixField2 = CollectionService.class.getDeclaredField("mixField2");
        assertNotNull(mixField2);
        assertEquals(Double.class, ClassHelper.getElementType(mixField2.getType(), mixField2.getGenericType(), isCollection));
        assertTrue(isCollection.get());
        
        isCollection = new ChangeableBoolean();
        Field test = CollectionService.class.getDeclaredField("test");
        assertNotNull(test);
        assertEquals(FakeClass.class, ClassHelper.getElementType(test.getType(), test.getGenericType(), isCollection));
        assertTrue(isCollection.get());
        
        isCollection = new ChangeableBoolean();
        Field none = CollectionService.class.getDeclaredField("none");
        assertNotNull(none);
        assertEquals(FakeClass.class, ClassHelper.getElementType(none.getType(), none.getGenericType(), isCollection));
        assertFalse(isCollection.get());
    }

    private interface IFakeInterface<T extends Annotation> { }

    private final class FakeService implements IFakeInterface<Config> { }

    @SuppressWarnings("unused")
    private final class CollectionService {

        private List<String> listField;
        private Map<String, Integer> mapField;
        private Map<String, List<Float>> mixField;
        private List<Map<String, Double>> mixField2;
        private Map<String, FakeClass<String>> test;
        private FakeClass<String> none;
    }
    
    private final class FakeClass<T> {}
}
