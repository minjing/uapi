package uapi.web;

import uapi.InvalidArgumentException;
import uapi.helper.Builder;

/**
 * Created by min on 16/6/11.
 */
public class RestfulResponse {

    public static final String TYPE     = "type";
    public static final String MESSAGE  = "message";
    public static final String DATA     = "data";

    private final class ResponseBuilder extends Builder {

        @Override
        protected void validation() throws InvalidArgumentException {

        }

        @Override
        protected void initProperties() {

        }

        @Override
        protected Object createInstance() {
            return null;
        }
    }

}
