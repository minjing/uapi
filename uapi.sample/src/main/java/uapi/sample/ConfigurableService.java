package uapi.sample;

import uapi.config.annotation.Config;
import uapi.log.ILogger;
import uapi.service.IRegistry;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

/**
 * Created by xquan on 4/5/2016.
 */
@Service
public class ConfigurableService {

    @Config(path="name")
    String _name;

    @Config(path="age")
    Integer _age;

    @Inject
    ILogger _logger;

    @Inject
    IRegistry _svcReg;

    @Init
    public void init() {
        this._logger.info("Configured {} = {}", "name", this._name);
        this._logger.info("Configured {} = {}", "age", this._age);
    }
}
