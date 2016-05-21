package uapi.service.remote;

import uapi.IIdentifiable;
import uapi.helper.Pair;

import java.util.List;

/**
 * The service invocation driver is used to invoke service
 */
public interface ICommunicator extends IIdentifiable<String> {

    Object request(ServiceMeta serviceMeta, Object... args);
}
