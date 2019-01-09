package com.baizhi.es.dao;

import com.baizhi.entity.User;

import java.util.List;
import java.util.Map;

/**
 * @author gaozhy
 * @date 2019/1/1.23:10
 */
public interface CustomUserRepository {

    public List<User> findByPageable(int nowPage,int pageSize);

    public List<User> findByFieldDesc(String field);

    public List<User> findByRealNameLikeAndHighLight(String realName);

    public List<User> findByNameWithTermFilter(String ...terms);

    public List<User> findByAgeWithRangeFilter(int start,int end);

    public Map findByNameStartingWithAndAggregations(String prefixName);

    /**
     * 嵌套查询：
     *
     * 先按年龄直方图（桶聚合）统计
     * 然后再统计区间内员工的最高工资（度量聚合）
     */
    public Map aggregationsWithHistogramAndMax();

    /**
     * 日期直方图（桶聚合）
     */
    public Map aggregationsWithDateHistogram();

}
