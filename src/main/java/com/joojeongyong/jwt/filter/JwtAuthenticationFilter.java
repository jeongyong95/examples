package com.joojeongyong.jwt.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// /login 요청해서 id, pw 전송하면 UsernamePasswordAuthenticationFilter가 동작함 --> formLogin 때만
// 따라서 이거 만들어서 넣어줘야 한다.
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    //    로그인 요청에서 로그인 시도를 위해 실행되는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("JwtAuthenticationFilter 로그인 시도 메서드 작동");

//        1. id. pw 받음
//        2. 로그인 시도 : authenticationManager로 로그인 시도하면 PrincipalDetailsService가 호출됨 --> loadUserByUsername() 실행
//        3. principalDetails를 세션에 저장 if 안 담으면 권한관리가 안됨 --> 권한 관리를 위함
//        4. JWT 토큰을 만들어서 응답해주면 됨
        return super.attemptAuthentication(request, response);
    }

}
