package cn.zzzzbw.tiny.filemanager.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 管理后台 拦截器
 *
 * @author zbw
 * @since 2017/10/11 14:10
 */
@Slf4j
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Autowired
    private SecurityService securityService;

    private static final List<String> IGNORE_URIS = Arrays.asList("/login}");


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();

        //登录拦截
        if (isAuthUrl(url)) {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            boolean valid = securityService.valid(authorization);
            if (!valid) {
                log.info("valid fail! url:{}, authorization:{}", url, authorization);
                throw new SecurityException("未登录");
            }
        }

        return true;
    }


    /**
     * 判定是否要验证登录
     *
     * @param url 访问url
     * @return 是否要验证
     */
    private boolean isAuthUrl(String url) {
        PathMatcher matcher = new AntPathMatcher();
        return IGNORE_URIS.stream().noneMatch(t -> matcher.match(t, url));
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception {
    }
}
