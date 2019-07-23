package com.neusoft.study.common.shiro.filters;

import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * <p>Title: com.neusoft.study.common.shiro.filters</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/30 0030 10:50
 * Description: No Description
 */
public class AnyRolesAuthorizationFilter extends AuthorizationFilter {

    @Override
    protected void postHandle(ServletRequest request, ServletResponse response){
        request.setAttribute("anyRolesAuthFilter.FILTERED", true);
    }

    /**
     * 所以自己实现一个role filter，只要任何一个角色符合条件就通过，只需要重写AuthorizationFilter中两个方法就可以了：
     * @param servletRequest
     * @param servletResponse
     * @param mappedValue
     * @return
     * @throws Exception
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue) throws Exception {
        Boolean afterFiltered = (Boolean)(servletRequest.getAttribute("anyRolesAuthFilter.FILTERED"));
        if( BooleanUtils.isTrue(afterFiltered)){
            return true;
        }

        Subject subject = getSubject(servletRequest, servletResponse);
        String[] rolesArray = (String[]) mappedValue;
        if (rolesArray == null || rolesArray.length == 0) { //没有角色限制，有权限访问
            return true;
        }
        for (String role : rolesArray) {
            //若当前用户是rolesArray中的任何一个，则有权限访问
            if (subject.hasRole(role)){
                return true;
            }
        }
        return false;
    }

    /**
     * 权限校验失败，错误处理
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
        return false;
    }

}
