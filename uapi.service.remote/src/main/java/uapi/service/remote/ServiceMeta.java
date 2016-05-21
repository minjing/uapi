package uapi.service.remote;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.service.web.ArgumentMapping;

import java.util.Collections;
import java.util.List;

/**
 * Hold remote service meta information
 */
public final class ServiceMeta {

    private final String _name;
    private List<ArgumentMapping> _argMappings;

    public ServiceMeta(
            final String name,
            final List<ArgumentMapping> argMappings) {
        ArgumentChecker.required(name, "name");
        ArgumentChecker.required(argMappings, "argMappings");
        this._name = name;
        this._argMappings = Collections.unmodifiableList(argMappings);
    }

    public String getName() {
        return this._name;
    }

    public List<ArgumentMapping> getArgumentMappings() {
        return this._argMappings;
    }

    public void updateArgumentMappings(List<ArgumentMapping> argMappings) {
        ArgumentChecker.required(argMappings, "argMappings");
        if (this._argMappings.size() != argMappings.size()) {
            throw new InvalidArgumentException(
                    "The service {} argument size is not matched, expect {} actually {}",
                    this._name, this._argMappings.size(), argMappings.size());
        }
        for (int i = 0; i < this._argMappings.size(); i++) {
            ArgumentMapping argMapping1 = this._argMappings.get(i);
            ArgumentMapping argMapping2 = argMappings.get(i);
            if (! argMapping1.getType().equals(argMapping2.getType())) {
                throw new InvalidArgumentException(
                        "Found unmatched argument in service {}, expected {} actually {}",
                        this._name, argMapping1, argMapping2);
            }
        }
        this._argMappings = Collections.unmodifiableList(argMappings);
    }
}
