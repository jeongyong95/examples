package com.joojeongyong.jwt.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
public class MyFilter2 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("나의 필터2");
//        체인에 안넘겨주면 여기서 response함
        chain.doFilter(request,response);
    }
}
