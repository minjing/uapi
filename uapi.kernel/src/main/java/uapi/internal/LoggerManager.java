package uapi.internal;

import org.slf4j.LoggerFactory;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.log.ILogger;
import uapi.service.IService;
import uapi.service.IServiceGenerator;
import uapi.service.Registration;
import uapi.service.Type;

@Registration({
    @Type(ILogger.class)
})
public class LoggerManager implements IService, IServiceGenerator<ILogger> {

    @Override
    public ILogger createService(Object serveFor) {
        if (serveFor == null) {
            throw new InvalidArgumentException("servFor", InvalidArgumentType.EMPTY);
        }
        return new Logger(LoggerFactory.getLogger(serveFor.getClass()));
    }
}
