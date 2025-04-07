import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import styled from "styled-components";

const KakaoCallback = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // URL에서 인증 코드 추출
    const code = new URLSearchParams(location.search).get("code");

    if (code) {
      // 백엔드 API 호출하여 카카오 로그인 처리
      // AuthController.java의 /api/auth/oauth/kakao 엔드포인트 사용
      axios
        .get(`/api/auth/oauth/kakao?code=${code}`)
        .then((response) => {
          // 로그인 성공 처리
          const { accessToken, refreshToken } = response.data;

          // 토큰을 로컬 스토리지에 저장
          localStorage.setItem("accessToken", accessToken);
          localStorage.setItem("refreshToken", refreshToken);

          // 메인 페이지로 리다이렉트
          navigate("/");
        })
        .catch((err) => {
          console.error("카카오 로그인 처리 중 오류 발생:", err);
          setError("로그인 처리 중 오류가 발생했습니다.");
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
