/*
 * The Log4FIX Software License
 * Copyright (c) 2006 opentradingsolutions.org  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Neither the name of the product (Log4FIX), nor opentradingsolutions.org,
 *    nor the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL OPENTRADINGSOLUTIONS.ORG OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package org.opentradingsolutions.log4fix.datadictionary;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.FixVersions;
import quickfix.SessionID;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian M. Coyner
 */
public class ClassPathDataDictionaryLoader implements DataDictionaryLoader {

    private Map<String,DataDictionary> dictionaryCache;

    public ClassPathDataDictionaryLoader() {
        dictionaryCache = new HashMap<String,DataDictionary>(5);
    }

    public DataDictionary loadDictionary(SessionID sessionId) {

        String beginString = sessionId.getBeginString();

        DataDictionary dictionary = dictionaryCache.get(beginString);
        if (dictionary != null) {
            return dictionary;
        }

        if (!(FixVersions.BEGINSTRING_FIX40.equals(beginString)
                || FixVersions.BEGINSTRING_FIX41.equals(beginString)
                || FixVersions.BEGINSTRING_FIX42.equals(beginString)
                || FixVersions.BEGINSTRING_FIX43.equals(beginString)
                || FixVersions.BEGINSTRING_FIX44.equals(beginString))) {

            throw new IllegalArgumentException("Invalid FIX BeginString: '" +
                    sessionId + "'.");
        }

        String dictionaryFileName = beginString.replaceAll("\\.", "") + ".xml";
        // the dictionary is loaded from the quickfix.jar file.
        InputStream ddis = Thread.currentThread().getContextClassLoader().
                getResourceAsStream(dictionaryFileName);
        if (ddis == null) {
            throw new NullPointerException("Data Dictionary file '" +
                    dictionaryFileName + "' not found at root of CLASSPATH.");
        }

        try {
            dictionary = new DataDictionary(ddis);
            dictionaryCache.put(beginString, dictionary);
            return dictionary;
        } catch (ConfigError configError) {
            throw new RuntimeException("Error loading data dictionary file.",
                    configError);
        }
    }
}
