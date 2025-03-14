package ru.efimov.DiplomFirst.service;

import lombok.NonNull;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.efimov.DiplomFirst.entity.Role;
import ru.efimov.DiplomFirst.entity.Student;
import ru.efimov.DiplomFirst.entity.User;
import ru.efimov.DiplomFirst.repository.StudentRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final StudentRepository studentRepository;


    public Optional<User> getByLogin(@NonNull String login) {
        List<Student> list2 = studentRepository.findAll();
        List<User> users = new ArrayList<>();
        for (Student stud1 : list2){
            User newUser = new User(stud1.getUsername(), stud1.getPassword(), stud1.getId(), Collections.singleton(Role.USER));
            users.add(newUser);
        }


        return users.stream()
                .filter(user -> login.equals(user.getLogin()))
                .findFirst();
    }


}
