package com.baizhi.es.test;

import com.baizhi.entity.User;
import com.baizhi.es.dao.CustomUserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * @author gaozhy
 * @date 2019/1/1.23:26
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-es.xml")
public class CustomUserRepositoryTest {

    @Autowired
    private CustomUserRepository repository;

    @Test
    public void testQueryByPage(){
        List<User> users = repository.findByPageable(0, 2);
        users.forEach(user -> {
            System.out.println(user);
        });
    }
    @Test
    public void testQueryBySort(){
        List<User> users = repository.findByFieldDesc("_id");
        users.forEach(user -> {
            System.out.println(user);
        });
    }
    @Test
    public void testQueryByHighLight(){
        List<User> users = repository.findByRealNameLikeAndHighLight("王八");
        users.forEach(user -> {
            System.out.println(user);
        });
    }

    @Test
    public void testQueryByNameWithTermFilter(){
        List<User> users = repository.findByNameWithTermFilter("zs","ls");
        users.forEach(user -> {
            System.out.println(user);
        });
    }

    @Test
    public void testQueryByAgeWithRangeFilter(){
        List<User> users = repository.findByAgeWithRangeFilter(21,30);
        users.forEach(user -> {
            System.out.println(user);
        });
    }

    @Test
    public void testQueryByNameStartingWithAndAggregations(){
        Map map = repository.findByNameStartingWithAndAggregations("z");
        System.out.println(map.get("result"));
    }

    @Test
    public void testAggregationsWithHistogramAndMax(){
        Map map = repository.aggregationsWithHistogramAndMax();
        System.out.println(map.get("result"));
    }

    @Test
    public void testAggregationsWithDateHistogram(){
        Map map = repository.aggregationsWithDateHistogram();
        System.out.println(map.get("result"));
    }
}
