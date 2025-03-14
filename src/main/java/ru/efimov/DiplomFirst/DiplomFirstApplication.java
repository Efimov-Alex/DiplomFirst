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
{
    "type": "Bearer",
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBbGV4IiwiZXhwIjoxNzQ0NTYwMDM2LCJyb2xlcyI6WyJVU0VSIl19.BSTqHHEfLyoWpeFZcF7VoZH_jisFZmb5QppT_0wCjOeyXLqCOHEU4HoJ1k8xkaLZhbIFCka8-sOtbUnjgZyBGQ",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBbGV4IiwiZXhwIjoxNzY3ODg4MDM2fQ.JCj3rCdyWtHtX4b7spKboZJeAXca5HyMcFdH1hfBuLpxFgcP6NTMtlYq6WPp-DO52RBX-D4ngtvDIcW4keq93A"
}

http://localhost:8083/api/auth/login

http://localhost:8083/api/students/29/metrics

{"characteristic":"Время на исправление ошибок", "value":"efefef"}


http://localhost:8083/api/taskError/100

{"login":"Alex", "password":"efefef"}


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

http://localhost:8083/api/materials/67/materialAnalyzes/

{"mean_time":30.0}


http://localhost:8083/api/tasks/56/taskAnalyzes

{"mean_time":45.7, "creation_time":"2025-03-11T13:00:00", "deadline":"2025-03-12T13:45:00","count_error":3.6}



http://localhost:8083/api/auth/login

http://localhost:8083/api/students



 */

// { "date_of_error":"2024-02-10T13:45:00", "count_errors":12}