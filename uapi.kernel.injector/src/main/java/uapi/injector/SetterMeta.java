package uapi.injector;

import uapi.annotation.MethodMeta;

/**
 * Created by xquan on 2/19/2016.
 */
public class SetterMeta extends MethodMeta {

    private SetterMeta(Builder builder) {
        super(builder);
    }

    public static class Builder extends MethodMeta.Builder {

        private String _injectType;

        private Builder() { }

        public void setInjectType(String type) {
            checkStatus();
            this._injectType = type;
        }
    }
}
