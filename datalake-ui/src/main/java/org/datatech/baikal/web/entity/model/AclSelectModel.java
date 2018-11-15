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

package org.datatech.baikal.web.entity.model;

public class AclSelectModel {
    private AclSelectListModel cipherIndex;
    private AclSelectListModel mask;
    private AclSelectListModel columnAcl;
    private AclSelectListModel rowAcl;

    public AclSelectListModel getCipherIndex() {
        return cipherIndex;
    }

    public void setCipherIndex(AclSelectListModel cipherIndex) {
        this.cipherIndex = cipherIndex;
    }

    public AclSelectListModel getMask() {
        return mask;
    }

    public void setMask(AclSelectListModel mask) {
        this.mask = mask;
    }

    public AclSelectListModel getColumnAcl() {
        return columnAcl;
    }

    public void setColumnAcl(AclSelectListModel columnAcl) {
        this.columnAcl = columnAcl;
    }

    public AclSelectListModel getRowAcl() {
        return rowAcl;
    }

    public void setRowAcl(AclSelectListModel rowAcl) {
        this.rowAcl = rowAcl;
    }
}
