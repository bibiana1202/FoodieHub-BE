import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { httpRequest } from "../../utils/httpRequest"; // httpRequest를 불러옴

function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError(null);

        // 디버깅 로그
        console.log("로그인 요청 데이터:", { email, password });

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
            // 로그인 요청
            const response = await httpRequest("POST", "/api/auth/login", {
                username: email, // email 필드
                password: password // password 필드
            });
            console.log("서버 응답 확인:", response);

            // response는 이미 JSON 데이터
            if (response.message === "Authentication successful") {
                console.log("로그인 성공:", response.message);
                window.location.href = "/main";
            } else {
                console.error("로그인 실패:", response.message);
                setError(response.message || "로그인 실패");
            }

        } catch (err) {
            console.error("로그인 실패:", err);
            setError("로그인 실패. 이메일과 비밀번호를 확인해주세요.");
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
                        name="email"
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
                <button type="submit">로그인</button>
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

            {error && <p style={{ color: "red" }}>{error}</p>}
        </div>
    );
}

export default LoginPage;
