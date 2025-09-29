package com.example.urlshortener.controller;

import com.example.urlshortener.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password
    ) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            return tokenProvider.generateToken(auth.getName());

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

//    @PostMapping("/register")
//    public String register(@RequestParam String username,
//                           @RequestParam String password) {
//        String hashed = passwordEncoder.encode(password);
//        User user = User.builder()
//                .username(username)
//                .password(hashed)
//                .build();
//        userRepository.save(user);
//        return "User registered";
//    }

}
