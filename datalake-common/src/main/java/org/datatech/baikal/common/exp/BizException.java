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

package org.datatech.baikal.common.exp;

/**
 * Datalake application related exception.
 */
public class BizException extends RuntimeException {
    private static final long serialVersionUID = -4022870552336761084L;

    public BizException() {
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public static void throwWhenFalse(boolean maybeFalse, String msgToUsr, Throwable cause) throws BizException {
        if (!maybeFalse) {
            throw new BizException(msgToUsr, cause);
        }
    }

    public static void throwWhenFalse(boolean maybeFalse, String msgToUsr) throws BizException {
        if (!maybeFalse) {
            throw new BizException(msgToUsr);
        }
    }

    public static void throwWhenTrue(boolean maybeTrue, String msgToUsr, Throwable cause) throws BizException {
        if (!maybeTrue) {
            throw new BizException(msgToUsr, cause);
        }
    }

    public static void throwWhenTrue(boolean maybeTrue, String msgToUsr) throws BizException {
        if (maybeTrue) {
            throw new BizException(msgToUsr);
        }
    }

    public static void throwWhenNull(Object objMayBeNull, String msgToUsr, Throwable cause) throws BizException {
        if (objMayBeNull == null) {
            throw new BizException(msgToUsr, cause);
        }
    }

    public static void throwWhenNull(Object objMayBeNull, String msgToUsr) throws BizException {
        if (objMayBeNull == null) {
            throw new BizException(msgToUsr);
        }
    }

    public static void throwWhenNotNull(Object objMayBeNull, String msgToUsr) throws BizException {
        if (objMayBeNull != null) {
            throw new BizException(msgToUsr);
        }
    }
}