package com.think.web.core.interceptor;

import com.think.common.util.StringUtil;
import com.think.core.security.ThinkToken;
import com.think.data.Manager;
import com.think.web.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
public class ThinkParentInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            ThinkToken token = null;
            Optional<ThinkToken> thinkTokenOptional = WebUtil.getToken();
            if (thinkTokenOptional.isPresent()) {
                token = thinkTokenOptional.get();
            }
            if (token != null) {
                if (StringUtil.isNotEmpty(token.getCurrentRegion())) {
                    Manager.beginDataSrv(token.getCurrentRegion());
                } else {
                    Manager.beginDataSrv();
                }
            } else {
                Manager.beginDataSrv();
            }
        }catch (Exception e){}
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Manager.endDataSrv();
    }

}
