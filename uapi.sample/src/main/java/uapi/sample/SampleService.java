package uapi.sample;

import uapi.config.Config;
import uapi.log.ILogger;
import uapi.service.IService;
import uapi.service.Inject;

public final class SampleService implements IService {

    @Inject
    private ILogger _logger;

    public void setLogger(ILogger logger) {
        this._logger = logger;
    }

    @Config(qualifier="text")
    public void config(String oldCfg, String newCfg) {
        this._logger.info("Receive configuration {}", newCfg);
    }
}
