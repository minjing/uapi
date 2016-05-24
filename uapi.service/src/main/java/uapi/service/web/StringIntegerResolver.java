package uapi.service.web;

/**
 * Created by xquan on 5/24/2016.
 */
public class StringIntegerResolver implements IStringResolver<Integer> {
    @Override
    public String getId() {
        return Integer.class.getCanonicalName();
    }

    @Override
    public Integer encode(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public String decode(Integer value) {
        return String.valueOf(value);
    }
}
