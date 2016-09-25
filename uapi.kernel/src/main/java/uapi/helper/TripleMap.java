/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.helper;

import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The TripleMap is Map's map
 *
 * @param   <P>
 *          left value type
 * @param   <S>
 *          right value type
 * @param   <V>
 *          the value type
 */
public class TripleMap<P, S, V> {

    private final Map<P, Map<S, V>> _store;

    public TripleMap() {
        this._store = new HashMap<>();
    }

    public void put(P primaryKey, S secondaryKey) {
        put(primaryKey, secondaryKey, null);
    }

    public void put(P primaryKey, S secondaryKey, V value) {
        ArgumentChecker.required(primaryKey, "primaryKey");
        ArgumentChecker.required(secondaryKey, "secondaryKey");
        Map<S, V> secondaryMap = this._store.get(primaryKey);
        if (secondaryMap == null) {
            secondaryMap = new HashMap<>();
            this._store.put(primaryKey, secondaryMap);
        }
        secondaryMap.put(secondaryKey, value);
    }

    public void put(P primaryKey, List<S> secondaryKeys) {
        ArgumentChecker.required(primaryKey, "primaryKey");
        ArgumentChecker.required(secondaryKeys, "secondaryKeys");
        Observable.from(secondaryKeys).subscribe(secondaryKey -> put(primaryKey, secondaryKey));
    }

    public Map<S, V> get(P primaryKey) {
        ArgumentChecker.required(primaryKey, "primaryKey");
        return this._store.get(primaryKey);
    }

    public V get(P primaryKey, S secondaryKey) {
        ArgumentChecker.required(primaryKey, "primaryKey");
        ArgumentChecker.required(secondaryKey, "secondaryKey");
        Map<S, V> secondaryMap = get(primaryKey);
        if (secondaryKey == null) {
            return null;
        }
        return secondaryMap.get(secondaryKey);
    }

    public int size() {
        return this._store.size();
    }

    public Set<Map.Entry<P, Map<S, V>>> entrySet() {
        return this._store.entrySet();
    }

    public boolean hasEmptyValue(P primaryKey) {
        ArgumentChecker.required(primaryKey, "primaryKey");
        Map<S, V> secondaryMap = this._store.get(primaryKey);
        if (secondaryMap == null) {
            return true;
        }
        for (Map.Entry entry : secondaryMap.entrySet()) {
            if (entry.getValue() == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("TripleMap{}", MapHelper.asString(this._store));
    }
}
