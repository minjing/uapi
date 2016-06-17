/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.Guarder;
import uapi.rx.Looper;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Used to extract message form properties file
 */
public class MessageExtractor implements IMessageExtractor {

    private static final String RES_BUNDLE_NAME = "message";

    private final ClassLoader _clsLoader;

    private final List<String> _msgKeys = new LinkedList<>();

    private final Map<Locale, ResourceBundle> _resBundles = new HashMap<>();

    private final Lock _lock = new ReentrantLock();

    public MessageExtractor(ClassLoader classLoader) {
        ArgumentChecker.required(classLoader, "classLoader");
        this._clsLoader = classLoader;
    }

    public void addDefinedKeys(String... messageKeys) {
        ArgumentChecker.required(messageKeys, "messageKeys");
        Looper.from(messageKeys).foreach(this._msgKeys::add);
    }

    @Override
    public boolean isDefined(String messageKey) {
        return this._msgKeys.contains(messageKey);
    }

    @Override
    public String extract(String messageKey, Locale local) {
        ArgumentChecker.required(messageKey, "messageKey");
        ArgumentChecker.required(local, "local");
        ResourceBundle resourceBundle = Guarder.by(this._lock).runForResult(() -> {
            ResourceBundle resBundle = this._resBundles.get(local);
            if (resBundle == null) {
                resBundle = ResourceBundle.getBundle(RES_BUNDLE_NAME, local, this._clsLoader);
                if (resBundle == null) {
                    throw new KernelException("Can't find resource bundle by name {}", RES_BUNDLE_NAME);
                }
                this._resBundles.put(local, resBundle);
            }
            return resBundle;
        });
        return resourceBundle.getString(messageKey);
    }
}
