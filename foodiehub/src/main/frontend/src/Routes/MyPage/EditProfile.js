import React, { useState } from "react";

const EditProfile = () => {
    const [profile, setProfile] = useState({
        nickname: "홍길동",
        email: "user@example.com",
        phone: "010-1234-5678",
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProfile({
            ...profile,
            [name]: value, // 동적으로 필드 업데이트
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log("수정된 회원정보:", profile);
        alert("회원정보가 저장되었습니다!");
        // 서버로 데이터를 전송하려면 아래 주석을 참고
        // fetch('/api/update-profile', {
        //     method: 'POST',
        //     headers: { 'Content-Type': 'application/json' },
        //     body: JSON.stringify(profile),
        // });
    };

    return (
        <div >
            <h2>회원정보 수정</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>닉네임</label>
                    <input
                        type="text"
                        name="nickname"
                        value={profile.nickname}
                        onChange={handleChange}
                    />
                </div>
                <div>
                    <label>이메일</label>
                    <input
                        type="email"
                        name="email"
                        value={profile.email}
                        onChange={handleChange}
                    />
                </div>
                <div>
                    <label>전화번호</label>
                    <input
                        type="text"
                        name="phone"
                        value={profile.phone}
                        onChange={handleChange}
                    />
                </div>
                <button type="submit">저장</button>
            </form>
        </div>
    );
};

export default EditProfile;
