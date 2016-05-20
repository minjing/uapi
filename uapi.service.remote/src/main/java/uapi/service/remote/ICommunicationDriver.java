package uapi.service.remote;

import uapi.IIdentifiable;
import uapi.helper.Pair;

import java.util.List;

/**
 * The service invocation driver is used to invoke service
 */
public interface ICommunicationDriver extends IIdentifiable<String> {

    Object request(List<Pair> params, Object... args);
}
