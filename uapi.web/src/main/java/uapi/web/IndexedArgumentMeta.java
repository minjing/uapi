package uapi.web;

import uapi.InvalidArgumentException;

/**
 * Created by xquan on 5/3/2016.
 */
public class IndexedArgumentMeta extends ArgumentMeta {

    private final int _idx;

    public int getIndex() {
        return this._idx;
    }

    public IndexedArgumentMeta(
            final From from,
            final String type,
            final int index
    ) throws InvalidArgumentException {
        super(from, type);
        this._idx = index;
    }
}
