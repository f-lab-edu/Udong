import http from 'k6/http';
import {check} from 'k6';
import {randomIntBetween, randomString} from "https://jslib.k6.io/k6-utils/1.1.0/index.js";

// 부하 테스트 설정
export let options = {
    vus: 100, // 동시 사용자 수
    duration: '3m', // 테스트 지속 시간
};

// JWT 토큰 재사용(엔드 포인트 통과가 목적이므로 userId=1로 고정 후 캐싱)
let cachedToken = null;

function getAuthToken() {
    if (cachedToken) {
        return cachedToken;
    }

    const tokenUrl = 'http://localhost:8080/api/test/generate-token?userId=1';
    let tokenResponse = http.get(tokenUrl);

    check(tokenResponse, {
        'token retrieved successfully': (res) => res.status === 200 && res.body.length > 0,
    });

    cachedToken = `Bearer ${tokenResponse.body}`;
    return cachedToken;
}

export default function () {
    const token = getAuthToken();
    const url = 'http://localhost:8080/api/udongs';

    // JSON 바디 설정
    const requestBody = JSON.stringify({
        cityIds: [randomIntBetween(1, 20)], // 랜덤한 도시 2개 선택
        title: randomString(randomIntBetween(10, 50)), // 10~50자 랜덤 제목
        description: randomString(randomIntBetween(50, 300)), // 50~300자 랜덤 내용
        recruitmentCount: randomIntBetween(1, 10), // 1~10명
        startDate: '2025-03-01',
        endDate: '2025-03-10',
        tags: ['태그1', '태그2']
    });

    // 요청 헤더 설정
    const headers = {
        'Authorization': token,
        'Content-Type': 'application/json',
    };

    // HTTP POST 요청 전송
    let response = http.post(url, requestBody, {headers});

    // 응답 확인
    check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 2000ms': (r) => r.timings.duration < 2000,
    });
}
