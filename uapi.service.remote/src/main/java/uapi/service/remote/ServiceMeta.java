/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;
import uapi.service.web.ArgumentMapping;

import java.util.Collections;
import java.util.List;

/**
 * Hold remote service meta information
 */
public class ServiceMeta {

    private final String _name;
    private final String _returnTypeName;
    private List<ArgumentMapping> _argMappings;

    public ServiceMeta(
            final String name,
            final String returnTypeName,
            final List<ArgumentMapping> argMappings) {
        ArgumentChecker.required(name, "name");
        ArgumentChecker.required(returnTypeName, "valueParserName");
        ArgumentChecker.required(argMappings, "argMappings");
        this._name = name;
        this._returnTypeName = returnTypeName;
        this._argMappings = Collections.unmodifiableList(argMappings);
    }

    public String getName() {
        return this._name;
    }

    public String getReturnTypeName() {
        return this._returnTypeName;
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

//    /**
//     * Return name of the communicator which can handle this ServiceMeta
//     *
//     * @return Name of the communicator
//     */
//    public abstract String getCommunicatorName();

    @Override
    public String toString() {
        return StringHelper.makeString("name={},returnTypeName={},argMappings={}",
                this._name, this._returnTypeName, CollectionHelper.asString(this._argMappings));
    }
}
