package com.example.demo.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Order(2)
@WebFilter(filterName = "CustomFilter1", urlPatterns = {"/*"}, asyncSupported = true)
@Slf4j
public class CustomFilter1 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("filter1 初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("dofilter1 处理请求");
        servletResponse.setCharacterEncoding("GBK");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        log.info("filter1 销毁");
    }
}
