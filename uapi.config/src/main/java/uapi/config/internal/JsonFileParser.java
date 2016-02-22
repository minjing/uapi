package uapi.config.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.jr.ob.JSON;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.KernelException;
import uapi.config.IConfigFileParser;
import uapi.service.IService1;
import uapi.service.Registration;
import uapi.service.Type;

@Registration({
    @Type(IConfigFileParser.class)
})
public class JsonFileParser
    implements IService1, IConfigFileParser {

    private static final String JSON_FILE_EXT   = "json";

    @Override
    public String[] supportedFileExtensions() {
        return new String[] { JSON_FILE_EXT };
    }

    @Override
    public Map<String, Object> parse(File configFile) {
        if (configFile == null) {
            throw new InvalidArgumentException("configFile", InvalidArgumentType.EMPTY);
        }
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            throw new KernelException(e, "The config file {} does not exist.", configFile.getName());
        }
        Map<String, Object> config = null;
        JSON.std.with(JSON.Feature.READ_ONLY);
        try {
            config = JSON.std.mapFrom(fin);
        } catch (IOException e) {
            throw new KernelException(e, "Parse config file {} failed.", configFile.getName());
        }
        return config;
    }
}
