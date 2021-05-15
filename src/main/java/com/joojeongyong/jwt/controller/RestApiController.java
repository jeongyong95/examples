package com.joojeongyong.jwt.controller;

import com.joojeongyong.jwt.model.JoinDto;
import com.joojeongyong.jwt.model.User;
import com.joojeongyong.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class RestApiController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("home")
    public String home() {
        return "<h1>home</h1>";
    }

    @PostMapping("token")
    public String token() {
        return "<h1>token</h1>";
    }

    @PostMapping("join")
    public void createUser(@RequestBody JoinDto joinDto) {
        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        user.setRoles("USER");
        userRepository.save(user);
    }

    @GetMapping("api/v1/user")
    public String user() {
        return "User 페이지입니다.";
    }

    @GetMapping("api/v1/manager")
    public String manager() {
        return "Manager 페이지입니다.";
    }

    @GetMapping("api/v1/admin")
    public String admin() {
        return "Admin 페이지입니다.";
    }
}
