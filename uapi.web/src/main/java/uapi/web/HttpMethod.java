package uapi.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by min on 16/5/2.
 */
public enum HttpMethod {

    GET(0x1),
    PUT(0x2),
    POST(0x4),
    DELETE(0x8);

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
        return httpMethods.toArray(new HttpMethod[httpMethods.size()]);
    }

    private final int _value;

    HttpMethod(int value) {
        this._value = value;
    }

    public int getValue() {
        return this._value;
    }
}
