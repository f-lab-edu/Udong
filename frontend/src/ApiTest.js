import React, { useState, useEffect } from "react";

function ApiTest() {
  const [backendData, setBackendData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [manualTestResult, setManualTestResult] = useState(null);

  useEffect(() => {
    // 컴포넌트 마운트 시 자동으로 API 호출
    fetchBackendData();
  }, []);

  const fetchBackendData = () => {
    setLoading(true);
    setError(null);
    setBackendData(null);

    fetch("/api/test")
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Network response was not ok: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        setBackendData(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
        setError(error.message);
        setLoading(false);
      });
  };

  // 수동 테스트 함수 - 직접 URL을 입력하여 테스트
  const testDirectUrl = () => {
    const url = "http://localhost:8081/api/test";
    setManualTestResult({
      status: "loading",
      message: `Testing direct connection to ${url}...`,
    });

    fetch(url)
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Network response was not ok: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        setManualTestResult({
          status: "success",
          message: "Direct connection successful!",
          data: data,
        });
      })
      .catch((error) => {
        setManualTestResult({
          status: "error",
          message: `Error: ${error.message}`,
        });
      });
  };

  return (
    <div
      className="api-test-container"
      style={{ padding: "20px", maxWidth: "800px", margin: "0 auto" }}
    >
      <h2>백엔드 API 연결 테스트</h2>

      <div
        style={{
          marginBottom: "20px",
          padding: "15px",
          border: "1px solid #ddd",
          borderRadius: "5px",
        }}
      >
        <h3>프록시 설정을 통한 API 테스트 (/api/test)</h3>
        <p>package.json의 proxy 설정을 사용하여 API를 호출합니다.</p>
        <button
          onClick={fetchBackendData}
          style={{
            padding: "8px 16px",
            backgroundColor: "#4CAF50",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          API 다시 호출
        </button>

        <div style={{ marginTop: "15px" }}>
          {loading ? (
            <p>로딩 중...</p>
          ) : error ? (
            <div
              style={{
                color: "red",
                padding: "10px",
                backgroundColor: "#ffebee",
                borderRadius: "4px",
              }}
            >
              <p>
                <strong>오류 발생:</strong> {error}
              </p>
              <p>가능한 원인:</p>
              <ul>
                <li>백엔드 서버가 실행되지 않았습니다.</li>
                <li>CORS 설정이 올바르지 않습니다.</li>
                <li>프록시 설정이 올바르지 않습니다.</li>
              </ul>
            </div>
          ) : (
            <div
              style={{
                padding: "10px",
                backgroundColor: "#e8f5e9",
                borderRadius: "4px",
              }}
            >
              <p>
                <strong>성공!</strong> 백엔드 서버에서 응답을 받았습니다:
              </p>
              <pre
                style={{
                  backgroundColor: "#f5f5f5",
                  padding: "10px",
                  borderRadius: "4px",
                  overflow: "auto",
                }}
              >
                {JSON.stringify(backendData, null, 2)}
              </pre>
            </div>
          )}
        </div>
      </div>

      <div
        style={{
          marginBottom: "20px",
          padding: "15px",
          border: "1px solid #ddd",
          borderRadius: "5px",
        }}
      >
        <h3>직접 URL 호출 테스트 (http://localhost:8081/api/test)</h3>
        <p>
          프록시 설정 없이 직접 백엔드 URL을 호출합니다. CORS 오류가 발생할 수
          있습니다.
        </p>
        <button
          onClick={testDirectUrl}
          style={{
            padding: "8px 16px",
            backgroundColor: "#2196F3",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          직접 URL 테스트
        </button>

        <div style={{ marginTop: "15px" }}>
          {manualTestResult && (
            <div
              style={{
                padding: "10px",
                backgroundColor:
                  manualTestResult.status === "success"
                    ? "#e8f5e9"
                    : manualTestResult.status === "error"
                    ? "#ffebee"
                    : "#e3f2fd",
                borderRadius: "4px",
              }}
            >
              <p>
                <strong>
                  {manualTestResult.status === "success"
                    ? "성공!"
                    : manualTestResult.status === "error"
                    ? "오류 발생:"
                    : "정보:"}
                </strong>{" "}
                {manualTestResult.message}
              </p>

              {manualTestResult.data && (
                <div>
                  <p>응답 데이터:</p>
                  <pre
                    style={{
                      backgroundColor: "#f5f5f5",
                      padding: "10px",
                      borderRadius: "4px",
                      overflow: "auto",
                    }}
                  >
                    {JSON.stringify(manualTestResult.data, null, 2)}
                  </pre>
                </div>
              )}

              {manualTestResult.status === "error" && (
                <div>
                  <p>가능한 원인:</p>
                  <ul>
                    <li>백엔드 서버가 실행되지 않았습니다.</li>
                    <li>CORS 설정이 올바르지 않습니다.</li>
                    <li>백엔드 서버 포트가 다릅니다.</li>
                  </ul>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      <div
        style={{
          padding: "15px",
          backgroundColor: "#f5f5f5",
          borderRadius: "5px",
        }}
      >
        <h3>연결 문제 해결 방법</h3>
        <ol>
          <li>
            <strong>백엔드 서버 실행 확인:</strong> 백엔드 서버가 8081 포트에서
            실행 중인지 확인하세요.
          </li>
          <li>
            <strong>프록시 설정 확인:</strong> package.json 파일에{" "}
            <code>"proxy": "http://localhost:8081"</code> 설정이 있는지
            확인하세요.
          </li>
          <li>
            <strong>CORS 설정 확인:</strong> 백엔드에서 CORS 설정이 올바르게
            되어 있는지 확인하세요.
          </li>
          <li>
            <strong>네트워크 요청 확인:</strong> 브라우저 개발자 도구의 네트워크
            탭에서 요청과 응답을 확인하세요.
          </li>
        </ol>
      </div>
    </div>
  );
}

export default ApiTest;
