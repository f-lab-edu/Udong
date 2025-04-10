import React, { useEffect, useState, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import styled from "styled-components";

const KakaoCallback = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const isProcessing = useRef(false);

  useEffect(() => {
    // 이미 처리 중이면 중복 실행 방지
    if (isProcessing.current) return;

    // URL에서 인증 코드 추출
    const code = new URLSearchParams(location.search).get("code");

    if (code) {
      // 처리 중 플래그 설정
      isProcessing.current = true;

      // 백엔드 API 호출하여 카카오 로그인 처리
      axios
        .get(`/api/auth/oauth/kakao?code=${code}`)
        .then((response) => {
          // 로그인 성공 처리
          console.log("로그인 응답 전체:", response);
          console.log("로그인 응답 데이터:", response.data);
          console.log("로그인 응답 상태:", response.status);

          // 응답 데이터 구조 확인 및 처리
          if (response.data && response.data.token) {
            console.log("토큰 정보:", response.data.token);
            const { accessToken, refreshToken } = response.data.token;

            // 토큰 확인
            console.log("액세스 토큰:", accessToken);
            console.log("리프레시 토큰:", refreshToken);

            // 토큰이 있는지 확인
            if (accessToken && refreshToken) {
              // 토큰을 로컬 스토리지에 저장
              localStorage.setItem("accessToken", accessToken);
              localStorage.setItem("refreshToken", refreshToken);

              // 로컬 스토리지 확인
              console.log(
                "저장된 액세스 토큰:",
                localStorage.getItem("accessToken")
              );
              console.log(
                "저장된 리프레시 토큰:",
                localStorage.getItem("refreshToken")
              );

              // 메인 페이지로 리다이렉트
              navigate("/");
            } else {
              throw new Error("토큰이 응답에 포함되어 있지 않습니다.");
            }
          } else {
            console.error("응답 데이터 구조 확인:", response.data);
            throw new Error("응답 데이터 구조가 올바르지 않습니다.");
          }
        })
        .catch((err) => {
          console.error("카카오 로그인 처리 중 오류 발생:", err);

          // KOE320 에러 처리
          if (err.response?.data?.error === "invalid_grant") {
            setError(
              "인증 코드가 만료되었거나 이미 사용되었습니다. 다시 로그인해주세요."
            );
          } else {
            setError(
              err.response?.data?.message ||
                err.message ||
                "로그인 처리 중 오류가 발생했습니다."
            );
          }
          setLoading(false);
        });
    } else {
      setError("인증 코드를 찾을 수 없습니다.");
      setLoading(false);
    }
  }, [location, navigate]);

  if (loading) {
    return (
      <CallbackContainer>
        <LoadingMessage>로그인 처리 중입니다...</LoadingMessage>
      </CallbackContainer>
    );
  }

  if (error) {
    return (
      <CallbackContainer>
        <ErrorMessage>{error}</ErrorMessage>
        <BackButton onClick={() => navigate("/login")}>
          로그인 페이지로 돌아가기
        </BackButton>
      </CallbackContainer>
    );
  }

  return null;
};

const CallbackContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  padding: 20px;
  text-align: center;
`;

const LoadingMessage = styled.p`
  font-size: 18px;
  margin-bottom: 20px;
`;

const ErrorMessage = styled.p`
  color: #e74c3c;
  font-size: 18px;
  margin-bottom: 20px;
`;

const BackButton = styled.button`
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  &:hover {
    background-color: #2980b9;
  }
`;

export default KakaoCallback;
