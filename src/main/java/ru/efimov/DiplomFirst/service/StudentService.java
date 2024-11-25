package ru.efimov.DiplomFirst.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.efimov.DiplomFirst.entity.Student;
import ru.efimov.DiplomFirst.repository.StudentRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public Optional<Student> getByUsername(@NonNull String username) {
        return studentRepository.findFirstByUsername(username);
    }
}
