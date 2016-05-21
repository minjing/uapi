package uapi.service.web;

import uapi.InvalidArgumentException;

/**
 * Created by xquan on 5/3/2016.
 */
public class IndexedArgumentMapping extends ArgumentMapping {

    private final int _idx;

    public int getIndex() {
        return this._idx;
    }

    public IndexedArgumentMapping(
            final ArgumentFrom from,
            final String type,
            final int index
    ) throws InvalidArgumentException {
        super(from, type);
        this._idx = index;
    }
}
