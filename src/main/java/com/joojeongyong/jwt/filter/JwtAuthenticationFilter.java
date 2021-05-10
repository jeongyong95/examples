package com.joojeongyong.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joojeongyong.jwt.auth.PrincipalDetails;
import com.joojeongyong.jwt.model.User;
import com.joojeongyong.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

// /login 요청해서 id, pw 전송하면 UsernamePasswordAuthenticationFilter가 동작함 --> formLogin 때만
// 따라서 이거 만들어서 넣어줘야 한다.
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserRepository userRepository;

    //    로그인 요청에서 로그인 시도를 위해 실행되는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("JwtAuthenticationFilter 로그인 시도 메서드 작동");
        User user = new User();
        user.setUsername("jeongyong95");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setId(1l);
        user.setRoles("USER");
        userRepository.save(user);

//        1. id. pw 받음
        ObjectMapper mapper = new ObjectMapper();
        user = null;
        try {
            user = mapper.readValue(request.getInputStream(), User.class);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

//            PrincipalDetailsService의 LoadUserByUsername()가 실행됨 --> username만 관심, pw는 스프링에서 내부적으로 처리
//            Authentication은 로그인 정보가 담김
            Authentication authentication = authenticationManager.authenticate(token);
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println(principalDetails.getUser().getUsername());

            //            Authentication은 세션에 저장됨
            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(user);

//        2. 로그인 시도 : authenticationManager로 로그인 시도하면 PrincipalDetailsService가 호출됨 --> loadUserByUsername() 실행
//        3. principalDetails를 세션에 저장 if 안 담으면 권한관리가 안됨 --> 권한 관리를 위함
//        4. JWT 토큰을 만들어서 응답해주면 됨
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        인증이 통과되면 여기로 로그인 처리가 여기로 이어짐
//        attempt 메서드 직후 현재 메서드가 실행됨
        super.successfulAuthentication(request, response, chain, authResult);
    }
}
