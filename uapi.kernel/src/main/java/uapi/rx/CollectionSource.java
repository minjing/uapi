package uapi.rx;

import uapi.helper.ArgumentChecker;

import java.util.*;

/**
 * A CollectionSource hold data source locally, unlike OrderedSource, it does not guarantee the items are ordered
 * which is depends on underlay implementation
 */
public class CollectionSource<T> extends Operator<T> {

    private final Collection<T> _items;
    private final java.util.Iterator<T> _itemsIte;

    CollectionSource(Collection<T> items) {
        ArgumentChecker.required(items, "items");
        this._items = items;
        this._itemsIte = items.iterator();
    }

    @Override
    boolean hasItem() {
        return this._itemsIte.hasNext();
    }

    @Override
    T getItem() {
        if (hasItem()) {
            return this._itemsIte.next();
        }
        return null;
    }
}
