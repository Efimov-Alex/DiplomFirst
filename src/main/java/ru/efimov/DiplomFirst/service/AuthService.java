package ru.efimov.DiplomFirst.service;


;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.efimov.DiplomFirst.component.Login;
import ru.efimov.DiplomFirst.dto.JwtRequest;
import ru.efimov.DiplomFirst.entity.Student;

import javax.security.auth.message.AuthException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final StudentService studentService;
    private final JwtProvider jwtProvider;

    public Login login(@NonNull JwtRequest authRequest) throws AuthException {
        final Student student = studentService.getByUsername(authRequest.getUsername())
                .orElseThrow(() -> new AuthException("Пользователь не найден"));
        if (student.getPassword().equals(authRequest.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(student);
            return new Login(accessToken);
        } else {
            throw new AuthException("Неправильный пароль");
        }
    }

}
