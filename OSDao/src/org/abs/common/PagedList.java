package org.abs.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页组件基础数据类
 * @author 苟伟
 *
 */

public class PagedList<E> {

    private List<E> dataList = new ArrayList<E>(0);

    public List<E> getDataList() {
        if (dataList == null) {
            return new ArrayList<E>(0);
        }
        return dataList;
    }

    public void setDateList(List<E> dataList) {
        this.dataList = dataList;
    }

    /**
     * 分页信息
     * 
     */
    public static final int DEFAULT_PAGE_SIZE = 20; // 默认分页大小=20

    private int totalPage = 1; // 总页数，默认只有一页

    private long totalRowCount = -1; // 总记录数，默认-1表示未进行查询

    private int nowPage = 1; // 当前页数

    private int pageSize = DEFAULT_PAGE_SIZE; // 页大小

    private int startRow = 0; // 开始行数

    // private boolean enableLastPage = false; //前页是否可用
    // private boolean enableNextPage = false; //下页是否可用
    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalRowCount(long totalRow) {
        this.totalRowCount = totalRow;
    }

    public long getTotalRowCount() {
        return totalRowCount;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public PagedList(List<E> pageDataList, int nowPage, int pageSize, long totalRow) {
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        this.setDateList(pageDataList);
        this.setNowPage(nowPage);
        this.setPageSize(pageSize);
        this.setTotalRowCount(totalRow);
        // 计算其他可得到的值
        this.setStartRow((nowPage - 1) * pageSize);
        this.setTotalPage((int) ((totalRow % pageSize) > 0 ? ((totalRow / pageSize) + 1) : (totalRow / pageSize)));
    }

    /**
     * 上一页
     * 
     * @return
     */
    public int getLastPage() {
        if (nowPage <= 1) {
            return 1;
        } else {
            return nowPage - 1;
        }
    }

    /**
     * 下一页
     * 
     * @return
     */
    public int getNextPage() {
        if (nowPage >= totalPage) {
            return totalPage;
        } else {
            return nowPage + 1;
        }
    }

    public int getStart() {
        return (nowPage - 1) * pageSize;
    }

    public int getEnd() {
        return nowPage * pageSize;
    }

    /**
     * 本页第一条记录序号（用于Hibernate）
     * 
     * @return
     */
    public int getPageStartRow() {
        return pageSize * (nowPage - 1);
    }

    public String toJsonString() {
        List<E> data = getDataList();
        StringBuilder tmp = new StringBuilder("{");
        tmp.append("totalRowCount: ").append(totalRowCount)
                .append(",nowPage: ").append(nowPage).append(",pageSize: ")
                .append(pageSize).append(",totalPage: ").append(totalPage)
                .append(",data: ").append(data).append("}");
        return tmp.toString();
    }
}
