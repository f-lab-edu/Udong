import React from "react";
import styled from "styled-components";
import { useNavigate } from "react-router-dom";
import logo from "../assets/images/logo.png";
import kakaoLogo from "../assets/kakao_logo.svg";

const LoginPage = () => {
  const navigate = useNavigate();

  // 카카오 로그인 처리
  const handleKakaoLogin = () => {
    // 카카오 인증 서버로 리다이렉트
    // 백엔드의 AuthController에 정의된 엔드포인트를 사용
    window.location.href = `https://kauth.kakao.com/oauth/authorize?client_id=${process.env.REACT_APP_KAKAO_CLIENT_ID}&redirect_uri=${process.env.REACT_APP_KAKAO_REDIRECT_URI}&response_type=code`;
  };

  // 깃허브 로그인 기능은 나중에 구현 예정

  // 로그인 없이 둘러보기
  const handleSkipLogin = () => {
    navigate("/");
  };

  return (
    <LoginContainer>
      <LoginHeader>
        <BackButton onClick={() => navigate("/")}>&lt;</BackButton>
        <HeaderTitle>로그인</HeaderTitle>
        <HomeButton onClick={() => navigate("/")}>🏠</HomeButton>
      </LoginHeader>

      <LogoContainer>
        <LogoImage src={logo} alt="우동 로고" />
      </LogoContainer>

      <LoginButtonsContainer>
        <KakaoButton onClick={handleKakaoLogin}>
          <KakaoLogoImg src={kakaoLogo} alt="카카오 로고" />
          카카오 로그인
        </KakaoButton>
      </LoginButtonsContainer>

      <SkipLoginButton onClick={handleSkipLogin}>
        로그인 없이 둘러보기
      </SkipLoginButton>
    </LoginContainer>
  );
};

// 스타일 컴포넌트
const LoginContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  height: 100vh;
  box-sizing: border-box;
  border: 1px solid #e0e0e0;
`;

const LoginHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 10px 0;
  margin-bottom: 20px;
`;

const BackButton = styled.button`
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
`;

const HomeButton = styled.button`
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
`;

const HeaderTitle = styled.h1`
  font-size: 20px;
  margin: 0;
`;

const LogoContainer = styled.div`
  display: flex;
  justify-content: center;
  margin: 40px 0;
`;

const LogoImage = styled.img`
  width: 300px;
  height: 300px;
  object-fit: contain;
`;

const LoginButtonsContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 10px;
  margin-bottom: 20px;
`;

const KakaoButton = styled.button`
  width: 100%;
  padding: 12px;
  margin-bottom: 10px;
  background-color: #fee500;
  color: #000000;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  &:hover {
    background-color: #e6cf00;
  }
`;

const KakaoLogoImg = styled.img`
  width: 24px;
  height: 22px;
  margin-right: 8px;
`;

const SkipLoginButton = styled.button`
  background: none;
  border: none;
  color: #666;
  margin-top: 20px;
  text-decoration: underline;
  cursor: pointer;
  &:hover {
    color: #333;
  }
`;

export default LoginPage;
