package org.abs.common;

import java.util.ArrayList;
import java.util.List;

/**
 * ��ҳ�������������
 * @author ��ΰ
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
     * ��ҳ��Ϣ
     * 
     */
    public static final int DEFAULT_PAGE_SIZE = 20; // Ĭ�Ϸ�ҳ��С=20

    private int totalPage = 1; // ��ҳ����Ĭ��ֻ��һҳ

    private long totalRowCount = -1; // �ܼ�¼����Ĭ��-1��ʾδ���в�ѯ

    private int nowPage = 1; // ��ǰҳ��

    private int pageSize = DEFAULT_PAGE_SIZE; // ҳ��С

    private int startRow = 0; // ��ʼ����

    // private boolean enableLastPage = false; //ǰҳ�Ƿ����
    // private boolean enableNextPage = false; //��ҳ�Ƿ����
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
        // ���������ɵõ���ֵ
        this.setStartRow((nowPage - 1) * pageSize);
        this.setTotalPage((int) ((totalRow % pageSize) > 0 ? ((totalRow / pageSize) + 1) : (totalRow / pageSize)));
    }

    /**
     * ��һҳ
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
     * ��һҳ
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
     * ��ҳ��һ����¼��ţ�����Hibernate��
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
