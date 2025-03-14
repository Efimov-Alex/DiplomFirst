package ru.efimov.DiplomFirst.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.efimov.DiplomFirst.entity.Role;
import ru.efimov.DiplomFirst.entity.User;
import ru.efimov.DiplomFirst.repository.StudentRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private StudentRepository studentRepository;

    private final List<User> users;

    public UserService() {
        this.users = List.of(
                new User("Alex", "efefef", "Антон", "Иванов", Collections.singleton(Role.USER)),
                new User("ivan", "12345", "Сергей", "Петров", Collections.singleton(Role.ADMIN))
        );
    }

    public Optional<User> getByLogin(@NonNull String login) {
        return users.stream()
                .filter(user -> login.equals(user.getLogin()))
                .findFirst();
    }


}
