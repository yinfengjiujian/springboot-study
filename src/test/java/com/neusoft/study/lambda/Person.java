package com.neusoft.study.lambda;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: com.neusoft.study.lambda</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/15 0015 18:40
 * Description: No Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    public String name;
    public int age;
    public String address;
    public String sex;

}
