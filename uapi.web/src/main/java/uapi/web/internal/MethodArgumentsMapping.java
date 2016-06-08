/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.internal;

import rx.*;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;
import uapi.service.web.ArgumentMapping;
import uapi.service.web.IndexedArgumentMapping;

import java.util.*;

/**
 * Created by xquan on 5/11/2016.
 */
public final class MethodArgumentsMapping {

    private final String _name;
    private final String _rtnTypeName;
    private final List<ArgumentMapping> _argMappings;

    MethodArgumentsMapping(
            final String name,
            final String returnTypeName
    ) {
        ArgumentChecker.required(name, "name");
        ArgumentChecker.required(returnTypeName, "returnTypeName");
        this._name = name;
        this._rtnTypeName = returnTypeName;
        this._argMappings = new ArrayList<>();
    }

    void addArgumentMapping(
            final ArgumentMapping argMapping
    ) {
        ArgumentChecker.required(argMapping, "argMapping");
        this._argMappings.add(argMapping);
    }

    public String getName() {
        return this._name;
    }

    public String getReturnTypeName() {
        return this._rtnTypeName;
    }

//    public String getPath() {
//        StringBuilder buffer = new StringBuilder();
//        List<IndexedArgumentMapping> uriArgs = new ArrayList<>();
//        rx.Observable.from(this._argMappings)
//                .filter(argMapping -> argMapping instanceof IndexedArgumentMapping)
//                .map(argMapping -> (IndexedArgumentMapping) argMapping)
//                .subscribe(uriArgs::add);
//        if (uriArgs.size() == 0) {
//            return StringHelper.EMPTY;
//        }
//        Collections.sort(uriArgs, (arg1, arg2) -> {
//            int idx1 = arg1.getIndex();
//            int idx2 = arg2.getIndex();
//            if (idx1 == idx2) {
//                throw new KernelException("Do not allow both uri argument are same index {} and {}", arg1, arg2);
//            }
//            if (idx1 < idx2) {
//                return -1;
//            } else {
//                return 1;
//            }
//        });
//        for (int i = 0; i < uriArgs.size(); i++) {
//            IndexedArgumentMapping uriArg = uriArgs.get(i);
//            if (uriArg.getIndex() != i) {
//                throw new KernelException("Expect uri index {} but found {} - {}", i,uriArg.getIndex(), uriArg);
//            }
//            buffer.append(uriArg.)
//        }
//        return buffer.toString();
//    }

    public List<ArgumentMapping> getArgumentMappings() {
        return this._argMappings;
    }

    @Override
    public String toString() {
        return StringHelper.makeString("MethodArgumentsMapping[name={},returnTypeName={},argMapping={}]",
                this._name, this._rtnTypeName, this._argMappings);
    }
}
