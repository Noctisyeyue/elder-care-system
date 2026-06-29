package com.eldercare.system.util;

import java.util.List;

/**
 * 通用分页响应，所有分页查询接口都用这个格式返回
 *
 * @param <T> 数据记录的类型
 */
public class PageResult<T> {

    /** 当前页数据 */
    private List<T> records;

    /** 总记录数 */
    private Long total;

    /** 当前页码 */
    private Long page;

    /** 每页大小 */
    private Long size;

    /**
     * 创建空分页结果
     */
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

    /**
     * 获取当前页数据
     *
     * @return 当前页数据
     */
    public List<T> getRecords() { return records; }

    /**
     * 设置当前页数据
     *
     * @param records 当前页数据
     */
    public void setRecords(List<T> records) { this.records = records; }

    /**
     * 获取总记录数
     *
     * @return 总记录数
     */
    public Long getTotal() { return total; }

    /**
     * 设置总记录数
     *
     * @param total 总记录数
     */
    public void setTotal(Long total) { this.total = total; }

    /**
     * 获取当前页码
     *
     * @return 当前页码
     */
    public Long getPage() { return page; }

    /**
     * 设置当前页码
     *
     * @param page 当前页码
     */
    public void setPage(Long page) { this.page = page; }

    /**
     * 获取每页大小
     *
     * @return 每页大小
     */
    public Long getSize() { return size; }

    /**
     * 设置每页大小
     *
     * @param size 每页大小
     */
    public void setSize(Long size) { this.size = size; }
}
