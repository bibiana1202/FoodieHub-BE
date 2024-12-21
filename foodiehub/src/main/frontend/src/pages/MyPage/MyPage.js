// src/pages/MyPage/MyPage.js
import React, { useState, useEffect } from "react";
import axios from "axios";
import Header from "./Header";
import Tabs from "./Tabs";
import List from "./List";

const MyPage = () => {
    const [activeTab, setActiveTab] = useState("찜 목록");
    const [favorites, setFavorites] = useState([]);
    const [reviews, setReviews] = useState([]);

    // API 호출
    useEffect(() => {
        if (activeTab === "찜 목록") {
            // 찜 목록 데이터 가져오기
            axios.get("/mypage/favorites")
                .then((response) => {
                    setFavorites(response.data);
                })
                .catch((error) => {
                    console.error("찜 목록 데이터를 가져오는 중 오류 발생:", error);
                });
        } else {
            // 내 평가 데이터 가져오기
            axios.get("/mypage/reviews")
                .then((response) => {
                    setReviews(response.data);
                })
                .catch((error) => {
                    console.error("내 평가 데이터를 가져오는 중 오류 발생:", error);
                });
        }
    }, [activeTab]);

    // 현재 탭에 따른 데이터 선택
    const items = activeTab === "찜 목록" ? favorites : reviews;

    return (
        <div>
            <Header nickname="닉네임" id="아이디" />
            <Tabs activeTab={activeTab} setActiveTab={setActiveTab} />
            <List items={items} />
        </div>
    );
};

export default MyPage;
