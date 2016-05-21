package uapi.service.web;

/**
 * Indicate where is the argument from.
 */
public enum ArgumentFrom {

    /**
     * The argument can be retrieved form HTTP header
     */
    Header,

    /**
     * The argument value can be retrieved from HTTP request query parameters, post parameter
     */
    Param,

    /**
     * The argument value can be retrieved from HTTP request URI
     */
    Uri
}
