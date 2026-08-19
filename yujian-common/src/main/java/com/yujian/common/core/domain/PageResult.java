package com.yujian.common.core.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 统一分页返回结构，作为列表查询接口的回参（总条数、分页参数及当前页数据）。
 *
 * @param <T> 当前页记录类型
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 符合条件的总记录数（全量计数，非当前页条数） */
    private long total;

    /** 当前页码，从 1 开始 */
    private long pageNum;

    /** 每页条数（pageSize） */
    private long pageSize;

    /** 当前页数据列表，无数据时为空列表 */
    private List<T> records;

    /**
     * 从 MyBatis-Plus IPage 转换
     *
     * @param page 分页对象
     * @param <T>  记录类型
     * @return PageResult
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<T>();
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setRecords(page.getRecords() == null ? Collections.<T>emptyList() : page.getRecords());
        return result;
    }

    /**
     * 手动组装分页结果
     *
     * @param total    总记录数
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @param records  数据列表
     * @param <T>      记录类型
     * @return PageResult
     */
    public static <T> PageResult<T> of(long total, long pageNum, long pageSize, List<T> records) {
        PageResult<T> result = new PageResult<T>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setRecords(records == null ? Collections.<T>emptyList() : records);
        return result;
    }
}
