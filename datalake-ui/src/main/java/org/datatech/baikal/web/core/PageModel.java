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

package org.datatech.baikal.web.core;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * 数据分页模型类。<br>
 */
public class PageModel implements Serializable {
    private static final long serialVersionUID = 330410716100946538L;
    private int pageSize = 100;
    private Integer pageIndex = 0;
    private int prevPageIndex = 1;
    private int nextPageIndex = 1;
    private int pageCount = 0;
    private int pageFirstRowIndex = 1;
    private byte[] pageStartRowKey = null;
    private byte[] pageEndRowKey = null;
    private int queryTotalCount = 0;
    private long startTime = System.currentTimeMillis();
    private long endTime = System.currentTimeMillis();

    public PageModel() {
    }

    public PageModel(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取分页记录数量
     *
     * @return 分页记录数量
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置分页记录大小
     *
     * @param pageSize 分页记录大小
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取当前页序号
     *
     * @return 当前页序号
     */
    public Integer getPageIndex() {
        return pageIndex;
    }

    /**
     * 设置当前页序号
     *
     * @param pageIndex 当前页序号
     */
    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * 获取分页总数
     *
     * @return 分页总数
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * 设置分页总数
     *
     * @param pageCount 分页总数
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * 获取每页的第一行序号
     *
     * @return 每页的第一行序号
     */
    public int getPageFirstRowIndex() {
        this.pageFirstRowIndex = (this.getPageIndex() - 1) * this.getPageSize() + 1;
        return pageFirstRowIndex;
    }

    /**
     * 获取每页起始行键
     *
     * @return 起始行键
     */
    public byte[] getPageStartRowKey() {
        return pageStartRowKey;
    }

    /**
     * 设置每页起始行键
     *
     * @param pageStartRowKey 每页起始行键
     */
    public void setPageStartRowKey(byte[] pageStartRowKey) {
        this.pageStartRowKey = pageStartRowKey;
    }

    /**
     * 获取每页结束行键
     *
     * @return 每页结束行键
     */
    public byte[] getPageEndRowKey() {
        return pageEndRowKey;
    }

    /**
     * 设置每页结束行键
     *
     * @param pageEndRowKey 每页结束行键
     */
    public void setPageEndRowKey(byte[] pageEndRowKey) {
        this.pageEndRowKey = pageEndRowKey;
    }

    /**
     * 获取上一页序号
     *
     * @return 上一页序号
     */
    public int getPrevPageIndex() {
        if (this.getPageIndex() > 1) {
            this.prevPageIndex = this.getPageIndex() - 1;
        } else {
            this.prevPageIndex = 1;
        }
        return prevPageIndex;
    }

    /**
     * 获取下一页序号
     *
     * @return 下一页序号
     */
    public int getNextPageIndex() {
        this.nextPageIndex = this.getPageIndex() + 1;
        return nextPageIndex;
    }

    /**
     * 获取已检索总记录数
     *
     * @return 已检索总记录数
     */
    public int getQueryTotalCount() {
        return queryTotalCount;
    }

    /**
     * 获取已检索总记录数
     *
     * @param queryTotalCount 已检索总记录数
     */
    public void setQueryTotalCount(int queryTotalCount) {
        this.queryTotalCount = queryTotalCount;
    }

    /**
     * 初始化起始时间（毫秒）
     */
    public void initStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 初始化截止时间（毫秒）
     */
    public void initEndTime() {
        this.endTime = System.currentTimeMillis();
    }

    /**
     * 获取毫秒格式的耗时信息
     *
     * @return 毫秒格式的耗时信息
     */
    public String getTimeIntervalByMilli() {
        return String.valueOf(this.endTime - this.startTime) + "毫秒";
    }

    /**
     * 获取秒格式的耗时信息
     *
     * @return 秒格式的耗时信息
     */
    public String getTimeIntervalBySecond() {
        double interval = (this.endTime - this.startTime) / 1000.0;
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(interval) + "秒";
    }
}