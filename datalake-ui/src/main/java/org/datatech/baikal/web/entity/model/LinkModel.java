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

public class LinkModel {

    private String rowkey;
    private String port;
    private String rmtIp;

    public LinkModel() {
        super();
    }

    public LinkModel(byte[] rowkey, byte[] port, byte[] rmtIp) {
        super();
        this.rowkey = new String(rowkey);
        this.port = new String(port);
        this.rmtIp = new String(rmtIp);
    }

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getRmtIp() {
        return rmtIp;
    }

    public void setRmtIp(String rmtIp) {
        this.rmtIp = rmtIp;
    }
}
