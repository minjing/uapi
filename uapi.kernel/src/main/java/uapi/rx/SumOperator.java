/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.rx;

import uapi.KernelException;

/**
 * Created by min on 16/8/21.
 */
public class SumOperator<T> extends TerminatedOperator<T> {

    private SupportedType _type;

    private Integer _intSum = 0;
    private Float _floatSum = 0.0F;
    private Double _doubleSum = 0.0D;
    private Long _longSum = 0L;

    SumOperator(Operator<T> previously) {
        super(previously);
    }

    @Override
    T getItem() throws NoItemException {
        while (hasItem()) {
            try {
                T item = (T) getPreviously().getItem();
                if (item instanceof Integer) {
                    checkType(SupportedType.INT);
                    this._intSum += (Integer) item;
                } else if (item instanceof Float) {
                    checkType(SupportedType.FLOAT);
                    this._floatSum += (Float) item;
                } else if (item instanceof Double) {
                    checkType(SupportedType.DOUBLE);
                    this._doubleSum += (Double) item;
                } else if (item instanceof Long) {
                    checkType(SupportedType.LONG);
                    this._longSum += (Long) item;
                } else {
                    throw new KernelException("Unsupported type - {}", item);
                }
            } catch (NoItemException ex) {
                // do nothing
            }
        }
        switch (this._type) {
            case INT:
                return (T) this._intSum;
            case FLOAT:
                return (T) this._floatSum;
            case DOUBLE:
                return (T) this._doubleSum;
            case LONG:
                return (T) this._longSum;
            default:
                throw new KernelException("Unsupported type - {}", this._type);

        }
    }

    private void checkType(SupportedType type) {
        if (this._type == null) {
            this._type = type;
        } else {
            if (this._type != type) {
                throw new KernelException("The current item type {} does not match previously item type {}", type, this._type);
            }
        }
    }

    private enum SupportedType {
        INT, FLOAT, DOUBLE, LONG
    }
}
