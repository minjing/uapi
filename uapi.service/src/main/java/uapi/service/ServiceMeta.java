/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.StringHelper;

import java.util.List;

/**
 * Hold remote service meta information
 */
public class ServiceMeta extends MethodMeta {

//    private final String _name;
//    private final String _returnTypeName;
//    private List<ArgumentMeta> _argMappings;
//
    private String _id;

    public ServiceMeta(
            final String name,
            final String returnTypeName
    ) {
        this(name, returnTypeName, null);
    }

    public ServiceMeta(
            final String name,
            final String returnTypeName,
            final List<ArgumentMeta> argMappings) {
        super(name, returnTypeName, argMappings);
    }

    public String getId() {
        return this._id;
    }

    public void setId(final String id) {
        ArgumentChecker.required(id, "id");
        this._id = id;
    }
//
//    public String getName() {
//        return this._name;
//    }
//
//    public String getReturnTypeName() {
//        return this._returnTypeName;
//    }
//
//    public List<ArgumentMeta> getArgumentMappings() {
//        return this._argMappings;
//    }
//
//    public boolean isSame(ServiceMeta other) {
//        if (other == null) {
//            return false;
//        }
//        if (! this._name.equals(other._name)) {
//            return false;
//        }
//        if (! this._returnTypeName.equals(other._name)) {
//            return false;
//        }
//        if (this._argMappings.size() != other._argMappings.size()) {
//            return false;
//        }
//        for (int i = 0; i < this._argMappings.size(); i++) {
//            if (! this._argMappings.get(i).isSameType(other._argMappings.get(i))) {
//                return false;
//            }
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return StringHelper.makeString("name={},returnTypeName={},argMappings={}",
                getName(), getReturnTypeName(), CollectionHelper.asString(getArgumentMappings()));
    }
}
