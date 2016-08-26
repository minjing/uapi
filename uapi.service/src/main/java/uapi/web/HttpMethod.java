/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import uapi.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

/**
 * Define http method
 */
public enum HttpMethod {

    GET(HttpMethod.Get),
    PUT(HttpMethod.Put),
    POST(HttpMethod.Post),
    DELETE(HttpMethod.Delete),
    OPTIONS(HttpMethod.Options),
    HEAD(HttpMethod.Head),
    TRACE(HttpMethod.Trace);

    public static final int Get     = 0x1;
    public static final int Put     = 0x2;
    public static final int Post    = 0x4;
    public static final int Delete  = 0x8;
    public static final int Options = 0x16;
    public static final int Head    = 0x32;
    public static final int Trace   = 0x64;

    @JsonCreator
    public static HttpMethod[] parse(int value) {
        List<HttpMethod> httpMethods = new ArrayList<>();
        if ((value & GET._value) == GET._value) {
            httpMethods.add(GET);
        }
        if ((value & PUT._value) == PUT._value) {
            httpMethods.add(PUT);
        }
        if ((value & POST._value) == POST._value) {
            httpMethods.add(POST);
        }
        if ((value & DELETE._value) == DELETE._value) {
            httpMethods.add(DELETE);
        }
        if ((value & OPTIONS._value) == OPTIONS._value) {
            httpMethods.add(OPTIONS);
        }
        if ((value & HEAD._value) == HEAD._value) {
            httpMethods.add(HEAD);
        }
        if ((value & TRACE._value) == TRACE._value) {
            httpMethods.add(TRACE);
        }
        return httpMethods.toArray(new HttpMethod[httpMethods.size()]);
    }

    public static HttpMethod parse(String value) {
        if (HttpMethod.GET.name().equalsIgnoreCase(value)) {
            return HttpMethod.GET;
        } else if (HttpMethod.PUT.name().equalsIgnoreCase(value)) {
            return HttpMethod.PUT;
        } else if (HttpMethod.POST.name().equalsIgnoreCase(value)) {
            return HttpMethod.POST;
        } else if (HttpMethod.DELETE.name().equalsIgnoreCase(value)) {
            return HttpMethod.DELETE;
        } else if (HttpMethod.OPTIONS.name().equalsIgnoreCase(value)) {
            return HttpMethod.OPTIONS;
        } else if (HttpMethod.HEAD.name().equalsIgnoreCase(value)) {
            return HttpMethod.HEAD;
        } else if (HttpMethod.TRACE.name().equalsIgnoreCase(value)) {
            return HttpMethod.TRACE;
        } else {
            throw new InvalidArgumentException("No HttpMethod can be mapped to {}", value);
        }
    }

    private String _aaa;
    private final int _value;

    HttpMethod(int value) {
        this._value = value;
    }

    @JsonValue
    public int getValue() {
        return this._value;
    }

    public void setAA(String bbb) {
        this._aaa = bbb;
    }

    public class AA {

        public void test() {
            HttpMethod method = HttpMethod.GET;
            method.setAA("bb");

        }
    }
}
