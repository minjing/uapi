/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import com.google.common.base.Strings;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;
import uapi.rx.Looper;
import uapi.service.annotation.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by xquan on 6/16/2016.
 */
@Service
public class MessageLoader {

    List<IMessageExtractor> _msgExtractors = new LinkedList<>();

    public void registerExtractor(IMessageExtractor messageExtractor) {
        ArgumentChecker.required(messageExtractor, "messageExtractor");
        this._msgExtractors.add(messageExtractor);
    }

    public String load(String messageKey) {
        return load(messageKey, Locale.ENGLISH);
    }

    public String load(String messageKey, Locale local) {
        return load(messageKey, local, new String[0]);
    }

    public String load(String messageKey, String... args) {
        return load(messageKey, Locale.ENGLISH, args);
    }

    public String load(String messageKey, Locale local, String... args) {
        ArgumentChecker.required(messageKey, "messageKey");
        IMessageExtractor msgExtractor = Looper.from(this._msgExtractors)
                .filter(extractor -> extractor.isDefined(messageKey)).first();
        if (msgExtractor == null) {
            throw new KernelException("No message extractor defines message key {}", messageKey);
        }
        String msg = msgExtractor.extract(messageKey, local);
        if (Strings.isNullOrEmpty(msg)) {
            return msg;
        }
        return StringHelper.makeString(msg, args);
    }
}
