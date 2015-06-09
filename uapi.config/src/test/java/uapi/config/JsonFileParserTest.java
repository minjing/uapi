package uapi.config;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import uapi.config.internal.JsonFileParser;

public class JsonFileParserTest {

    @Test
    public void testMapParse() {
        JsonFileParser parser = new JsonFileParser();
        Map<String, Object> config = parser.parse(new File("src/test/resources/config-map.json"));
        assertNotNull(config);
        assertEquals("string", config.get("string-value"));
        assertEquals(2, config.get("int-value"));
        List<?> arr = (List<?>) config.get("array");
        assertEquals("string-item", arr.get(0));
        assertEquals(34, arr.get(1));
    }
}
