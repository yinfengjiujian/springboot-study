package com.neusoft.study.common.shiro.filters;

import com.alibaba.fastjson.JSONObject;
import com.neusoft.study.common.exception.BusinessException;
import com.neusoft.study.common.response.ResponseCodeEnum;
import com.neusoft.study.common.response.ResponseResultUtil;
import com.neusoft.study.common.shiro.JWTToken;
import com.neusoft.study.common.shiro.utils.JwtUtils;
import com.neusoft.study.user.entity.UserInfo;
import com.neusoft.study.user.service.impl.UserJwtServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * <p>Title: com.neusoft.study.common.shiro.filters</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/30 0030 10:53
 * Description: No Description
 */
@Slf4j
public class JwtAuthFilter extends AuthenticatingFilter {

    private static final int tokenRefreshInterval = 300;

    private UserJwtServiceImpl userJwtService;

    /**
     * 设置登录的地址为：/login
     *
     * @param
     */
    public JwtAuthFilter(UserJwtServiceImpl userJwtService) {
        this.userJwtService = userJwtService;
        this.setLoginUrl("/login");
    }

    /***
     * =====跨域支持
     * 对于前后端分离的项目，一般都需要跨域访问，这里需要做两件事，
     * 1、一个是在JwtFilter的postHandle中在头上加上跨域支持的选项（理论上应该重新定义一个Filter的，图省事就让它多干点吧😓）
     * 2、编写一个全局控制的Controller切面类，ResponseHeaderAdvice
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        //对于OPTION请求做拦截，不做token校验
        /**
         * 简言之，options请求是用于请求服务器对于某些接口等资源的支持情况的，包括各种请求方法、头部的支持情况，仅作查询使用
         *我们可以把浏览器自主发起的行为称之为“浏览器级行为”。之所以说options是一种浏览器级行为，是因为在某些情况下，
         * 普通的get或者post请求回首先自动发起一次options请求，当options请求成功返回后，真正的ajax请求才会再次发起
         * **/
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name()))
            return false;

        return super.preHandle(request, response);
    }

    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) {
        this.fillCorsHeader(WebUtils.toHttp(request), WebUtils.toHttp(response));
        request.setAttribute("jwtShiroFilter.FILTERED", true);
    }


    /**
     * 父类会在请求进入拦截器后调用该方法，返回true则继续，返回false则会调用onAccessDenied()。这里在不通过时，还调用了isPermissive()方法，我们后面解释。
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {

        //是否是  "/login"  请求，如果是的，直接返回不做拦截
        if (this.isLoginRequest(request, response)) {
            return true;
        }

        Boolean afterFiltered = (Boolean) (request.getAttribute("jwtShiroFilter.FILTERED"));

        if (BooleanUtils.isTrue(afterFiltered)) {
            return true;
        }
        boolean allowed = false;

        try {
            allowed = executeLogin(request, response);
        } catch (IllegalStateException e) { //not found any token
            log.error("Not found any token");
            responseError(request,response,ResponseCodeEnum.NOT_FOUND_TOKEN);
        } catch (BusinessException e) {
            log.error(e.getMessage());
            responseError(request,response,e);
        } catch (Exception e) {
            log.error("Error occurs when login", e);
            responseError(request,response,ResponseCodeEnum.FAILD_ACTION);
        }

        /**关于permissive
         * 就是这么简单直接，字符串匹配。当然这里也可以重写这个方法插入更复杂的逻辑。
         * 这么做的目的是什么呢？因为有时候我们对待请求，并不都是非黑即白，比如登出操作，
         * 如果用户带的token是正确的，我们会将保存的用户信息清除；如果带的token是错的，也没关系，大不了不干啥，
         * 没必要返回错误给用户。还有一个典型的案例，比如我们阅读博客，匿名用户也是可以看的。只是如果是登录用户，
         * 我们会显示额外的东西，比如是不是点过赞等。所以认证这里的逻辑就是token是对的，我会给把人认出来；是错的，
         * 我也直接放过，留给controller来决定怎么区别对待
         *
         *
         * */
        return allowed || super.isPermissive(mappedValue);
    }

    /**
     * 这里重写了父类的方法，使用我们自己定义的Token类，提交给shiro。这个方法返回null的话会直接抛出异常，进入isAccessAllowed（）的异常处理逻辑。
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
        //从请求的head获取token字符串
        String jwtToken = getAuthzHeader(servletRequest);
        try {
            //拿到的token不为空，且token没有过期
            if (StringUtils.isNotBlank(jwtToken) && !JwtUtils.isTokenExpired(jwtToken)) {
                return new JWTToken(jwtToken);
            }
        } catch (Exception e) {
            throw  new BusinessException(ResponseCodeEnum.TOKEN_ERROR);
        }
        //token过期，那么抛出业务异常
        if (StringUtils.isNotBlank(jwtToken) && JwtUtils.isTokenExpired(jwtToken)){
            throw  new BusinessException(ResponseCodeEnum.TOKEN_EXPIRED);
        }
        return null;
    }

    /**
     * 如果这个Filter在之前isAccessAllowed（）方法中返回false,则会进入这个方法。我们这里直接返回错误的response
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse httpResponse = WebUtils.toHttp(servletResponse);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setStatus(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION);
        fillCorsHeader(WebUtils.toHttp(servletRequest), httpResponse);
        responseError(servletRequest,servletResponse,ResponseCodeEnum.TOKEN_ERROR);
        return false;
    }

    /**
     * 如果Shiro Login认证成功，会进入该方法，等同于用户名密码登录成功，我们这里还判断了是否要刷新Token
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        String newToken = null;
        if (token instanceof JWTToken) {
            JWTToken jwtToken = (JWTToken) token;
            UserInfo userInfo = (UserInfo) subject.getPrincipal();
            boolean shouldRefresh = shouldTokenRefresh(JwtUtils.getIssuedAt(jwtToken.getToken()));
            if (shouldRefresh) {
                newToken = userJwtService.generateJwtToken(userInfo.getAccount());
            }
        }
        if (StringUtils.isNotBlank(newToken)) {
            httpResponse.setHeader("x-auth-token", newToken);
        }
        return true;
    }

    /**
     * 如果调用shiro的login认证失败，会回调这个方法，这里我们什么都不做，因为逻辑放到了onAccessDenied（）中。
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        log.error("Validate token fail, token:{}, error:{}", token.toString(), e.getMessage());
        return false;
    }

    protected String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String header = httpRequest.getHeader("x-auth-token");
        return StringUtils.removeStart(header, "Bearer ");
    }


    /**
     * 前面的Filter里面还有一个逻辑（是不是太多了😓），就是如果用户这次的token校验通过后，
     * 我们还会顺便看看token要不要刷新，如果需要刷新则将新的token放到header里面。
     * 这样做的目的是防止token丢了之后，别人可以拿着一直用。我们这里是固定时间刷新。
     * 安全性要求更高的系统可能每次请求都要求刷新，或者是每次POST，PUT等修改数据的请求后必须刷新。判断逻辑如下
     *
     * @param issueAt
     * @return
     */
    protected boolean shouldTokenRefresh(Date issueAt) {
        //上一次token的生成时间
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        //当前系统时间，减去需要刷新token的配置时间参数，得到一个新的时间
        LocalDateTime dateTime = LocalDateTime.now().minusSeconds(tokenRefreshInterval);
        //新的时间如果比生成token的时间要大，那么需要刷新token
        return dateTime.isAfter(issueTime);
    }

    protected void fillCorsHeader(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,HEAD");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
    }

    /**
     * 非法url返回身份错误信息
     */
    private void responseError(ServletRequest request, ServletResponse response,ResponseCodeEnum responseCodeEnum) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("utf-8");
            out = response.getWriter();
            response.setContentType("application/json; charset=utf-8");
            out.print(JSONObject.toJSONString(new ResponseResultUtil<>().errorEnum(responseCodeEnum)));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 非法url返回身份错误信息
     */
    private void responseError(ServletRequest request, ServletResponse response,BusinessException be) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("utf-8");
            out = response.getWriter();
            response.setContentType("application/json; charset=utf-8");
            out.print(JSONObject.toJSONString(new ResponseResultUtil<>().error(be.getCode(),be.getMessage())));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
