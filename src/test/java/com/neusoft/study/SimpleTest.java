package com.neusoft.study;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.study.user.condition.UserCondition;
import com.neusoft.study.user.dao.UserMapper;
import com.neusoft.study.user.entity.User;
import com.neusoft.study.user.entity.UserAndExtend;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: com.neusoft.study</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/7/6 0006 22:25
 * Description: No Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleTest {

    @Autowired(required = false)
    private UserMapper userMapper;


    @Test
    public void selectTest(){
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(2,userList.size());
        userList.forEach(System.out::println);

    }

    @Test
    public void selectJoin(){
        UserCondition userCondition = new UserCondition();
//        userCondition.setName("燕子");
//        userCondition.setUserid("11476723287300587");
        List<UserAndExtend> persionBase = userMapper.getPersionBase(userCondition);
        System.out.println(persionBase);
    }

    /**
     * 分页查询
     */
    @Test
    public void selectPageJoin(){
        //页面接收过来的参数条件
        UserCondition userCondition = new UserCondition();
        userCondition.setName("燕子");
//        userCondition.setUserid("11476723287300587");

        //组装页面传递过来的查询条件，进行查询
        QueryWrapper<UserAndExtend> queryWrapper = new QueryWrapper<UserAndExtend>();
        queryWrapper.eq("nickname",userCondition.getName());
        //需要分页查询的page
        Page<UserAndExtend> page = new Page<>(1,2);

        IPage<UserAndExtend> userPage = userMapper.getUserPage(page, queryWrapper);
        System.out.println("总页数：" + page.getPages());
        System.out.println("总记录数：" + page.getTotal());
        List<UserAndExtend> records = page.getRecords();
        records.forEach(System.out::println);
    }

    @Test
    public void insert(){
        User user = new User();
        user.setAccount("yinhai");
        user.setAge(32);
        user.setPassword("fijeoferidi");
        user.setUsername("段澹雅");
        userMapper.insert(user);
    }

    @Test
    public void selectByid(){
        Map<String,Object> map = new HashMap<>();
        map.put("account","yinhai");
//        userMapper.selectByMap()
        User user = userMapper.selectById(11476723287300587L);

        List<User> users = userMapper.selectByMap(map);
        System.out.println(users);

        System.out.println(user);
    }

//    @Test
//    public void selectByWrapper(String name,String email){
//        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();
//        //姓名 包含  yihai  并且 年龄小于 40 的写法
//        userQueryWrapper.like("username","yihai").lt("age",40);
//
//        //名字中包含  “雨”  并且年龄大于等于20且小于等于40 并且邮箱不为空
//        userQueryWrapper.like("username","雨").between("age",20,40).isNotNull("email");
//
//
//        //名字为 “王” 姓开头或者年龄大于等于25，按照年龄降序排列，年龄相同的按照创建时间升序排列
//        userQueryWrapper.likeRight("username","王").or()
//                .ge("age",25).orderByDesc("age").orderByAsc("create_date");
//
//        // 创建日期为2019年2月14日 并且直属上级的名字为王姓
//        userQueryWrapper.apply("date_format(create_date,'%Y-%m-%d')={0}","2019-02-14")
//                .inSql("manager_id","select id from user where name like '王%' ");
//
//        //  name like '王%' and (age < 40 or email is not null)
//        userQueryWrapper.likeRight("name","王")
//                .and(wq -> wq.lt("age",40).or().isNotNull("email"));
//
//        // (age < 40 or email is not null) and name like '王%'
//        userQueryWrapper.nested(wq -> wq.lt("age",40).or().isNotNull("email"))
//                .likeRight("name","王");
//
//        if(StringUtils.isNotEmpty(name)){
//            userQueryWrapper.like("name",name);
//        }
//
//        if(StringUtils.isNotEmpty(email)){
//            userQueryWrapper.like("email",email);
//        }
//
//        userQueryWrapper.like(StringUtils.isNotEmpty(name),"name",name);
//        userQueryWrapper.like(StringUtils.isNotEmpty(email),"email",email);
//    }

    @Test
    public void selectMy(){
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();
        userQueryWrapper.likeRight("username","段");

        List<User> userList = userMapper.selectAll(userQueryWrapper);

        userList.forEach(System.out::println);
    }

    @Test
    public void selectPage(){
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();

        userQueryWrapper.eq("account","yinhai");

        Page<User> userPage = new Page<>(2,2);

        IPage<User> userIPage = userMapper.selectPage(userPage, userQueryWrapper);

        System.out.println("总页数：" + userIPage.getPages());
        System.out.println("总记录数：" + userIPage.getTotal());

        List<User> userList = userIPage.getRecords();

        userList.forEach(System.out::println);

    }
}
