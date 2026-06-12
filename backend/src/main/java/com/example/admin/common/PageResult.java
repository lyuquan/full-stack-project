package com.example.admin.common;

import java.util.List;

/**
 * Common page response object.
 *
 * It returns both current page records and pagination metadata to the frontend.
 */
public class PageResult<T> {

    /**
     * Current page records.
     */
    private List<T> records;

    /**
     * Total record count that matches the current query.
     */
    private Long total;

    /**
     * Current page number, starts from 1 for frontend users.
     */
    private Integer page;

    /**
     * Page size, means how many records are returned per page.
     */
    private Integer size;

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Integer page, Integer size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
