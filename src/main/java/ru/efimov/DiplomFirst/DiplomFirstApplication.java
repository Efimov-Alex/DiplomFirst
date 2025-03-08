package ru.efimov.DiplomFirst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiplomFirstApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiplomFirstApplication.class, args);
	}

}

/*
http://localhost:8083/login

{"username":"Alex", "password":"efefef"}


http://localhost:8083/api/materials

{"title":"title2", "description":"description2"}

http://localhost:8083/api/materials/1

http://localhost:8083/api/materials?title=title2





http://localhost:8083/api/enters

{"student":{"id":3, "username":"John","password":"dodhf34","date_of_registration":"2024-11-10T16:47:00"}
, "date_of_enter":"2024-11-10T13:00:00"}



http://localhost:8083/api/students

{"id":3, "username":"John","password":"dodhf34","date_of_registration":"2024-11-10T16:47:00"}

http://localhost:8083/api/studentId?username=John



http://localhost:8083/api/students/1/enters

{"student":{"id":1, "username":"John","password":"dodhf34","date_of_registration":"2024-11-10T16:47:00"}
, "date_of_enter":"2024-02-10T13:00:00"}


http://localhost:8083/api/students/1/exits

{"student":{"id":1, "username":"John","password":"dodhf34","date_of_registration":"2024-11-10T16:47:00"}
, "date_of_exit":"2024-02-10T18:00:00"}



http://localhost:8083/api/students/1/openMaterials/44


{"student":{"id":1, "username":"John","password":"dodhf34","date_of_registration":"2024-11-10T16:47:00"},
"material":{"id":44, "title":"title2", "description":"description2"}
, "date_of_open":"2024-02-10T13:00:00"}


http://localhost:8083/api/students/1/closeMaterials/44

{"student":{"id":1, "username":"John","password":"dodhf34","date_of_registration":"2024-11-10T16:47:00"},
"material":{"id":44, "title":"title2", "description":"description2"}
, "date_of_close":"2024-02-10T13:00:00"}
 */

// { "date_of_error":"2024-02-10T13:45:00", "count_errors":12}