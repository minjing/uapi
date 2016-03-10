package uapi.log.internal;

import org.slf4j.LoggerFactory;

import uapi.InvalidArgumentException;
import uapi.InvalidArgumentException.InvalidArgumentType;
import uapi.log.ILogger;
import uapi.service.*;
import uapi.service.annotation.Service;

@Service
public class LoggerManager implements IServiceFactory<ILogger> {

    @Override
    public ILogger createService(Object serveFor) {
        if (serveFor == null) {
            throw new InvalidArgumentException("servFor", InvalidArgumentType.EMPTY);
        }
        return new Logger(LoggerFactory.getLogger(serveFor.getClass()));
    }
}
