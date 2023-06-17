package xyz.xminao.dinenow.filter;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import xyz.xminao.dinenow.common.BaseContext;
import xyz.xminao.dinenow.common.Result;

import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 强制转换
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 获取本次请求URI
        String requestURI = request.getRequestURI();
        // 拦截到的uri输出到日志
        log.info("拦截到的URI：{}", request.getRequestURI());

        // 定义不需要处理的请求
        String[] neglectURIs = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                // 前台用户端登录操作
                "/user/sendMsg",
                "/user/login"
        };

        // 判断本次请求是否需要处理，true不需要处理，false需要处理
        boolean check = check(neglectURIs, requestURI);

        // 如果不需要处理，直接放行
        if (check) {
            log.info("本次请求:{}，不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 如果需要处理，判断登陆状态，决定是否放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，ID：{}", request.getSession().getAttribute("employee"));
            // 获取线程ID
            long id = Thread.currentThread().getId();
            log.info("doFilter线程id：{}", id);
            // 获取session中的用户ID
            Long empId = (Long) request.getSession().getAttribute("employee");
            // 使用BaseContext封装id
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        // 非后台用户登录
        if (request.getSession().getAttribute("user") != null) {
            log.info("客户端用户已登录.id: {}", request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        // 未登录，返回登录结果，通过输出流向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
    }

    // 判断请求uri是否需要过滤器处理
    private boolean check(String[] neglectURIs, String requestURI) {
        for (String uri : neglectURIs) {
            if (PATH_MATCHER.match(uri, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
