package uapi.web;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;

/**
 * Created by xquan on 5/3/2016.
 */
public class NamedArgumentMapping extends ArgumentMapping {

    private final String _name;

    public String getName() {
        return this._name;
    }

    public NamedArgumentMapping(
            final From from,
            final String type,
            final String name
    ) throws InvalidArgumentException {
        super(from, type);
        ArgumentChecker.required(name, "name");
        this._name = name;
    }
}
