package uapi.sample;

import uapi.config.Config;
import uapi.log.ILogger;
import uapi.service.IService1;
import uapi.service.InitAtLaunch;
import uapi.service.Inject;

@InitAtLaunch
public final class SampleService1 implements IService1 {

    @Inject
    private ILogger _logger;

    public void setLogger(ILogger logger) {
        this._logger = logger;
    }

    @Config("text")
    public void config(String oldCfg, String newCfg) {
        this._logger.info("Receive configuration - {}", newCfg);
    }
}
