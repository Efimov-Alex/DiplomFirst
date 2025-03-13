package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.*;
import ru.efimov.DiplomFirst.repository.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class UserAnalyzeController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserAnalyzeRepository userAnalyzeRepository;

    @Autowired
    private TaskErrorRepository taskErrorRepository;

    @Autowired
    private TaskPassedRepository taskPassedRepository;






    @GetMapping("/students/{studentId}/metrics")
    public ResponseEntity<String> getUserMetrics(@PathVariable(value = "studentId") Long studentId
    ) {
        List<UserAnalyze> userAnalyzes = userAnalyzeRepository.findByStudentId(studentId);

        if (userAnalyzes.size() ==0){
            throw new ResourceNotFoundException("Not found userAnalyze with studentId = " + studentId);
        }
        UserAnalyze newUserAnalyzeErrors = null;

        for (UserAnalyze u1 : userAnalyzes){
            if (u1.getCharacteristic().equals("Колличество ошибок")){
                newUserAnalyzeErrors = new UserAnalyze();
                newUserAnalyzeErrors.setId(u1.getId());
                newUserAnalyzeErrors.setStudent(u1.getStudent());
                newUserAnalyzeErrors.setCharacteristic(u1.getCharacteristic());
                newUserAnalyzeErrors.setValue(u1.getValue());
            }
        }

        if (newUserAnalyzeErrors == null){
            throw new ResourceNotFoundException("Not found userAnalyze with Characteristic = " + "Колличество ошибок");
        }




        List<TaskError> taskErrors = taskErrorRepository.findByStudentId(studentId);
        int lengthList = taskErrors.size();
        float totalSum = 0;
        for (TaskError t1 : taskErrors){
            totalSum += t1.getCount_errors();
        }

        float averageCount = (float) totalSum / lengthList;

        newUserAnalyzeErrors.setValue(String.valueOf(averageCount));

        userAnalyzeRepository.save(newUserAnalyzeErrors);

        String result = "Average Count Errors - " + averageCount;




        HashMap<Long, Integer> mapAttemptsPerTask = new HashMap<>();



        UserAnalyze newUserAnalyzeFailAttempts= null;

        for (UserAnalyze u1 : userAnalyzes){
            if (u1.getCharacteristic().equals("Колличество попыток перед сдачей")){
                newUserAnalyzeFailAttempts = new UserAnalyze();
                newUserAnalyzeFailAttempts.setId(u1.getId());
                newUserAnalyzeFailAttempts.setStudent(u1.getStudent());
                newUserAnalyzeFailAttempts.setCharacteristic(u1.getCharacteristic());
                newUserAnalyzeFailAttempts.setValue(u1.getValue());
            }
        }

        if (newUserAnalyzeFailAttempts == null){
            throw new ResourceNotFoundException("Not found userAnalyze with Characteristic = " + "Колличество попыток перед сдачей");
        }

        for (TaskError t1 : taskErrors){
            if (!mapAttemptsPerTask.containsKey(t1.getTask().getId())){
                mapAttemptsPerTask.put(t1.getTask().getId(), 1);
            }
            else{
                mapAttemptsPerTask.put(t1.getTask().getId(), mapAttemptsPerTask.get(t1.getTask().getId())+1);
            }
        }

        long totalFailAttempts = 0;

        for (Long failedTaskId : mapAttemptsPerTask.keySet()){
            totalFailAttempts += mapAttemptsPerTask.get(failedTaskId);
        }

        float averageFailedAttempt = (float) totalFailAttempts / mapAttemptsPerTask.size();

        newUserAnalyzeFailAttempts.setValue(String.valueOf(averageFailedAttempt));

        userAnalyzeRepository.save(newUserAnalyzeFailAttempts);

        result += " Average Count Failed Attempts - " + averageFailedAttempt;



        HashMap<Long, List<LocalDateTime>> mapTimesPerTask = new HashMap<>();

        UserAnalyze newUserAnalyzeTimesRepairError = null;

        for (UserAnalyze u1 : userAnalyzes){
            if (u1.getCharacteristic().equals("Время между неуспешными попытками")){
                newUserAnalyzeTimesRepairError = new UserAnalyze();
                newUserAnalyzeTimesRepairError.setId(u1.getId());
                newUserAnalyzeTimesRepairError.setStudent(u1.getStudent());
                newUserAnalyzeTimesRepairError.setCharacteristic(u1.getCharacteristic());
                newUserAnalyzeTimesRepairError.setValue(u1.getValue());
            }
        }

        if (newUserAnalyzeTimesRepairError == null){
            throw new ResourceNotFoundException("Not found userAnalyze with Characteristic = " + "Время между неуспешными попытками");
        }


        for (TaskError t1 : taskErrors){
            if (!mapTimesPerTask.containsKey(t1.getTask().getId())){
                List<LocalDateTime> newList = new ArrayList<>();
                newList.add(t1.getDate_of_error());

                mapTimesPerTask.put(t1.getTask().getId(), newList);
            }
            else{
                List<LocalDateTime> newList = mapTimesPerTask.get(t1.getTask().getId());
                newList.add(t1.getDate_of_error());
                mapTimesPerTask.put(t1.getTask().getId(), newList);
            }
        }

        long totalTimeRepairErrors = 0;
        long totalCountRepairErrors = 0;

        for (Long failedTaskId : mapTimesPerTask.keySet()){
            List<LocalDateTime> sortedList = mapTimesPerTask.get(failedTaskId);
            Collections.sort(sortedList);

            for (int i=1;i < sortedList.size();i++){
                LocalDateTime prevTime = sortedList.get(i-1);
                LocalDateTime curTime = sortedList.get(i);

                long minutes = ChronoUnit.MINUTES.between(prevTime, curTime);
                totalTimeRepairErrors += minutes;
                totalCountRepairErrors += 1;

            }


        }

        float averageTimeRepairError= (float) totalTimeRepairErrors / totalCountRepairErrors;

        newUserAnalyzeTimesRepairError.setValue(String.valueOf(averageTimeRepairError));

        userAnalyzeRepository.save(newUserAnalyzeTimesRepairError);

        result += " Average Time for Repair Errors - " + averageTimeRepairError;







        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }





    @GetMapping("/userAnalyze/{id}")
    public ResponseEntity<UserAnalyze> getuserAnalyzeByStudentId(@PathVariable(value = "id") Long id) {
        UserAnalyze userAnalyze = userAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found UserAnalyze with id = " + id));

        return new ResponseEntity<>(userAnalyze, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/userAnalyzes")
    public ResponseEntity<UserAnalyze> createUserAnalyze(@PathVariable(value = "studentId") Long studentId,
                                                               @RequestBody UserAnalyze userAnalyzeRequest) {
        UserAnalyze userAnalyze = studentRepository.findById(studentId).map(student -> {
            userAnalyzeRequest.setStudent(student);
            return userAnalyzeRepository.save(userAnalyzeRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(userAnalyze, HttpStatus.CREATED);
    }

    @PutMapping("/userAnalyzes/{id}")
    public ResponseEntity<UserAnalyze> updateUserAnalyze(@PathVariable("id") long id, @RequestBody UserAnalyze userAnalyzeRequest) {
        UserAnalyze userAnalyze = userAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserAnalyzeId " + id + "not found"));

        userAnalyze.setCharacteristic(userAnalyzeRequest.getCharacteristic());
        userAnalyze.setValue(userAnalyzeRequest.getValue());

        return new ResponseEntity<>(userAnalyzeRepository.save(userAnalyze), HttpStatus.OK);
    }

    @DeleteMapping("/userAnalyzes/{id}")
    public ResponseEntity<HttpStatus> deleteUserAnalyze(@PathVariable("id") long id) {
        userAnalyzeRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/userAnalyzes")
    public ResponseEntity<List<UserAnalyze>> deleteAlluserAnalyzesOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        userAnalyzeRepository.deleteByStudentId(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

