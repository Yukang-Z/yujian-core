package com.yujian.common.page;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 统一分页返回对象
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 当前页数据
     */
    private List<T> list;

    /**
     * 空分页结果
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param <T>      数据类型
     * @return 空分页
     */
    public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
        return of(pageNum, pageSize, 0L, Collections.<T>emptyList());
    }

    /**
     * 构建分页结果
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param total    总记录数
     * @param list     当前页数据
     * @param <T>      数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(total == null ? 0L : total);
        result.setList(list == null ? Collections.<T>emptyList() : list);
        int size = pageSize == null || pageSize <= 0 ? 1 : pageSize;
        long pages = result.getTotal() == 0 ? 0 : (result.getTotal() + size - 1) / size;
        result.setPages((int) pages);
        return result;
    }
}
