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

package org.datatech.baikal.web.common;

import java.util.HashSet;
import java.util.Set;

/**
 * 枚举类 放置枚举类型的数据
 */
public class Enums {

    public enum ResultCode {

        // 用户异常状态
        //用户异常状态
        RESULTCODE_SUCCESS("200", "操作成功"), RESULTCODE_RESETPASSWORD("201", "强制登录修改密码"), RESULTCODE_FAIL("900",
                "操作失败"), RESULTCODE_NOTLOGIN("910", "用户未登录"), RESULTCODE_LOGINFAIL("911",
                        "用户名密码错误"), RESULTCODE_MORELOGIN("909", "登录次数过多"), RESULTCODE_UNAUTHORIZED("912",
                                "用户没有操作权限"), RESULTCODE_ERROR("901", "未知错误"), RESULTCODE_CERTIFICATE_ERROR("902",
                                        "凭证错误"), RESULTCODE_NONE_ACCOUNT("903", "账号不存在"), RESULTCODE_LOGIN_DUE("904",
                                                "登录已过期"), RESULTCODE_NOT_TOKEN("905", "请求未携带凭证"), RESULTCODE_DISABLED(
                                                        "906", "密码重试次数超过五次,用户已被禁止登录"), RESULTCODE_PASSWORD_ERROR("202",
                                                                "密码输入有误,请重试"), RESULTCODE_REMINDER_MESSAGE("203",
                                                                        "密码过期剩余时间");

        private String value;
        private String name;

        ResultCode(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }

    /**
     * 源数据库类型
     */
    public enum DbType {

        // 数据库类型
        DB_TYPE_ORACLE("ORACLE", "ORACLE"), DB_TYPE_DB2("DB2", "DB2"), DB_TYPE_MYSQL("MYSQL",
                "MYSQL"), DB_TYPE_MSSQL("MSSQL", "MSSQL"), DB_TYPE_MONGO("MONGO", "MONGO");

        public static Set<String> keys = new HashSet<>();

        static {
            keys.add(DbType.DB_TYPE_ORACLE.name);
            keys.add(DbType.DB_TYPE_DB2.name);
            keys.add(DbType.DB_TYPE_MYSQL.name);
            keys.add(DbType.DB_TYPE_MSSQL.name);
            keys.add(DbType.DB_TYPE_MONGO.name);
        }

        private String value;
        private String name;

        DbType(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum MetaFlag {
        // 0为maintask启动fulldump，1表示删除，2表示UI启动fulldump，3表示后台fulldump结束 4重新同步 5fulldump出错
        // 6重新同步出错 7 其他错误 8 超时
        META_FLAG_MAINTASK_FULLDUMP("0", "maintask启动fulldump"), META_FLAG_DELETE("1", "表示删除"), META_FLAG_UI_FULLDUMP(
                "2", "UI启动fulldump"), METAFLAG_FULLDUMP_FINISH("3", "fulldump结束"), METAFLAG_RESYNCHRONIZATION("4",
                        "重新同步"), METAFLAG_FULLDUMP_ERRO("5", "fulldump出错"), METAFLAG_RESYNCHRONIZATION_ERRO("6",
                                "重新同步出错"), METAFLAG_OTHER_ERRO("7", "其他错误"), METAFLAG_TIME_OUT("8", "超时");

        public static Set<String> sync = new HashSet<String>();

        static {
            sync.add(Enums.MetaFlag.METAFLAG_FULLDUMP_FINISH.value());
            sync.add(Enums.MetaFlag.METAFLAG_RESYNCHRONIZATION.value());
            sync.add(Enums.MetaFlag.METAFLAG_FULLDUMP_ERRO.value());
            sync.add(Enums.MetaFlag.METAFLAG_RESYNCHRONIZATION_ERRO.value());
            sync.add(Enums.MetaFlag.METAFLAG_OTHER_ERRO.value());
            sync.add(Enums.MetaFlag.METAFLAG_TIME_OUT.value());
        }

        private String value;
        private String name;

        MetaFlag(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum DdlChanged {
        // 判断ddl是否发生变化
        DDLCHANGED_ENABLED("1", "表结构发生变化"), DDLCHANGED_DISABLED("0", "表结构未发生变化");

        private String value;
        private String name;

        DdlChanged(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum OperationLogType {
        // 操作日志类型
        OPERATIONLOGTYPE_CREATE("新增", "新增"), OPERATIONLOGTYPE_UPDATE("修改", "修改"), OPERATIONLOGTYPE_DELETE("删除",
                "删除"), OPERATIONLOGTYPE_SYNC("同步", "同步");

        public static Set<String> keys = new HashSet<>();

        static {
            // 操作日志类型结合
            keys.add(OperationLogType.OPERATIONLOGTYPE_CREATE.name);
            keys.add(OperationLogType.OPERATIONLOGTYPE_UPDATE.name);
            keys.add(OperationLogType.OPERATIONLOGTYPE_DELETE.name);
            keys.add(OperationLogType.OPERATIONLOGTYPE_SYNC.name);
        }

        private String value;
        private String name;

        OperationLogType(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String value() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
}
