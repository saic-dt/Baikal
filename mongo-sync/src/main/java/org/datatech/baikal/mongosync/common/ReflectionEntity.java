/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datatech.baikal.mongosync.common;

import java.lang.reflect.Method;
import java.util.Date;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use reflection method to access object.
 */
public class ReflectionEntity {
    private static final Logger logger = LoggerFactory.getLogger(ReflectionEntity.class);
    public final static Class DOCUMENT_CLASS = Document.class;
    public final static Class DATE_CLASS = Date.class;
    public final static Class BINARY_CLASS = org.bson.types.Binary.class;
    public final static Class STRING_CLASS = String.class;
    public static Method DOCUMENT_KEY_SET_METHOD;
    public static Method DOCUMENT_GET_METHOD;

    static {
        try {
            DOCUMENT_KEY_SET_METHOD = DOCUMENT_CLASS.getMethod("keySet");
            DOCUMENT_GET_METHOD = DOCUMENT_CLASS.getMethod("get", Object.class);
        } catch (NoSuchMethodException e) {
            logger.error("Exception occurred.", e);
        }
    }

}
