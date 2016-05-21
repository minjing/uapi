package uapi.service.web;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;

/**
 * Created by xquan on 5/3/2016.
 */
public class ArgumentMapping {

    private ArgumentFrom _from;
    private String _type;

    public ArgumentFrom getFrom() {
        return this._from;
    }

    public String getType() {
        return this._type;
    }

    public ArgumentMapping(
            final String type) {
        ArgumentChecker.required(type, "type");
        this._type = type;
    }

    public ArgumentMapping(
            final ArgumentFrom from,
            final String type
    ) throws InvalidArgumentException {
        ArgumentChecker.required(from, "from");
        ArgumentChecker.required(type, "type");
        this._from = from;
        this._type = type;
    }

    @Override
    public String toString() {
        return "ArgumentMapping[from=" + this._from + ", type=" + this._type + "]";
    }
}
