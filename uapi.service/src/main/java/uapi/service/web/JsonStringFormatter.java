package uapi.service.web;

import com.fasterxml.jackson.jr.ob.JSON;
import uapi.IIdentifiable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.service.annotation.Service;

/**
 * A string formatter for JSON
 */
@Service(IStringFormatter.class)
public class JsonStringFormatter implements IStringFormatter, IIdentifiable<String> {

    public static final String NAME = "JSON";

    @Override
    public String getId() {
        return NAME;
    }

    @Override
    public String format(
            final Object value,
            final Class type
    ) throws KernelException {
        ArgumentChecker.required(value, "value");
        ArgumentChecker.required(type, "type");
        try {
            return JSON.std.asString(value);
        } catch (Exception ex) {
            throw new KernelException(ex);
        }
    }

    @Override
    public Object unformat(
            final String value,
            final Class type
    ) throws KernelException {
        ArgumentChecker.required(value, "value");
        ArgumentChecker.required(type, "type");
        try {
            return JSON.std.beanFrom(type, value);
        } catch (Exception ex) {
            throw new KernelException(ex);
        }
    }
}
