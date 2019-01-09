package com.baizhi.es.dao;

import com.baizhi.entity.User;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 基础操作的es repository接口（定义的有通用的增删改查方法）
 *
 * @author gaozhy
 * @date 2018/12/29.9:26
 */
public interface UserRepository extends ElasticsearchRepository<User,String> {

    /**
     * 根据年龄区间查询数据 并根据年龄降序排列
     */
    public List<User> findByAgeBetweenOrderByAgeDesc(int start,int end);

    /**
     * 查询真实姓名已“王”开头的数据
     */
    public List<User> findByRealnameStartingWith(String startStr);

    /**
     * 通过Query注解自定义查询表达式
     */
    @Query("{\"bool\" : {\"must\" : {\"fuzzy\" : {\"name\" : \"?0\"}}}}")
    public List<User> findByNameLike(String name);
}
