import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { httpRequest } from "../../utils/httpRequest"; // httpRequest를 불러옴
import useTokenStorage from "../../utils/useTokenStorage"; // useTokenStorage를 불러옴

function Main({user}) {

    useEffect(() => {
        console.log("Main에서 사용자 상태 확인:", user);
    }, [user]); // 상태 변경 시 렌더링 확인


    // URL에서 토큰을 저장하는 훅 호출
    useTokenStorage();


    return (
        <div>
            <h1>메인 페이지 테스트 페이지 !!!</h1>

            {/* 조건부 렌더링: 사용자 정보 */}
            {user.username ? (
                <div>
                    <p>사용자 이름: <b>{user.username}</b></p>
                    <p>사용자 권한: <b>{user.role}</b></p>
                    {user.role === "ROLE_ADMIN" && <p>관리자 권한이 있습니다.</p>}
                </div>
            ) : (
                <div>
                    <p>로그인하지 않은 사용자만 볼 수 있습니다.</p>
                </div>
            )}
        </div>
    );
}

export default Main;
