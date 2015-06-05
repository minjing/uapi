package uapi.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.service.IService;
import uapi.service.Inject;

public class FileBasedConfigSource extends TraceableConfigSource implements IService {

    @Inject
    private final Map<String /* file extension */, IConfigFileParser> _parsers;

    public FileBasedConfigSource() {
        this._parsers = new HashMap<>();
    }

    public void addParser(IConfigFileParser parser) {
        if (parser == null) {
            throw new InvalidArgumentException("parser", InvalidArgumentType.EMPTY);
        }
        String[] fileExts = parser.supportedFileExtensions();
        Stream.of(fileExts).forEach((fileExt) -> {
            this._parsers.put(fileExt, parser);
        });
    }
}
