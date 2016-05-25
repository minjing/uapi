package uapi.service.web;

import uapi.KernelException;

/**
 * Created by xquan on 5/25/2016.
 */
public interface IStringFormatter<T> {

    String format(T value, Class<T> type) throws KernelException;

    T unformat(String value, Class<T> type) throws KernelException;
}
