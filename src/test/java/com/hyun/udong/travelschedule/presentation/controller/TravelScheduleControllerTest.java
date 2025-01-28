package com.hyun.udong.travelschedule.presentation.controller;

import com.hyun.udong.auth.oauth.TestOauth;
import com.hyun.udong.travelschedule.presentation.dto.TravelScheduleRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/member.sql")
class TravelScheduleControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 여행_일정_업데이트_성공시_OK_반환한다() {
        TravelScheduleRequest request = new TravelScheduleRequest(
                LocalDate.of(2025, 1, 25),
                LocalDate.of(2025, 2, 10),
                List.of(1L, 2L));

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", TestOauth.ACCESS_TOKEN_1L)
                .body(request)

                .when()
                .post("/travel/schedule")

                .then().log().all()
                .statusCode(200)
                .body("startDate", equalTo(String.valueOf(request.getStartDate())))
                .body("endDate", equalTo(String.valueOf(request.getEndDate())))
                .body("travelScheduleCities[0].cityId", equalTo(1))
                .body("travelScheduleCities[1].cityId", equalTo(2));
    }

    @Test
    void null_요청으로_여행_일정_업데이트시_BAD_REQUEST_반환한다() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", TestOauth.ACCESS_TOKEN_1L)

                .when()
                .post("/travel/schedule")

                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
