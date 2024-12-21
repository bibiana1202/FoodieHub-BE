import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    // const handleLogin = async (e) => {
    //     e.preventDefault();
    //     // 로그인 API 호출
    //     console.log("로그인 시도", { email, password });
    //
    //     setError(null); // 이전 에러 초기화
    //     // 입력값 검증
    //     if (!email.trim()) {
    //         setError("이메일을 입력해주세요.");
    //         return;
    //     }
    //     if (!password.trim()) {
    //         setError("비밀번호를 입력해주세요.");
    //         return;
    //     }
    //     try {
    //         // API 요청 보내기
    //         const response = await fetch("/login", {
    //             method: "POST",
    //             headers: {
    //                 "Content-Type": "application/x-www-form-urlencoded",
    //             },
    //             body: JSON.stringify({ username: email, password }),
    //         });
    //
    //         if (response.ok) {
    //             // 로그인 성공 처리
    //             // Spring Security가 설정한 success URL로 리다이렉트 처리
    //             console.log("로그인 성공! 서버에서 리다이렉트 중...");
    //
    //         } else {
    //             // 로그인 실패 처리
    //             const errorData = await response.json();
    //             console.error("로그인 실패: ", errorData.message);
    //             setError(errorData.message || "로그인 실패");
    //         }
    //     } catch (err) {
    //         console.error("로그인 요청 중 오류 발생: ", err);
    //         setError("서버와의 연결에 문제가 발생했습니다.");
    //     }
    // };
    const handleLogin = async (e) => {
        e.preventDefault();
        setError(null);

        // 입력값 검증
        if (!email.trim()) {
            setError("이메일을 입력해주세요.");
            return;
        }
        if (!password.trim()) {
            setError("비밀번호를 입력해주세요.");
            return;
        }

        try {
            // URL-encoded 데이터 준비
            const formData = new URLSearchParams();
            formData.append("username", email);
            formData.append("password", password);

            // Axios로 POST 요청 전송
            const response = await axios.post("/login", formData.toString(), {
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
            });

            console.log("응답 상태:", response.status);

            if (response.status === 200) {
                console.log("로그인 성공!");
                window.location.href = "/main"; // 성공 시 대시보드로 이동
            }
        } catch (err) {
            if (err.response) {
                // 서버가 반환한 에러 응답 처리
                console.error("로그인 실패:", err.response.data);
                setError("로그인 실패. 이메일과 비밀번호를 확인해주세요.");
            } else {
                // 네트워크 에러 또는 기타 문제
                console.error("로그인 요청 중 오류 발생:", err);
                setError("서버와의 연결에 문제가 발생했습니다.");
            }
        }
    };

    return (
        <div>
            <form onSubmit={handleLogin}>
                <div>
                    <label>Email address</label>
                    <input
                        type="email"
                        className="form-control"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        name="username"
                        required
                    />
                </div>
                <div>
                    <label>Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        name="password"
                        required
                    />
                </div>
                <button type="submit">Submit</button>
            </form>

            <button type="button" onClick={() => navigate("/signup")}>
                회원가입
            </button>
            <button type="button" onClick={() => navigate("/signup_admin")}>
                사업자 회원가입
            </button>

            <div>
                {/* OAuth 로그인 버튼 */}
                <a href="/oauth2/authorization/google">
                    <img src="/img/google.png" width="50" height="50" alt="Google 로그인" />
                </a>
                <a href="/oauth2/authorization/kakao">
                    <img src="/img/kakao.png" width="50" height="50" alt="Kakao 로그인" />
                </a>
                <a href="/oauth2/authorization/naver">
                    <img src="/img/naver.png" width="50" height="50" alt="Naver 로그인" />
                </a>
            </div>
        </div>
    );
}

export default LoginPage;
