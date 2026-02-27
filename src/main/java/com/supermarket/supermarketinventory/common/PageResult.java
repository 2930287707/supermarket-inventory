package com.supermarket.supermarketinventory.common;
import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private long total;    // 总条数
    private int pageNum;   // 当前页
    private int pageSize;  // 每页大小
    private List<T> list;  // 数据列表

    public PageResult(long total, int pageNum, int pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
    }
}
