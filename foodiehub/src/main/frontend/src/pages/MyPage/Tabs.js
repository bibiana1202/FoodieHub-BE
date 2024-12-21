// src/pages/MyPage/Tabs.js
import React from "react";

const Tabs = ({ activeTab, setActiveTab }) => {
    return (
        <div className="tabs">
            <button
                className={activeTab === "찜 목록" ? "active" : ""}
                onClick={() => setActiveTab("찜 목록")}
            >
                찜 목록 (N)
            </button>
            <button
                className={activeTab === "내 평가" ? "active" : ""}
                onClick={() => setActiveTab("내 평가")}
            >
                내 평가 (N)
            </button>
        </div>
    );
};

export default Tabs;
