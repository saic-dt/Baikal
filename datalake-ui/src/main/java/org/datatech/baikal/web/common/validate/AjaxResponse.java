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

/**
 *
 */
package org.datatech.baikal.web.common.validate;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.datatech.baikal.common.exp.BizException;

/**
 * web 控制层返回的对象
 * RESULTCODE_SUCCESS 默认是200
 * <p>
 * Date:2018-08-01
 * <p>
 * Version: 1.0
 */
public class AjaxResponse implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1290830745905844589L;
    public static String RESULTCODE_SUCCESS = "200";
    private static Date CURRENTTIME = new Timestamp(System.currentTimeMillis());
    private Boolean success;
    private String message = "";
    private Date timestamp = CURRENTTIME;
    private String resultCode = RESULTCODE_SUCCESS;

    private Object data = "{}";

    public AjaxResponse() {
        this(Boolean.TRUE, "操作成功");
    }

    public AjaxResponse(Boolean success) {
        this(success, null);
    }

    public AjaxResponse(String message) {
        this(Boolean.TRUE, "操作成功");
    }

    public AjaxResponse(Date timestamp, Boolean success, String message, String resultCode, Object data) {
        this.success = success;
        this.message = message;
        this.timestamp = timestamp;
        this.resultCode = resultCode;
        this.data = data;
        if (this.message == null) {
            if (Boolean.FALSE.equals(success)) {
                this.message = "操作失败";
            }
            if (Boolean.TRUE.equals(success)) {
                this.message = "操作成功";
            }

        }
    }

    public AjaxResponse(Boolean success, String message, String resultCode, Object data) {
        this.success = success;
        this.message = message;
        this.resultCode = resultCode;
        this.data = data;
        if (this.message == null) {
            if (Boolean.FALSE.equals(success)) {
                this.message = "操作失败";
            }
            if (Boolean.TRUE.equals(success)) {
                this.message = "操作成功";
            }

        }
    }

    public AjaxResponse(Date timestamp, Boolean success, String message, String resultCode) {
        this.timestamp = timestamp;
        this.success = success;
        this.message = message;
        this.resultCode = resultCode;
        if (this.message == null) {
            if (Boolean.FALSE.equals(success)) {
                this.message = "操作失败";
            }
            if (Boolean.TRUE.equals(success)) {
                this.message = "操作成功";
            }

        }
    }

    public AjaxResponse(Date timestamp, Boolean success, String message, Object data) {
        this.timestamp = timestamp;
        this.success = success;
        this.message = message;
        this.data = data;
        if (this.message == null) {
            if (Boolean.FALSE.equals(success)) {
                this.message = "操作失败";
            }
            if (Boolean.TRUE.equals(success)) {
                this.message = "操作成功";
            }

        }
    }

    public AjaxResponse(Boolean success, String message) {
        this(success, message, "900", "{}");
    }

    public static AjaxResponse fail() {
        return fail("");
    }

    public static AjaxResponse fail(String message) {
        return new AjaxResponse(Boolean.FALSE, message);
    }

    public static AjaxResponse fail(Exception e) {
        return new AjaxResponse(Boolean.FALSE, e.getMessage());
    }

    public static AjaxResponse fail(String message, Exception e) {
        if (e instanceof BizException) {
            message = e.getMessage();
        } else {
            if (e.getMessage() != null && !"".equals(e.getMessage())) {
                message = e.getMessage();
            }
        }
        return new AjaxResponse(Boolean.FALSE, message);
    }

    public static AjaxResponse fail(String message, String resultCode, Object object) {
        return new AjaxResponse(CURRENTTIME, Boolean.FALSE, message, resultCode, object);
    }

    public static AjaxResponse fail(String message, Exception e, String resultCode, Object object) {
        if (e instanceof BizException) {
            message = e.getMessage();
        }
        return new AjaxResponse(CURRENTTIME, Boolean.FALSE, message, resultCode, object);
    }

    public static AjaxResponse fail(String message, String resultCode) {
        return new AjaxResponse(CURRENTTIME, Boolean.FALSE, message, resultCode);
    }

    public static AjaxResponse success() {
        return success(null);
    }

    public static AjaxResponse success(String message) {
        return new AjaxResponse(Boolean.TRUE, message);
    }

    public static AjaxResponse success(String message, Object object) {
        return AjaxResponse.success(message, RESULTCODE_SUCCESS, object);
    }

    public static AjaxResponse success(String message, String resultCode, Object object) {
        return new AjaxResponse(CURRENTTIME, Boolean.TRUE, message, resultCode, object);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
