package cn.zzzzbw.tiny.filemanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class ManagerInterceptor extends HandlerInterceptorAdapter {

    private static final List<String> IGNORE_URIS = Arrays.asList("/", "/login");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        //登录拦截
        if (isAuthUrl(url) && !isLogin(request)) {
            response.sendRedirect("/");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (isLogin(request) && modelAndView != null) {
            modelAndView.addObject("login", "ok");
        }
    }

    /**
     * 判定是否要验证登录
     *
     * @param url 访问url
     * @return 是否要验证
     */
    private boolean isAuthUrl(String url) {
        return IGNORE_URIS.stream().noneMatch(t -> new AntPathMatcher().match(t, url));
    }

    /**
     * 是否登录
     *
     * @param request
     * @return
     */
    private boolean isLogin(HttpServletRequest request) {
        Object login = request.getSession().getAttribute("login");
        return !StringUtils.isEmpty(login);
    }
}
