package com.hyun.udong.udong.presentation.controller;

import com.hyun.udong.common.fixture.TestOauth;
import com.hyun.udong.common.util.DataCleanerExtension;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.member.domain.SocialType;
import com.hyun.udong.member.infrastructure.repository.MemberRepository;
import com.hyun.udong.udong.domain.Participant;
import com.hyun.udong.udong.domain.Udong;
import com.hyun.udong.udong.domain.UdongStatus;
import com.hyun.udong.udong.domain.WaitingMember;
import com.hyun.udong.udong.infrastructure.repository.UdongRepository;
import com.hyun.udong.udong.infrastructure.repository.participant.ParticipantRepository;
import com.hyun.udong.udong.infrastructure.repository.waitingmember.WaitingMemberRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

@ExtendWith(DataCleanerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UdongControllerTest {
    public static final LocalDate NOW = LocalDate.now();

    private Udong udong;
    private Member owner;
    private String ownerToken;

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UdongRepository udongRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private WaitingMemberRepository waitingMemberRepository;

    @Autowired
    private TestOauth testOauth;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        owner = memberRepository.save(new Member(1L, SocialType.KAKAO, "짱구", "https://user1.com"));
        ownerToken = testOauth.generateAccessToken(owner.getId());

        udong = createSingleUdong(5, NOW, NOW.plusDays(5), UdongStatus.PREPARE);
    }

    private List<Udong> createUdong(int count, int recruitmentCount, LocalDate startDate, LocalDate endDate, UdongStatus status) {
        List<Udong> udongs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            udongs.add(udongRepository.save(new Udong(owner.getId(), "title" + i, "description" + i, recruitmentCount, startDate, endDate, status)));
        }
        return udongs;
    }

    private Udong createSingleUdong(int recruitmentCount, LocalDate startDate, LocalDate endDate, UdongStatus status) {
        return createUdong(1, recruitmentCount, startDate, endDate, status).get(0);
    }

    @Test
    void 모집글_생성_성공시_OK_반환한다() {
        CreateUdongRequest request = new CreateUdongRequest(
                List.of(1L, 2L),
                "동행 구해요",
                "서울과 부산 여행할 동행을 찾습니다!",
                5,
                NOW.plusDays(5),
                NOW.plusDays(10),
                List.of("여행", "맛집"));

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", ownerToken)
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
                .header("Authorization", ownerToken)

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
                NOW.plusDays(5),
                NOW.plusDays(10),
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
    void 검색_조건_없이_전체_우동_조회() {
        createUdong(10, 5, NOW, NOW.plusDays(5), UdongStatus.PREPARE);

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", ownerToken)

                .when()
                .get("/api/udongs")

                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("content", not(empty()))
                .body("totalElements", equalTo(11)); // 기본 1개 + 10개 추가
    }

    @Test
    void 검색_조건_없이_전체_우동_조회_페이징() {
        createUdong(10, 5, NOW, NOW.plusDays(5), UdongStatus.PREPARE);

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", ownerToken)
                .queryParam("page", 0)
                .queryParam("size", 10)

                .when()
                .get("/api/udongs")

                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("content", not(empty()))
                .body("totalElements", equalTo(11)) // 기본 1개 + 10개 추가
                .body("totalPages", equalTo(2))
                .body("hasNextPage", equalTo(true));
    }

    @Test
    void 특정_기간_필터링_우동_조회() {
        // given
        LocalDate startDate = NOW;
        LocalDate endDate = NOW.plusDays(5);

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", ownerToken)
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

    @Test
    void 우동_참여_요청_성공() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "맹구", "https://user2.com"));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", testOauth.generateAccessToken(requestMember.getId()))

                .when()
                .post("/api/udongs/{udongId}/participate", udong.getId())

                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("udongId", equalTo(udong.getId().intValue()))
                .body("memberId", equalTo(requestMember.getId().intValue()))
                .body("requestDate", equalTo(NOW.toString()));
    }

    @Test
    void 이미_참여_중인_우동에_참여_요청시_실패() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "맹구", "https://user2.com"));
        participantRepository.save(Participant.from(requestMember.getId(), udong));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", testOauth.generateAccessToken(requestMember.getId()))

                .when()
                .post("/api/udongs/{udongId}/participate", udong.getId())

                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("이미 참여 중인 우동입니다."));
    }

    @Test
    void 모집_인원이_다_찬_우동에_참여_요청시_실패() {
        // given
        Udong fullUdong = createSingleUdong(2, NOW, NOW.plusDays(5), UdongStatus.PREPARE);
        memberRepository.save(new Member(2L, SocialType.KAKAO, "맹구", "https://user2.com"));
        memberRepository.save(new Member(3L, SocialType.KAKAO, "훈이", "https://user3.com"));
        participantRepository.saveAll(List.of(Participant.from(2L, fullUdong), Participant.from(3L, fullUdong)));

        Member requestMember = memberRepository.save(new Member(4L, SocialType.KAKAO, "유리", "https://user4.com"));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", testOauth.generateAccessToken(requestMember.getId()))

                .when()
                .post("/api/udongs/{udongId}/participate", fullUdong.getId())

                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("모집 인원이 이미 다 찼습니다."));
    }

    @Test
    void 존재하지_않는_우동에_참여_요청시_실패() {
        // given
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "맹구", "https://user2.com"));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", testOauth.generateAccessToken(requestMember.getId()))

                .when()
                .post("/api/udongs/{udongId}/participate", 999L)

                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("존재하지 않는 우동입니다."));
    }

    @Test
    void 이미_시작된_우동에_참여_요청시_실패() {
        // given
        Udong inProgressUdong = createSingleUdong(2, NOW, NOW.plusDays(5), UdongStatus.IN_PROGRESS);
        Member requestMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "맹구", "https://user2.com"));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", testOauth.generateAccessToken(requestMember.getId()))

                .when()
                .post("/api/udongs/{udongId}/participate", inProgressUdong.getId())

                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("여행이 시작되었거나 종료된 우동에는 참여할 수 없습니다."));
    }

    @Test
    void 모임장이_대기자를_승인하면_참여자로_등록된다() {
        // given
        Member waitingMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "맹구", "https://waiting.com"));
        waitingMemberRepository.save(WaitingMember.builder()
                .udong(udong)
                .memberId(waitingMember.getId())
                .build());

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", ownerToken)

                .when()
                .post("/api/udongs/{udongId}/approve/{waitingMemberId}", udong.getId(), waitingMember.getId())

                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("udongId", equalTo(udong.getId().intValue()))
                .body("memberId", equalTo(waitingMember.getId().intValue()))
                .body("participationDate", equalTo(NOW.toString()));
    }

    @Test
    void 모임장이_대기자를_거절하면_대기자리스트에서_삭제된다() {
        // given
        Member waitingMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "맹구", "https://waiting.com"));
        waitingMemberRepository.save(WaitingMember.builder()
                .udong(udong)
                .memberId(waitingMember.getId())
                .build());

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", ownerToken)

                .when()
                .delete("/api/udongs/{udongId}/reject/{waitingMemberId}", udong.getId(), waitingMember.getId())

                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 모임장이_아닌_사용자가_승인요청하면_실패한다() {
        // given
        Member waitingMember = memberRepository.save(new Member(2L, SocialType.KAKAO, "맹구", "https://waiting.com"));
        waitingMemberRepository.save(WaitingMember.builder()
                .udong(udong)
                .memberId(waitingMember.getId())
                .build());

        Member notOwnerMember = memberRepository.save(new Member(3L, SocialType.KAKAO, "훈이", "https://user3.com"));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", testOauth.generateAccessToken(notOwnerMember.getId()))

                .when()
                .post("/api/udongs/{udongId}/approve/{waitingMemberId}", udong.getId(), waitingMember.getId())

                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
