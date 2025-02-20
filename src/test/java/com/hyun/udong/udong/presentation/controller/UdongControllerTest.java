package com.hyun.udong.udong.presentation.controller;

import com.hyun.udong.common.fixture.TestOauth;
import com.hyun.udong.common.util.DataCleanerExtension;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.udong.presentation.dto.request.CreateUdongRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;

@ExtendWith(DataCleanerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UdongControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        memberRepository.save(new Member(1L, SocialType.KAKAO, "짱구", "https://user1.com"));
    }

    @Test
    void 모집글_생성_성공시_OK_반환한다() {
        CreateUdongRequest request = new CreateUdongRequest(
                List.of(1L, 2L),
                "동행 구해요",
                "서울과 부산 여행할 동행을 찾습니다!",
                5,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                List.of("여행", "맛집"));

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", TestOauth.ACCESS_TOKEN_1L)
                .body(request)

                .when()
                .post("/api/udongs")

                .then().log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo(request.getTitle()))
                .body("description", equalTo(request.getDescription()))
                .body("startDate", equalTo(String.valueOf(request.getStartDate())))
                .body("endDate", equalTo(String.valueOf(request.getEndDate())))
                .body("currentParticipantsCount", equalTo(1))
                .body("tags", containsInAnyOrder("여행", "맛집"));
    }

    @Test
    void null_요청으로_모집글_생성시_BAD_REQUEST_반환한다() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", TestOauth.ACCESS_TOKEN_1L)

                .when()
                .post("/api/udongs")

                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 유효하지_않은_토큰으로_모집글_생성시_UNAUTHORIZED_반환한다() {
        CreateUdongRequest request = new CreateUdongRequest(
                List.of(1L, 2L),
                "동행 구해요",
                "서울과 부산 여행할 동행을 찾습니다!",
                5,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                List.of("여행", "맛집"));

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", TestOauth.ACCESS_TOKEN_NOT_EXIST)
                .body(request)

                .when()
                .post("/api/udongs")

                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @Sql(scripts = "/insert_udong_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void 검색_조건_없이_전체_우동_조회() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", TestOauth.ACCESS_TOKEN_1L)

                .when()
                .get("/api/udongs")

                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("content", not(empty()))
                .body("totalElements", equalTo(20));
    }

    @Test
    @Sql(scripts = "/insert_udong_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void 검색_조건_없이_전체_우동_조회_페이징() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", TestOauth.ACCESS_TOKEN_1L)
                .queryParam("page", 0)
                .queryParam("size", 10)

                .when()
                .get("/api/udongs")

                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("content", not(empty()))
                .body("totalElements", equalTo(20))
                .body("totalPages", equalTo(2))
                .body("hasNextPage", equalTo(true));
    }

    @Test
    @Sql(scripts = "/insert_udong_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void 특정_기간_필터링_우동_조회() {
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 10);

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", TestOauth.ACCESS_TOKEN_1L)
                .queryParam("startDate", startDate.toString())
                .queryParam("endDate", endDate.toString())

                .when()
                .get("/api/udongs")

                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("content", not(empty()))
                .body("content.startDate", everyItem(equalTo(startDate.toString())))
                .body("content.endDate", everyItem(equalTo(endDate.toString())));
    }
}
