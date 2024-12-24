// src/pages/MyPage/UserInfo.js
import React from "react";

const UserInfo = ({ nickname, id }) => {
    return (
        <div className="header">
            <div className="profile">
                <img
                    src="/img/cherry.png"
                    alt="프로필 이미지"
                    className="profile-img"
                    width={50}
                    height={50}
                />
                <div className="profile-info">
                    <p>{nickname || "닉네임"}</p>
                    <p>{id || "아이디"}</p>
                </div>
            </div>
            <button className="edit-btn">회원정보 수정</button>
        </div>
    );
};

export default UserInfo;
