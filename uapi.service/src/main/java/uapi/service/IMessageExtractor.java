/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import java.util.Locale;

/**
 * A IMessageExtractor is used to load message locally
 */
public interface IMessageExtractor {

    /**
     * Check this message extractor defines specific message key or not
     *
     * @param   messageKey
     *          The message key which will be checked
     * @return  True if this message extractor defines the message key otherwise will return false
     */
    boolean isDefined(String messageKey);

    /**
     * Extract message by specific message key and local
     *
     * @param   messageKey
     *          The message key
     * @param   local
     *          The local
     * @return  The message text or null if no message was bind with specific message key
     */
    String extract(String messageKey, Locale local);
}
