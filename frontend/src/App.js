import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
// import logo from "./assets/images/logo.png";
import "./App.css";
import ApiTest from "./ApiTest";
import LoginPage from "./pages/LoginPage";
import KakaoCallback from "./pages/KakaoCallback";
import GithubCallback from "./pages/GithubCallback";

function App() {
  const [backendMessage, setBackendMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // 백엔드 API 호출
    fetch("/api/test")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        setBackendMessage(data.message);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
        setError("Failed to connect to backend");
        setLoading(false);
      });
  }, []);

  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/oauth/kakao/callback" element={<KakaoCallback />} />
          <Route path="/oauth/github/callback" element={<GithubCallback />} />
          <Route
            path="/"
            element={
              <header className="App-header">
                <img
                  src="https://upload.wikimedia.org/wikipedia/commons/thumb/9/99/Unofficial_JavaScript_logo_2.svg/1200px-Unofficial_JavaScript_logo_2.svg.png"
                  className="App-logo"
                  alt="logo"
                />
                <p>
                  Edit <code>src/App.js</code> and save to reload.
                </p>

                <Link
                  to="/login"
                  style={{ marginBottom: "20px", color: "white" }}
                >
                  로그인 페이지로 이동
                </Link>

                {/* 백엔드 연결 상태 표시 */}
                <div
                  style={{
                    marginTop: "20px",
                    padding: "10px",
                    backgroundColor: "#282c34",
                    borderRadius: "5px",
                  }}
                >
                  <h3>Backend Connection:</h3>
                  {loading ? (
                    <p>Loading data from backend...</p>
                  ) : error ? (
                    <p style={{ color: "red" }}>{error}</p>
                  ) : (
                    <p style={{ color: "green" }}>Message: {backendMessage}</p>
                  )}
                </div>

                {/* API 테스트 컴포넌트 추가 */}
                <div
                  style={{
                    marginTop: "30px",
                    width: "100%",
                    maxWidth: "800px",
                  }}
                >
                  <ApiTest />
                </div>
                <a
                  className="App-link"
                  href="https://reactjs.org"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  Learn React
                </a>
              </header>
            }
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
