package com.eldercare.system.util;

import java.util.List;

/**
 * 通用分页响应，所有分页查询接口都用这个格式返回
 *
 * @param <T> 数据记录的类型
 */
public class PageResult<T> {

    private List<T> records;
    private Long total;
    private Long page;
    private Long size;

    public PageResult() {}

    /**
     * 快速构造分页结果
     *
     * @param records 当前页数据
     * @param total   总记录数
     * @param page    当前页码
     * @param size    每页大小
     */
    public PageResult(List<T> records, Long total, Long page, Long size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    /**
     * 静态工厂方法
     *
     * @param records 当前页数据
     * @param total   总记录数
     * @param page    当前页码
     * @param size    每页大小
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long page, Long size) {
        return new PageResult<>(records, total, page, size);
    }

    // ===== getter / setter =====

    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public Long getPage() { return page; }
    public void setPage(Long page) { this.page = page; }
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
}
