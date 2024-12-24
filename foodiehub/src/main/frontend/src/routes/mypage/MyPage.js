// src/pages/MyPage/MyPage.js
import React, {useState, useEffect, use} from "react";
import { useNavigate } from "react-router-dom";
import {httpRequest} from "../../store/httpRequest";


const MyPage = () => {
    const [user, setUser] = useState({ nickname: "", email: "" });
    const navigate = useNavigate();

    // API 호출
    useEffect(() => {
        console.log("useEffect 실행됨");

        const fetchUserData = async () => {
            try {
                const data = await httpRequest("GET", "/api/mypage/profileinfo");
                setUser({ nickname: data.nickname, email: data.email });

            } catch (error) {
                console.error("GET /api/mypage/profileinfo 실패:", error);
            }
        };

        fetchUserData();
    }, []);



    return (
        <div>
            <p>닉네임 <b>{user.nickname}</b></p>
            <p>아이디 <b>{user.email}</b></p>
            <button type="button" onClick={() =>navigate("/mypage/edit")}>회원정보수정</button>
        </div>
    );
};

export default MyPage;
