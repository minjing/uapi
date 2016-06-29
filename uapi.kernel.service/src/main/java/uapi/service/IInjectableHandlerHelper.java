/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.annotation.ClassMeta;
import uapi.annotation.IHandlerHelper;

/**
 * A helper for maintain injectable annotation at build-time
 */
public interface IInjectableHandlerHelper extends IHandlerHelper {

    String name = "InjectableHelper";

    void setDependencyOptional(ClassMeta.Builder classBuilder, String serviceId, boolean optional);
}
