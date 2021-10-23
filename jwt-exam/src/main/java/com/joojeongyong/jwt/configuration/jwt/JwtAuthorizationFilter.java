package com.joojeongyong.jwt.configuration.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.joojeongyong.jwt.auth.PrincipalDetails;
import com.joojeongyong.jwt.model.User;
import com.joojeongyong.jwt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//  인증이 완료된 후 권한을 확인하는 필터 --> 권한, 인증이 필요한 요청이면 BasicAuthenticationFilter를 무조건 탐
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    //    인증, 권한이 필요한 요청이 있을 때 해당 필터를 타게 됨
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("인증이나 권한이 필요해서 권한 필터를 탔음");

        String jwtHeader = request.getHeader("Authorization");
        log.info("JWT Header :" + jwtHeader);

//        토큰이 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("bearer ")) {
            chain.doFilter(request, response);
            return;
        }
//        JWT Token을 검증해서 사용자인지 확인
        String token = request.getHeader("Authorization").replace("bearer ", "");
        String username = JWT.require(Algorithm.HMAC512("studyMaker")).build()
                .verify(token).getClaim("username").asString();

//       서명이 정상적으로 이뤄짐
        if (username != null) {
            User user = userRepository.findByUsername(username);

            PrincipalDetails principalDetails = new PrincipalDetails(user);

//          JWT token 서명을 통해서 서명이 정상이면 Authentication 객체 생성
//          로그인이 아니라 권한을 확인하는 것이기 때문에 강제로 authentication 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

//          강제로 SecurityContext(세션)에 접근하여 Authentication 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        }
    }
}
