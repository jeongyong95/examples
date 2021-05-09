package com.joojeongyong.jwt.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
@Component
public class MySecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        log.info("나의 Security 필터"); 이거 2번 찍힘
        System.out.println("나의 Security 필터");
//        체인에 안넘겨주면 여기서 response함
        chain.doFilter(request,response);
    }
}
