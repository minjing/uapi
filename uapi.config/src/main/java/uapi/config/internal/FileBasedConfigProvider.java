package uapi.config.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.base.Strings;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.KernelException;
import uapi.config.Config;
import uapi.config.IConfigFileParser;
import uapi.internal.TraceableConfigProvider;
import uapi.log.ILogger;
import uapi.service.IService;
import uapi.service.Inject;
import uapi.service.OnInit;

public class FileBasedConfigProvider
    extends TraceableConfigProvider
    implements IService {

    private static final String CFG_QUALIFIER   = "config";

    @Inject
    private ILogger _logger;

    @Inject
    private final Map<String /* file extension */, IConfigFileParser> _parsers;

    public FileBasedConfigProvider() {
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

    public void setLogger(ILogger logger) {
        this._logger = logger;
    }

    @OnInit
    public void init() {
        
    }

    @Config(qualifier=CFG_QUALIFIER)
    public void config(String oldFileName, String newFileName) {
        this._logger.info("Config update {} -> {}", oldFileName, newFileName);
        if (Strings.isNullOrEmpty(newFileName)) {
            throw new InvalidArgumentException("fileName", InvalidArgumentType.EMPTY);
        }
        File cfgFile = new File(newFileName);
        if (! cfgFile.exists()) {
            throw new KernelException("The config file {} does not exist.", newFileName);
        }
        if (! cfgFile.isFile()) {
            throw new KernelException("The config file {} is not a file.", newFileName);
        }
        if (! cfgFile.canRead()) {
            throw new KernelException("The config file {} can't be read.", newFileName);
        }
        int posDot = newFileName.lastIndexOf('.');
        if (posDot <= 0) {
            throw new KernelException("The config file {} must has a extension name.", newFileName);
        }
        String extName = newFileName.substring(posDot + 1);
        IConfigFileParser parser = this._parsers.get(extName);
        if (parser == null) {
            throw new KernelException("No parser associate with extension name {} on config file {}.", extName, newFileName);
        }
        Map<String, Object> config = parser.parse(cfgFile);
        config.forEach((key, value) -> { onChange(key, value); });
    }
}
