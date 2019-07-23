package com.neusoft.study.common.cache;

import com.neusoft.study.redis.RedisServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title: com.neusoft.study.common.cache</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/6 0006 23:00
 * Description: No Description
 */
@Component
@Aspect
@Slf4j
public class CacheAsepct {

    @Autowired
    private RedisServiceUtil redisServiceUtil;

    //保存每一趟车是否在重构缓存的标志，模拟锁的实现,此HashMap是线程安全的
    ConcurrentHashMap<String, String> mapLock = new ConcurrentHashMap<>();
    /**
     * 定义一个方法用于切入加入注解的方法，进行AOP拦截增强
     *
     * @return
     */
    @Around("@annotation(com.neusoft.study.common.cache.AierCache)")
    public Object queryCache(ProceedingJoinPoint joinPoint) throws Throwable {

        //通过反射拿到此方法的对象method
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint
                .getTarget()
                .getClass()
                .getMethod(signature.getName(), signature.getMethod().getParameterTypes());

        //通过方法对象拿到注解Annotation在此方法上的类对象
        AierCache cacheAnnotation = method.getAnnotation(AierCache.class);
        //通过拿到的注解类对象，获取注解类的EL表达式
        String keyEL = cacheAnnotation.keyEL();

        //1、创建EL表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(keyEL);

        //2、设置解析上下文(这些占位符的----值---，都来自方法的----参数值---)
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        //获取方法参数值
        Object[] args = joinPoint.getArgs();

        //还需要获取运行过程中参数的真实名称
        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);

        //循环真实参数名称，通过设置的上下文解析参数和值的对应关系
        for (int i = 0; i < parameterNames.length; i++) {
            evaluationContext.setVariable(parameterNames[i],args[i]);
        }

        //3、解析
        String key = expression.getValue(evaluationContext).toString();

        //返回的对象值
        Object value = "";

        //1、先从缓存中取数据
        Object object = redisServiceUtil.get(key);
        if (object != null) {
            if (object instanceof String) {
                value = (String) object;
                log.info(Thread.currentThread().getName() + "缓存中取得数据：========>" + value);
                return value;
            }
        }

        /**需要重构缓存的标志，默认不需要==========Start**/
        boolean lock = false;

        //如果在mapLock 中不存在对应的车次key数据，那么需要重构缓存  细粒度锁的方式
        //mapLock.putIfAbsent 有两种结果，如果不存在key那么put进去并返回null，如果存在那么获取此key的value进行返回
        lock = mapLock.putIfAbsent(key, key + "") == null;
        try {
            //为true=不存在此趟车的key数据，那么有且仅有某一个线程拿到锁,查询数据库重构缓存
            if (lock) {
                //先从缓存中取数据
                object = redisServiceUtil.get(key);
                if (object != null) {
                    if (object instanceof String) {
                        value = (String) object;
                        log.info(Thread.currentThread().getName() + "缓存中取得数据：========>" + value);
                        return value;
                    }
                }

                //2、缓存中没有，则从数据库取
                value = (String) joinPoint.proceed();
                log.warn(Thread.currentThread().getName() + "从数据库中取得数据：=============>" + value);

                //3、塞到主缓存中 并设置数据120秒过期时间，一致性
                redisServiceUtil.set(key, value, 120l);

                //TODO  4、塞到备份缓存中，并且不设置key过期时间，备份缓存永久有效

                //没有拿到锁的线程，全部走此逻辑，进行降级返回
            } else {
                //TODO 1、从备份缓存中获取数据进行返回

                //2、返回固定值，进行降级处理
                value = "0";
                log.info(Thread.currentThread().getName() + "缓存降级，返回固定值：" + value);
            }
        } finally {
            //释放模拟伪锁
            if (lock) {
                mapLock.remove(key);
            }
        }
        return value;
    }
}
