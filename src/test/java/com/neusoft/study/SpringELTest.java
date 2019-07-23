package com.neusoft.study;

import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * <p>Title: com.neusoft.study</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/7 0007 9:38
 * Description: SpringEl 表达式测试
 */
public class SpringELTest {

    public static void main(String[] args) {

    }


    @Test
    public void test1(){
        //JAVA占位符
        String template = "%s : 正在测试";
        String result = String.format(template,"我是duanml");
        System.out.println(result);
    }

    @Test
    public void SPELTest(){
        //如下的EL表达式(可以简单的理解为JAVA占位符)
        String ELDemo = "'hello :'  + #userid + ',' + #ok";

        //1、创建解析器
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(ELDemo);

        //2、设置解析上下文(有哪些占位符，以及每种占位符的值)
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("userid","尹海燕");
        evaluationContext.setVariable("ok","段美林");

        //3、解析
        String result = expression.getValue(evaluationContext).toString();
        System.out.println(result);


    }
}
