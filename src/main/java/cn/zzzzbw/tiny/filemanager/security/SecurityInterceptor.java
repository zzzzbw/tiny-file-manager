package cn.zzzzbw.tiny.filemanager.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * 管理后台 拦截器
 *
 * @author zbw
 * @since 2017/10/11 14:10
 */
@Slf4j
@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    private static final List<String> IGNORE_URIS = Arrays.asList("/login");


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        log.info("url");
        //登录拦截
        if (isAuthUrl(url)) {
            log.info("isAuthUrl");
            HttpSession session = request.getSession(true);
            Object login = session.getAttribute("login");
            if (StringUtils.isEmpty(login)) {
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
}
