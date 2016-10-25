package uapi.behavior.internal;

import uapi.behavior.IEventDrivenBehavior;
import uapi.behavior.IResponsible;
import uapi.helper.ArgumentChecker;

/**
 * Created by xquan on 10/11/2016.
 */
public class Responsible implements IResponsible {

    private String _name;

    public void setName(String name) {
        ArgumentChecker.required(name, "name");
        this._name = name;
    }

    @Override
    public String name() {
        return this._name;
    }

    @Override
    public IEventDrivenBehavior[] behaviors() {
        return null;
    }
}
