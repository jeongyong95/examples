package com.joojeongyong.jwt.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MySecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        log.info("나의 Security 필터"); 이거 2번 찍힘
        //        downcasting
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpRequest.setCharacterEncoding("UTF-8");
//        토큰이 있으면 인증, 없으면 controller 진입조차 못하게
//        id, pw로 사용자 확인이 되면 토큰을 만들어주고 응답해줌
//        요청때마다 header에 Authorization에 토큰을 담아서 올 거임
//        서버는 넘어온 토큰이 자신이 만든 토큰인지 검증하고 반응함
        if (httpRequest.getMethod().equals("POST")) {
            String headerAuth = httpRequest.getHeader("Authorization");
            log.info(headerAuth);

            if (headerAuth.equals("cos")) {
                chain.doFilter(httpRequest, httpResponse);
            } else {
                System.out.println("나의 Security 필터");
            }
        }

//        체인에 안넘겨주면 여기서 response함
        chain.doFilter(request,response);
    }
}
