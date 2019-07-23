package com.neusoft.study.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * <p>Title: com.neusoft.study.config</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/6/30 0030 18:21
 * Description: No Description
 *
 * 在实际使用中发现，对于controller返回@ResponseBody的请求，
 * filter中添加的header信息会丢失。对于这个问题spring已经给出解释，
 * 并建议实现ResponseBodyAdvice接口，并添加@ControllerAdvice。
 *
 */
@ControllerAdvice
public class ResponseHeaderAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    /**
     * 在所有的controller 返回 Body之前调用，进行header信息的添加
     * @param object
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object object, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest)serverHttpRequest;
        ServletServerHttpResponse serverResponse = (ServletServerHttpResponse)serverHttpResponse;
        if(serverRequest == null || serverResponse == null
                || serverRequest.getServletRequest() == null || serverResponse.getServletResponse() == null) {
            return object;
        }

        // 对于未添加跨域消息头的响应进行处理
        HttpServletRequest request = serverRequest.getServletRequest();
        HttpServletResponse response = serverResponse.getServletResponse();
        String originHeader = "Access-Control-Allow-Origin";
        if(!response.containsHeader(originHeader)) {
            String origin = request.getHeader("Origin");
            if(origin == null) {
                String referer = request.getHeader("Referer");
                if(referer != null){
                    origin = referer.substring(0, referer.indexOf("/", 7));
                }
            }
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        String allowHeaders = "Access-Control-Allow-Headers";
        if(!response.containsHeader(allowHeaders)){
            response.setHeader(allowHeaders, request.getHeader(allowHeaders));
        }

        String allowMethods = "Access-Control-Allow-Methods";
        if(!response.containsHeader(allowMethods)){
            response.setHeader(allowMethods, "GET,POST,OPTIONS,HEAD");
        }

        //这个很关键，要不然ajax调用时浏览器默认不会把这个token的头属性返给JS
        String exposeHeaders = "access-control-expose-headers";
        if(!response.containsHeader(exposeHeaders)){
            response.setHeader(exposeHeaders, "x-auth-token");
        }

        return object;
    }
}
