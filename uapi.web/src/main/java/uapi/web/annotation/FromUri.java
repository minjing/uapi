package uapi.web.annotation;

/**
 * Indicate the value should be a part of uri
 * For example the request uri is: GET /ws/user/1, the ws is web service prefix.
 * The user is web service name, the "1" is user id, so if you want to fetch the
 * user id from uri, you need tell the index of uri, the web service name index
 * is 0, so the user id is index 1.
 */
public @interface FromUri {

    /**
     * The index of uri part
     *
     * @return  The index of uri
     */
    int value();
}
