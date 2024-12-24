// src/components/Header.js
import React, {useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import {httpRequest} from "../store/httpRequest";

const Header = ({user,setUser}) => {
    // const [user, setUser] = useState({ username: "", role: "" });
    const navigate = useNavigate();

    useEffect(() => {
        console.log("Header에서 사용자 상태 확인:", user);
    }, [user]);

    // 네비게이션 함수
    const handleNavigate = (path) => {
        navigate(path);
    };

    useEffect(() => {
        console.log("useEffect 실행됨");

        const fetchUserData = async () => {
            try {
                const data = await httpRequest("GET", "/api/header/user");
                setUser({ username: data.username, email:data.email, role: data.role });
                console.log("사용자 상태 업데이트(header):", { username: data.username, role: data.role,email:data.email}); // 상태 업데이트 확인

            } catch (error) {
                console.error("GET /api/header/user 실패:", error);
            }
        };

        fetchUserData();
    }, []);

    const handleLogout = async () => {
        try {
            const response = await httpRequest("POST", "/api/auth/logout");

            // 로컬 스토리지 및 상태 초기화
            localStorage.removeItem("access_token");
            deleteCookie("oauth2_auth_request");
            setUser({ username: "", role: "" }); // 사용자 상태 초기화
            navigate("/main");
        } catch (error) {
            alert("로그아웃에 실패했습니다.");
        }
    };
    const deleteCookie = (name) => {
        document.cookie = `${name}=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 GMT;`;
    };

    return (
        <header  style ={{background: "grey"}}>
            <div onClick={() => handleNavigate("/main")}>
                <img src="/img/foodieHub.png" // 로고 이미지 경로
                     alt="foodieHub" width={50} height={50}/>
            </div>
            <nav>
                <button onClick={() => handleNavigate("/")}>홈</button>

                {user.username && user.role.split("_").pop() === "USER" &&
                    <button onClick={() => handleNavigate("/mypage")}>마이페이지</button>
                }

                {user.username && user.role === "ROLE_ADMIN" &&
                    <button onClick={() => handleNavigate("/mypage")}>관리자마이페이지</button>}


                {user.username &&
                    <button type="button" id="logout-btn" onClick={handleLogout}>로그아웃</button>}


                {!user.username &&
                    <button onClick={() => handleNavigate("/login")}>로그인</button>}
            </nav>
        </header>
    );
};

export default Header;
