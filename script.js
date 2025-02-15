// script.js
import http from 'k6/http';
import {check} from 'k6';
import {randomString} from "https://jslib.k6.io/k6-utils/1.1.0/index.js";

export let options = {
    vus: 300, // 가상 사용자 수
    duration: '10m', // 테스트 지속 시간
};

export default function () {
    // 랜덤한 OAuth refresh_token 생성 (테스트용)
    const refreshToken = randomString(20);

    // 요청 URL
    const url = `http://localhost:8081/api/auth/token/refresh?refreshToken=${refreshToken}`;

    // 요청 헤더
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // GET 요청 보내기
    let response = http.get(url, params);

    // 응답 확인
    check(response, {
        'is status 200': (r) => r.status === 200,
    });
}
