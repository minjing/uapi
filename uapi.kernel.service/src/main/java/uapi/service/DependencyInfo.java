/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.helper.ArgumentChecker;
import uapi.service.internal.QualifiedServiceId;

/**
 * Created by min on 16/5/14.
 */
public class DependencyInfo {

    private final String _fieldName;
    private final String _fieldType;
    private final String _dependencyId;
    private final String _injectFrom;
    private final boolean _generateField;

    public DependencyInfo(
            final String fieldName,
            final String fieldType
    ) {
        this(fieldName, fieldType, QualifiedServiceId.FROM_ANY, fieldType, false);
    }

    public DependencyInfo(
            final String fieldName,
            final String fieldType,
            final boolean generateField
    ) {
        this(fieldName, fieldType, QualifiedServiceId.FROM_ANY, fieldType, generateField);
    }

    public DependencyInfo(
            final String fieldName,
            final String fieldType,
            final String injectFrom,
            final String dependencyId,
            final boolean generateField
    ) {
        ArgumentChecker.required(fieldName, "fieldName");
        ArgumentChecker.required(fieldType, "fieldName");
        ArgumentChecker.required(dependencyId, "dependencyId");
        ArgumentChecker.required(injectFrom, "injectFrom");

        this._fieldName = fieldName;
        this._fieldType = fieldType;
        this._dependencyId = dependencyId;
        this._injectFrom = injectFrom;
        this._generateField = generateField;
    }

    public String getFieldName() {
        return this._fieldName;
    }

    public String getFieldType() {
        return this._fieldType;
    }

    public String getDependencyId() {
        return this._dependencyId;
    }

    public String getInjectFrom() {
        return this._injectFrom;
    }

    public boolean isGenerateField() {
        return this._generateField;
    }
}
