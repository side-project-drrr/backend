package com.drrr.admin.e2e;


import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminE2eTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }


    @Nested
    class AdminE2E {
        // happy test
        @Test
        void 관리자가_정상적으_로그인_됩니다() {
            given().log().all()
                    .when()
                    .post("/api/admin/signin")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all()
                    .extract();

        }
    }
}
