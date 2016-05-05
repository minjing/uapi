package uapi.web;

/**
 * Created by min on 16/5/2.
 */
public enum HttpMethod {

    GET(0x1),
    PUT(0x2),
    POST(0x4),
    DELETE(0x8);

    private final int _value;

    HttpMethod(int value) {
        this._value = value;
    }

    public int getValue() {
        return this._value;
    }
}
