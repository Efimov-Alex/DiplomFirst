package ru.efimov.DiplomFirst.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.efimov.DiplomFirst.entity.*;
import ru.efimov.DiplomFirst.repository.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping("/api")
public class UserAnalyzeController {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserAnalyzeRepository userAnalyzeRepository;

    @Autowired
    private TaskErrorRepository taskErrorRepository;

    @Autowired
    private TaskPassedRepository taskPassedRepository;

    @Autowired
    private OpenMaterialRepository openMaterialRepository;

    @Autowired
    private CloseMaterialRepository closeMaterialRepository;

    private static final Logger logger = LogManager.getLogger(UserAnalyzeController.class);


    @Autowired
    private  EnterRepository enterRepository;

    @Autowired
    private  ExitRepository exitRepository;






    @GetMapping("/students/{studentId}/metrics")
    public ResponseEntity<String> getUserMetrics(@PathVariable(value = "studentId") Long studentId
    ) {
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN" );
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<UserAnalyze> userAnalyzes = userAnalyzeRepository.findByStudentId(studentId);

        if (userAnalyzes.size() ==0){
            logger.error("Not found userAnalyze with studentId = " + studentId);
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
            logger.error("Not found userAnalyze with Characteristic = " + "Колличество ошибок");
            throw new ResourceNotFoundException("Not found userAnalyze with Characteristic = " + "Колличество ошибок");
        }




        List<TaskError> taskErrors = taskErrorRepository.findByStudentId(studentId);
        int lengthList = taskErrors.size();
        float totalSum = 0;
        for (TaskError t1 : taskErrors){
            totalSum += t1.getCount_errors();
        }

        if (lengthList == 0){
            logger.error("Not found TaskError");
            throw new ResourceNotFoundException("Not found TaskError");
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
            logger.error("Not found userAnalyze with Characteristic = " + "Колличество попыток перед сдачей");
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

        if (mapAttemptsPerTask.size() == 0){
            logger.error("Not found TaskError");
            throw new ResourceNotFoundException("Not found TaskError");
        }

        float averageFailedAttempt = (float) totalFailAttempts / mapAttemptsPerTask.size();

        newUserAnalyzeFailAttempts.setValue(String.valueOf(averageFailedAttempt));

        userAnalyzeRepository.save(newUserAnalyzeFailAttempts);

        result += " Average Count Failed Attempts - " + averageFailedAttempt;



        HashMap<Long, List<LocalDateTime>> mapTimesPerTask = new HashMap<>();

        HashMap<Long, List<Integer>> mapErrorsPerTask = new HashMap<>();

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
            logger.error("Not found userAnalyze with Characteristic = " + "Время между неуспешными попытками");
            throw new ResourceNotFoundException("Not found userAnalyze with Characteristic = " + "Время между неуспешными попытками");
        }


        for (TaskError t1 : taskErrors){
            if (!mapTimesPerTask.containsKey(t1.getTask().getId())){
                List<LocalDateTime> newList = new ArrayList<>();
                newList.add(t1.getDate_of_error());

                mapTimesPerTask.put(t1.getTask().getId(), newList);

                /*
                List<Integer> newList1 = new ArrayList<>();
                newList1.add(t1.getCount_errors());
                mapErrorsPerTask.put(t1.getTask().getId(), newList1);

                 */
            }
            else{
                List<LocalDateTime> newList = mapTimesPerTask.get(t1.getTask().getId());
                newList.add(t1.getDate_of_error());
                mapTimesPerTask.put(t1.getTask().getId(), newList);

                /*

                List<Integer> newList1 = mapErrorsPerTask.get(t1.getTask().getId());
                newList1.add(t1.getCount_errors());
                mapErrorsPerTask.put(t1.getTask().getId(), newList1);

                 */
            }
        }

        double totalTimeRepairErrors = 0;
        long totalCountRepairErrors = 0;

        for (Long failedTaskId : mapTimesPerTask.keySet()){
            List<LocalDateTime> sortedList = mapTimesPerTask.get(failedTaskId);
            Collections.sort(sortedList);

            Optional<Task> currentTask = taskRepository.findById(failedTaskId);
            Task _task = currentTask.get();

            long minutesForWork = ChronoUnit.MINUTES.between(_task.getCreation_time(), _task.getDeadline());

            for (int i=1;i < sortedList.size();i++){
                LocalDateTime prevTime = sortedList.get(i-1);
                LocalDateTime curTime = sortedList.get(i);

                long minutes = ChronoUnit.MINUTES.between(prevTime, curTime);
                totalTimeRepairErrors += (double) minutes / minutesForWork;
                totalCountRepairErrors += 1;

            }


        }

        if (totalCountRepairErrors == 0){
            logger.error("Not found TaskError");
            throw new ResourceNotFoundException("Not found TaskError");
        }

        float averageTimeRepairError= (float) totalTimeRepairErrors / totalCountRepairErrors;

        newUserAnalyzeTimesRepairError.setValue(String.valueOf(averageTimeRepairError));

        userAnalyzeRepository.save(newUserAnalyzeTimesRepairError);

        result += " Average Time for Repair Errors - " + averageTimeRepairError;






        UserAnalyze newUserAnalyzeTimeBetweenFirstTryAndPassed = null;

        for (UserAnalyze u1 : userAnalyzes){
            if (u1.getCharacteristic().equals("Время на исправление ошибок")){
                newUserAnalyzeTimeBetweenFirstTryAndPassed = new UserAnalyze();
                newUserAnalyzeTimeBetweenFirstTryAndPassed.setId(u1.getId());
                newUserAnalyzeTimeBetweenFirstTryAndPassed.setStudent(u1.getStudent());
                newUserAnalyzeTimeBetweenFirstTryAndPassed.setCharacteristic(u1.getCharacteristic());
                newUserAnalyzeTimeBetweenFirstTryAndPassed.setValue(u1.getValue());
            }
        }

        if (newUserAnalyzeTimeBetweenFirstTryAndPassed == null){
            logger.error("Not found userAnalyze with Characteristic = " + "Время на исправление ошибок");
            throw new ResourceNotFoundException("Not found userAnalyze with Characteristic = " + "Время на исправление ошибок");
        }

        HashMap<Long, LocalDateTime> mapTimeBetweenFirstTryAndPassed= new HashMap<>();

        for (TaskError t1 : taskErrors){
            if (!mapTimeBetweenFirstTryAndPassed.containsKey(t1.getTask().getId())){

                mapTimeBetweenFirstTryAndPassed.put(t1.getTask().getId(), t1.getDate_of_error());
              //  System.out.println("in map -" + t1.getDate_of_error());
            }
            else{
                LocalDateTime localDateTime = mapTimeBetweenFirstTryAndPassed.get(t1.getTask().getId());
                if (t1.getDate_of_error().compareTo(localDateTime) < 0){
                    mapTimeBetweenFirstTryAndPassed.put(t1.getTask().getId(), t1.getDate_of_error());
                }
             //   System.out.println("in map -" + t1.getDate_of_error());

            }
        }

        List<TaskPassed> taskPasseds = taskPassedRepository.findByStudentId(studentId);
        double totalTimeBetweenFirstTryAndPassed = 0;
        long totalCountBetweenFirstTryAndPassed = 0;

        for (TaskPassed t1 : taskPasseds){
            if (mapTimeBetweenFirstTryAndPassed.containsKey(t1.getTask().getId())){

                Optional<Task> currentTask = taskRepository.findById(t1.getTask().getId());
                Task _task = currentTask.get();

                long minutesForWork = ChronoUnit.MINUTES.between(_task.getCreation_time(), _task.getDeadline());

                long minutes = ChronoUnit.MINUTES.between(mapTimeBetweenFirstTryAndPassed.get(t1.getTask().getId()), t1.getDate_of_passed());
                totalTimeBetweenFirstTryAndPassed += (double) minutes / minutesForWork;
                totalCountBetweenFirstTryAndPassed += 1;
             //   System.out.println("in passed -" + minutes);
             //   System.out.println(t1.getDate_of_passed());
             //   System.out.println(mapTimeBetweenFirstTryAndPassed.get(t1.getId()));
            }
        }

        if (totalCountBetweenFirstTryAndPassed == 0){
            logger.error("Not found TaskPassed");
            throw new ResourceNotFoundException("Not found TaskPassed");
        }

        float averageTimeBetweenFirstTryAndPassed= (float) totalTimeBetweenFirstTryAndPassed / totalCountBetweenFirstTryAndPassed;

      //  System.out.println("in average -" + averageTimeBetweenFirstTryAndPassed);

        newUserAnalyzeTimeBetweenFirstTryAndPassed.setValue(String.valueOf(averageTimeBetweenFirstTryAndPassed));

        userAnalyzeRepository.save(newUserAnalyzeTimeBetweenFirstTryAndPassed);

        result += " Average Time between first try and passed - " + averageTimeBetweenFirstTryAndPassed;












        UserAnalyze newUserAnalyzeTimeOnMaterials = null;

        for (UserAnalyze u1 : userAnalyzes){
            if (u1.getCharacteristic().equals("Время на чтение материалов")){
                newUserAnalyzeTimeOnMaterials = new UserAnalyze();
                newUserAnalyzeTimeOnMaterials.setId(u1.getId());
                newUserAnalyzeTimeOnMaterials.setStudent(u1.getStudent());
                newUserAnalyzeTimeOnMaterials.setCharacteristic(u1.getCharacteristic());
                newUserAnalyzeTimeOnMaterials.setValue(u1.getValue());
            }
        }

        if (newUserAnalyzeTimeOnMaterials == null){
            logger.error("Not found userAnalyze with Characteristic = " + "Время на чтение материалов");
            throw new ResourceNotFoundException("Not found userAnalyze with Characteristic = " + "Время на чтение материалов");
        }

        List<OpenMaterial> openMaterials = openMaterialRepository.findByStudentId(studentId);
        List<CloseMaterial> closeMaterials = closeMaterialRepository.findByStudentId(studentId);

        Collections.sort(openMaterials, new Comparator<OpenMaterial>() {
            @Override
            public int compare(OpenMaterial a1, OpenMaterial a2) {
                return a1.getDate_of_open().compareTo(a2.getDate_of_open());
            }
        });

        Collections.sort(closeMaterials, new Comparator<CloseMaterial>() {
            @Override
            public int compare(CloseMaterial a1, CloseMaterial a2) {
                return a1.getDate_of_close().compareTo(a2.getDate_of_close());
            }
        });

        HashMap<Long, List<OpenMaterial>> map = new HashMap<>();

        for (OpenMaterial o1 : openMaterials){
            if (!map.containsKey(o1.getStudent().getId())){
                List<OpenMaterial> list1 = new ArrayList<>();
                list1.add(o1);
                map.put(o1.getStudent().getId(), list1);
            }
            else{
                List<OpenMaterial> list1 = map.get(o1.getStudent().getId());
                list1.add(0, o1);
                map.put(o1.getStudent().getId(), list1);
            }
        }

        long totalTimeSum = 0;
        long timeCount = 0;

        for (CloseMaterial o1 : closeMaterials){
            if (!map.containsKey(o1.getStudent().getId())){
                continue;
            }
            else{
                List<OpenMaterial> list1 = map.get(o1.getStudent().getId());
                if (list1.size() == 0){
                    logger.error("Not found OpenMaterial");
                    throw new ResourceNotFoundException("Not found OpenMaterial");
                }
                OpenMaterial o2 = list1.remove(list1.size()-1);
                map.put(o1.getStudent().getId(), list1);

                if (o2.getDate_of_open().compareTo(o1.getDate_of_close()) > 0){
                    continue;
                }
                long minutes = ChronoUnit.MINUTES.between(o2.getDate_of_open(), o1.getDate_of_close());
                System.out.println(minutes);
                System.out.println(o2.getDate_of_open());
                System.out.println(o1.getDate_of_close());
                totalTimeSum += minutes;
                timeCount += 1;
            }
        }

        if (timeCount == 0){
            logger.error("Not found CloseMaterial");
            throw new ResourceNotFoundException("Not found CloseMaterial");
        }


        float averageTimeOnMaterials = (float) totalTimeSum / timeCount;

        newUserAnalyzeTimeOnMaterials.setValue(String.valueOf(averageTimeOnMaterials));


        userAnalyzeRepository.save(newUserAnalyzeTimeOnMaterials);

        result += " Average Time on materials - " + averageTimeOnMaterials;








        UserAnalyze newUserAnalyzeTimeOnEnter = null;

        for (UserAnalyze u1 : userAnalyzes){
            if (u1.getCharacteristic().equals("Время на чтение сессии")){
                newUserAnalyzeTimeOnEnter = new UserAnalyze();
                newUserAnalyzeTimeOnEnter.setId(u1.getId());
                newUserAnalyzeTimeOnEnter.setStudent(u1.getStudent());
                newUserAnalyzeTimeOnEnter.setCharacteristic(u1.getCharacteristic());
                newUserAnalyzeTimeOnEnter.setValue(u1.getValue());
            }
        }

        if (newUserAnalyzeTimeOnEnter == null){
            logger.error("Not found userAnalyze with Characteristic = " + "Время на чтение материалов");
            throw new ResourceNotFoundException("Not found userAnalyze with Characteristic = " + "Время на чтение материалов");
        }

        List<Enter> enters = enterRepository.findByStudentId(studentId);
        List<Exit> exits = exitRepository.findByStudentId(studentId);

        Collections.sort(enters, new Comparator<Enter>() {
            @Override
            public int compare(Enter a1, Enter a2) {
                return a1.getDate_of_enter().compareTo(a2.getDate_of_enter());
            }
        });

        Collections.sort(exits, new Comparator<Exit>() {
            @Override
            public int compare(Exit a1, Exit a2) {
                return a1.getDate_of_exit().compareTo(a2.getDate_of_exit());
            }
        });

        HashMap<Long, List<Enter>> mapEnetrs = new HashMap<>();

        for (Enter o1 : enters){
            if (!mapEnetrs.containsKey(o1.getStudent().getId())){
                List<Enter> list1 = new ArrayList<>();
                list1.add(o1);
                mapEnetrs.put(o1.getStudent().getId(), list1);
            }
            else{
                List<Enter> list1 = mapEnetrs.get(o1.getStudent().getId());
                list1.add(0, o1);
                mapEnetrs.put(o1.getStudent().getId(), list1);
            }
        }

        totalTimeSum = 0;
        timeCount = 0;

        for (Exit o1 : exits){
            if (!mapEnetrs.containsKey(o1.getStudent().getId())){
                continue;
            }
            else{
                List<Enter> list1 = mapEnetrs.get(o1.getStudent().getId());
                if (list1.size() == 0){
                    throw new ResourceNotFoundException("Not found Enter");
                }
                Enter o2 = list1.remove(list1.size()-1);
                mapEnetrs.put(o1.getStudent().getId(), list1);

                if (o2.getDate_of_enter().compareTo(o1.getDate_of_exit()) > 0){
                    continue;
                }
                long minutes = ChronoUnit.MINUTES.between(o2.getDate_of_enter(), o1.getDate_of_exit());
                System.out.println(minutes);
                System.out.println(o2.getDate_of_enter());
                System.out.println(o1.getDate_of_exit());
                totalTimeSum += minutes;
                timeCount += 1;
            }
        }

        if (timeCount == 0){
            logger.error("Not found Exit");
            throw new ResourceNotFoundException("Not found Exit");
        }


        float averageTimeOnEnters = (float) totalTimeSum / timeCount;

        newUserAnalyzeTimeOnEnter.setValue(String.valueOf(averageTimeOnEnters));


        userAnalyzeRepository.save(newUserAnalyzeTimeOnEnter);

        result += " Average Time on session - " + averageTimeOnEnters;






        logger.info("Вывод всех метрик студента ");

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }





    @GetMapping("/userAnalyze/{id}")
    public ResponseEntity<UserAnalyze> getuserAnalyzeByStudentId(@PathVariable(value = "id") Long id) {
        UserAnalyze userAnalyze = userAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found UserAnalyze with id = " + id));

        logger.info("Получение объекта UserAnalyze по id " + id);
        return new ResponseEntity<>(userAnalyze, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/userAnalyzes")
    public ResponseEntity<UserAnalyze> createUserAnalyze(@PathVariable(value = "studentId") Long studentId,
                                                               @RequestBody UserAnalyze userAnalyzeRequest) {
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            logger.error("Ошибка 403 - FORBIDDEN");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        UserAnalyze userAnalyze = studentRepository.findById(studentId).map(student -> {
            userAnalyzeRequest.setStudent(student);
            return userAnalyzeRepository.save(userAnalyzeRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        logger.info("Создание объекта UserAnalyze по studentId " + studentId);
        return new ResponseEntity<>(userAnalyze, HttpStatus.CREATED);
    }

    @PutMapping("/userAnalyzes/{id}")
    public ResponseEntity<UserAnalyze> updateUserAnalyze(@PathVariable("id") long id, @RequestBody UserAnalyze userAnalyzeRequest) {
        UserAnalyze userAnalyze = userAnalyzeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserAnalyzeId " + id + "not found"));

        userAnalyze.setCharacteristic(userAnalyzeRequest.getCharacteristic());
        userAnalyze.setValue(userAnalyzeRequest.getValue());

        logger.info("Обновление объекта UserAnalyze  ");
        return new ResponseEntity<>(userAnalyzeRepository.save(userAnalyze), HttpStatus.OK);
    }

    @DeleteMapping("/userAnalyzes/{id}")
    public ResponseEntity<HttpStatus> deleteUserAnalyze(@PathVariable("id") long id) {
        userAnalyzeRepository.deleteById(id);

        logger.info("Удаление объекта UserAnalyze по id " + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students/{studentId}/userAnalyzes")
    public ResponseEntity<List<UserAnalyze>> deleteAlluserAnalyzesOfStudent(@PathVariable(value = "studentId") Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            logger.error("Not found Student with id = " + studentId);
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }
        Student student1 = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(student1.getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        userAnalyzeRepository.deleteByStudentId(studentId);
        logger.info("Удаление объектов UserAnalyze по studentId " + studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

