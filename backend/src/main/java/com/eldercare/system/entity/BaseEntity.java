package com.eldercare.system.entity;

/**
 * 实体基类，所有数据库实体继承它，自动获得通用审计字段
 */
public class BaseEntity {

    /** 0=正常 1=已删除 */
    private String delFlag;
    /** 创建人 */
    private String createBy;
    /** 创建时间 */
    private String createTime;
    /** 最后修改人 */
    private String updateBy;
    /** 最后修改时间 */
    private String updateTime;

    // ===== getter / setter =====

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
}
