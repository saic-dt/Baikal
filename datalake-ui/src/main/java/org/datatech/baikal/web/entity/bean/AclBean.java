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

package org.datatech.baikal.web.entity.bean;

import java.util.ArrayList;
import java.util.List;

public class AclBean {
    private boolean encrypt;
    /**
     * 创建默认的配置对象
     */
    private Integer cipherIndex;
    private List<String> columnAcl;
    private List<RowAclBean> rowAcl;
    private List<String> mask;
    private Integer columnType;
    private String columnTypeName;
    private Integer precision;
    private Integer scale;
    private String columnTypeFullName;

    public AclBean() {
    }

    private AclBean(boolean encrypt, Integer cipherIndex, List<String> columnAcl, List<RowAclBean> rowAcl,
            List<String> mask, AclBean aclBean) {
        super();
        this.encrypt = encrypt;
        this.cipherIndex = cipherIndex;
        this.columnAcl = columnAcl;
        this.rowAcl = rowAcl;
        this.mask = mask;
        this.columnType = aclBean.getColumnType();
        this.columnTypeName = aclBean.getColumnTypeName();
        this.precision = aclBean.getPrecision();
        this.scale = aclBean.getScale();
        this.columnTypeFullName = aclBean.getColumnTypeFullName();
    }

    /**
     * 创建默认的权限对象
     *
     * @param aclBean 权限对象
     *                encrypt 是否加密
     *                cipherIndex 加密方式下标
     *                columnAcl 列权限
     *                rowAcl 行权限
     *                mask  脱敏
     *                columnType 数据库字段的columnType
     *                columnTypeName 数据库字段的 columnTypeName
     *                precision  数据库字段的长度大小
     *                scale     数据库字段的小数点
     *                columnTypeFullName  数据库列明+数据库长度+精度 例如char(10)
     * @return 返回默认的权限对象
     */
    final public static AclBean createDefault(AclBean aclBean) {
        return new AclBean(true, 1, new ArrayList<>(0), new ArrayList<>(0), new ArrayList<>(0), aclBean);
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public Integer getCipherIndex() {
        return cipherIndex;
    }

    public void setCipherIndex(Integer cipherIndex) {
        this.cipherIndex = cipherIndex;
    }

    public List<String> getColumnAcl() {
        return columnAcl;
    }

    public void setColumnAcl(List<String> columnAcl) {
        this.columnAcl = columnAcl;
    }

    public List<RowAclBean> getRowAcl() {
        return rowAcl;
    }

    public void setRowAcl(List<RowAclBean> rowAcl) {
        this.rowAcl = rowAcl;
    }

    public List<String> getMask() {
        return mask;
    }

    public void setMask(List<String> mask) {
        this.mask = mask;
    }

    public Integer getColumnType() {
        return columnType;
    }

    public void setColumnType(Integer columnType) {
        this.columnType = columnType;
    }

    public String getColumnTypeName() {
        return columnTypeName;
    }

    public void setColumnTypeName(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getColumnTypeFullName() {
        return columnTypeFullName;
    }

    public void setColumnTypeFullName(String columnTypeFullName) {
        this.columnTypeFullName = columnTypeFullName;
    }
}
