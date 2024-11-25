package ru.efimov.DiplomFirst.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.efimov.DiplomFirst.component.Login;
import ru.efimov.DiplomFirst.dto.JwtRequest;
import ru.efimov.DiplomFirst.exception.UserAuthException;
import ru.efimov.DiplomFirst.service.AuthService;

import javax.security.auth.message.AuthException;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping()
    public ResponseEntity<Login> login(@RequestBody JwtRequest authRequest) {
        final Login token;
        try {
            token = authService.login(authRequest);
        } catch (AuthException e) {
            throw new UserAuthException(e);
        }
        return ResponseEntity.ok(token);
    }
}