import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { httpRequest } from "../../utils/httpRequest"; // httpRequest를 불러옴

function SignUpPage() {
    const [formData, setFormData] = useState({
        email: "",
        password1: "",
        password2: "",
        nickname: "",
        name: "",
        cellphone: "",
        role: "ROLE_USER",
    });

    const [errors, setErrors] = useState([]);
    const [nicknameCheckResult, setNicknameCheckResult] = useState("");
    const navigate = useNavigate();

    // 입력 값 변경 처리
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    // 닉네임 중복 확인
    const checkNickname = async () => {
        try {
            const response = await httpRequest(
                "GET",
                `/api/user/check-nickname?nickname=${formData.nickname}`
            );

            setNicknameCheckResult(response.message || "사용 가능한 닉네임입니다.");
        } catch (error) {
            setNicknameCheckResult(error.message || "닉네임 중복 확인 중 오류가 발생했습니다.");
        }
    };

    // 폼 제출 처리
    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors([]);  // 기존 에러 초기화

        // // 입력값 유효성 검사
        // if (!formData.email) {
        //     setErrors((prev) => [...prev, "이메일을 입력해주세요."]);
        //     return;
        // }
        // if (formData.password1 !== formData.password2) {
        //     setErrors((prev) => [...prev, "비밀번호가 일치하지 않습니다."]);
        //     return;
        // }

        try {
            const response = await httpRequest("POST", "/api/auth/user", formData);
            alert("회원가입이 완료되었습니다.");
            navigate("/login"); // 회원가입 성공 시 로그인 페이지로 이동
        } catch (error) {
            console.error("회원가입 실패:", error);

            // 서버 응답에서 에러 메시지 추출
            if (error.response && error.response.errors) {
                const validationErrors = error.response.errors; // errors 배열
                alert(validationErrors.join("\n")); // 줄바꿈으로 메시지 표시
            } else {
                alert("회원가입 중 알 수 없는 오류가 발생했습니다.");
            }

        }
    };

    return (
        <div className="container my-3">
            <h2>SIGN UP</h2>
            <p>회원 가입</p>

            {/* 폼 에러 표시 */}
            {errors.length > 0 && (
                <div>
                    <ul>
                        {errors.map((error, index) => (
                            <li key={index} style={{ color: "red" }}>{error}</li>
                        ))}
                    </ul>
                </div>
            )}

            <form onSubmit={handleSubmit}>
                {/* 이메일 입력 */}
                <div>
                    <label>아이디(Email)</label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />
                </div>

                {/* 비밀번호 입력 */}
                <div>
                    <label>비밀번호</label>
                    <input
                        type="password"
                        name="password1"
                        value={formData.password1}
                        onChange={handleChange}
                        required
                    />
                </div>

                {/* 비밀번호 확인 */}
                <div>
                    <label>비밀번호 확인</label>
                    <input
                        type="password"
                        name="password2"
                        value={formData.password2}
                        onChange={handleChange}
                        required
                    />
                </div>

                {/* 닉네임 */}
                <div>
                    <label>닉네임</label>
                    <input
                        type="text"
                        name="nickname"
                        value={formData.nickname}
                        onChange={handleChange}
                        required
                    />
                    {/*<button type="button" onClick={checkNickname}>*/}
                    {/*    중복 확인*/}
                    {/*</button>*/}
                    {/*{nicknameCheckResult && (*/}
                    {/*    <div style={{ marginTop: "10px", color: "red" }}>{nicknameCheckResult}</div>*/}
                    {/*)}*/}
                </div>

                {/* 실명 */}
                <div>
                    <label>실명</label>
                    <input
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />
                </div>

                {/* 전화번호 */}
                <div>
                    <label>전화번호</label>
                    <input
                        type="text"
                        name="cellphone"
                        value={formData.cellphone}
                        onChange={handleChange}
                        required
                    />
                </div>

                {/* hidden input으로 role 전달 */}
                <input type="hidden" name="role" value={formData.role} />

                {/* 제출 버튼 */}
                <button type="submit">회원가입</button>
            </form>

            {/* 로그인 안내 */}
            <div>
                이미 계정이 있으신가요? <a href="/login">로그인</a>
            </div>
        </div>
    );
}

export default SignUpPage;
