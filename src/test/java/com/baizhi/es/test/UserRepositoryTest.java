package com.baizhi.es.test;

import com.baizhi.entity.User;
import com.baizhi.es.dao.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author gaozhy
 * @date 2018/12/29.9:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-es.xml")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * 查所有
     */
    @Test
    public void testQueryAll(){
        Iterable<User> users = userRepository.findAll();
        for (User user : users) {
            System.out.println(user);
        }
    }

    /**
     * 查询所有 并根据年龄倒序排列
     */
    @Test
    public void testQueryBySort(){
        Iterable<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "age"));
        for (User user : users) {
            System.out.println(user);
        }
    }

    /**
     * 根据id查询
     */
    @Test
    public void testQueryById(){
        Optional<User> user = userRepository.findById("1");
        System.out.println(user.get());
    }

    /**
     * 新增或者修改数据
     */
    @Test
        public void testAdd(){
        userRepository.save(new User("13", "wb", "王八", 26, 10000D, new Date(), "河南省郑州市二七区德化街南路33号"));
        userRepository.save(new User("14", "wb", "王八", 26, 10000D, new Date(), "河南省郑州市二七区德化街南路33号"));
    }

    //==================================================================

    /**
     * 接口中声明方法查询：
     *    根据年龄区间查询数据 并根据年龄降序排列
     */
    @Test
    public void testQueryByRange(){
        List<User> users = userRepository.findByAgeBetweenOrderByAgeDesc(20, 28);
        users.forEach(user -> System.out.println(user));
    }

    /**
     * 接口中声明方法查询：
     *    查询真实姓名已“王”开头的数据
     *
     *    响应结果：
     *    User{id='6', name='wb', realname='王八', age=26, salary=10000.0, birthday=Sat Dec 29 14:38:39 CST 2018, address='河南省郑州市二七区德化街南路33号'}
          User{id='3', name='ww', realname='王五', age=25, salary=4300.0, birthday=Tue Mar 15 08:00:00 CST 2016, address='北京市海淀区中关村大街新中关商城2楼511室'}

     */
    @Test
    public void testQueryByPrefix(){
        List<User> users = userRepository.findByRealnameStartingWith("王");
        users.forEach(user -> System.out.println(user));
    }

    //==================================================================
    /**
     * 通过Query注解自定义查询表达式
     */
    @Test
    public void testQueryByNameLike(){
        List<User> users = userRepository.findByNameLike("zs");
        users.forEach(user -> System.out.println(user));
    }
}
