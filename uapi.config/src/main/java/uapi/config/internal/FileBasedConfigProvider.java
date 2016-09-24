/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import uapi.KernelException;
import uapi.config.IConfigFileParser;
import uapi.config.IConfigTracer;
import uapi.config.IConfigurable;
import uapi.helper.ArgumentChecker;
import uapi.log.ILogger;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

@Service
@Tag("Config")
public class FileBasedConfigProvider implements IConfigurable {

    static final String CFG_FILE_PATH  = "cli.config";

    @Inject
    protected ILogger _logger;

    @Inject
    protected IConfigTracer _cfgTracer;

    @Inject
    protected List<IConfigFileParser> _parsers = new ArrayList<>();

    private String _configPath;

    @Init
    protected void init() {
        this._logger.info("Config path is {}", this._configPath);
    }

    @Override
    public String[] getPaths() {
        return new String[] { CFG_FILE_PATH };
    }

    @Override
    public boolean isOptionalConfig(String path) {
        if (CFG_FILE_PATH.equals(path)) {
            return false;
        }
        throw new KernelException("Unsupported configuration path {} for service {}", path, FileBasedConfigProvider.class.getName());
    }

    @Override
    public void config(String path, Object configObject) {
        ArgumentChecker.notEmpty(path, "path");
        ArgumentChecker.notNull(configObject, "configObject");
        if (! CFG_FILE_PATH.equals(path)) {
            throw new KernelException("The config {} for path {} does not belongs to this service", configObject, path);
        }
        this._configPath = configObject.toString();

        this._logger.info("Config update {} -> {}", path, configObject);
        File cfgFile = new File(configObject.toString());
        if (! cfgFile.exists()) {
            throw new KernelException("The config file {} does not exist.", configObject);
        }
        if (! cfgFile.isFile()) {
            throw new KernelException("The config file {} is not a file.", configObject);
        }
        if (! cfgFile.canRead()) {
            throw new KernelException("The config file {} can't be read.", configObject);
        }

        String fileName = cfgFile.getName();
        int posDot = fileName.lastIndexOf('.');
        if (posDot <= 0) {
            throw new KernelException("The config file {} must contains a extension name.", configObject);
        }
        String extName = cfgFile.getName().substring(posDot + 1);
        Optional<IConfigFileParser> parser = this._parsers.stream()
                .filter(cfgParser -> cfgParser.isSupport(extName))
                .findFirst();
        if (! parser.isPresent()) {
            throw new KernelException("No parser associate with extension name {} on config file {}.", extName, fileName);
        }
        Map<String, Object> config = parser.get().parse(cfgFile);
        if (config == null) {
            this._logger.warn("No any configuration in the config file {}", cfgFile);
            return;
        }
        this._cfgTracer.onChange(config);
    }
}
