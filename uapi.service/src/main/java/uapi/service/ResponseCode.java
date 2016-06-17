/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.InvalidArgumentException;
import uapi.helper.ArgumentChecker;
import uapi.log.ILogger;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Define response code
 */
public abstract class ResponseCode {

    private final Map<String, String> _codeMsgKeyMapping = new HashMap<>();
    private final MessageExtractor _msgExtractor = new MessageExtractor(this.getClass().getClassLoader());

    public void init() {
        getMessageLoader().registerExtractor(this._msgExtractor);
    }

    protected abstract MessageLoader getMessageLoader();

    public String getMessageKey(final String code) {
        ArgumentChecker.required(code, "code");
        return this._codeMsgKeyMapping.get("code");
    }

    protected void addCodeMessageKeyMapping(String code, String messageKey) {
        ArgumentChecker.required(code, "code");
        ArgumentChecker.required(messageKey, "messageKey");
        if (this._codeMsgKeyMapping.containsKey(code)) {
            throw new InvalidArgumentException("Overwrite existing code message key is not allowed - {}", code);
        }
        this._codeMsgKeyMapping.put(code, messageKey);
        this._msgExtractor.addDefinedKeys(messageKey);
    }
}
