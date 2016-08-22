/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.http;

import uapi.KernelException;
import uapi.rx.Looper;
import uapi.rx.NoItemException;

/**
 * Created by xquan on 8/22/2016.
 */
public enum ContentType {

    FORM_URLENCODED("application/x-www-form-urlencoded"),
    TEXT("text/plain"),
    JSON("application/json"),
    JAVASCRIPT("application/javascript"),
    XML("text/xml"),
    HTML("text/html");

    public static ContentType parse(String typeName) {
        try {
            return Looper.from(ContentType.values())
                    .filter(contentType -> contentType.typeName().equalsIgnoreCase(typeName))
                    .first();
        } catch (NoItemException ex) {
            throw new KernelException("No ContentType can be mapped to {}", typeName);
        }
    }

    private final String _typeName;

    ContentType(String name) {
        this._typeName = name;
    }

    public String typeName() {
        return this._typeName;
    }
}
