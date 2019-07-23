package com.neusoft.study.lambda;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: com.neusoft.study.lambda</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/15 0015 18:39
 * Description: No Description
 */
@Slf4j
public class StuydLambda {

    @Test
    public void LambdaTest() {

        List<Person> personList = new ArrayList<>();

        personList.add(new Person("段美林", 32, "湖南衡阳", "男"));

        personList.add(new Person("尹海燕", 31, "湖南永州", "女"));

        personList.add(new Person("段皓睿", 3, "湖南衡阳", "男"));

        personList.add(new Person("段澹雅", 5, "湖南长沙", "女"));

        personList.add(new Person("段成发", 64, "广东东莞", "男"));

        personList.add(new Person("陈贵云", 56, "广东东莞", "女"));

        //遍历list
        personList.stream().forEach(person -> {
            log.info(person.toString());
        });


        //收集List对象中的某一列的值并返回一个新的list对象
        List<String> nameList = personList.stream().map(person -> {
            return person.getName();
        }).collect(Collectors.toList());
        log.info(nameList.toString());


        //对list对象中的某一列进行排序
        personList.sort(Comparator.comparing(Person::getAge).reversed());  //降序
        personList.sort(Comparator.comparing(Person::getAge));  //升序

        //list转为Map，并输出Map相关值
        Map<String, Person> personMap = personList.stream().collect(Collectors.toMap(Person::getName, person -> person));
        personMap.forEach((k, v) -> {
            if (k.equals("段美林")) {
                log.info(v.toString());
            }
        });

        //将List分组：List里面的对象元素，以某个属性来分组
        Map<String, List<Person>> listMap = personList.stream()
                .collect(Collectors.groupingBy(Person::getSex));
        listMap.forEach((k, v) -> {
            log.info(v.toString());
        });

        //过滤：从集合中过滤出来符合条件的元素
        List<Person> people = personList.stream().filter(person -> {
                return person.getName().equals("段美林");
        }).collect(Collectors.toList());
        people.stream().forEach(person -> {
            log.info(person.getName());
        });

        //求和：将集合中的数据按照某个属性求和
        int sum = personList.stream().mapToInt(Person::getAge).sum();
        System.out.println(sum);
    }

}
