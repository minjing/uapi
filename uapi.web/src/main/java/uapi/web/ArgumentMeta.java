package uapi.web;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;

/**
 * Created by xquan on 5/3/2016.
 */
public abstract class ArgumentMeta {

    public enum From {
        /**
         * The argument can be retrieved form HTTP header
         */
        Header,

        /**
         * The argument value can be retrieved from HTTP request query parameters, post parameter
         */
        Param,

        /**
         * The argument vaue can be retrieved from HTTP request URI
         */
        Uri
    }

    private final From _from;
    private final String _type;

    public From getFrom() {
        return this._from;
    }

    public String getType() {
        return this._type;
    }

    public ArgumentMeta(
            final From from,
            final String type
    ) throws InvalidArgumentException {
        ArgumentChecker.required(from, "from");
        ArgumentChecker.required(type, "type");
        this._from = from;
        this._type = type;
    }
}
