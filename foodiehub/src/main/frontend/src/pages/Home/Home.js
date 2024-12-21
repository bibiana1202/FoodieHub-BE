// src/pages/Home/Home.js
import React from "react";
import { Link } from "react-router-dom";

const Home = () => {
    return (
        <div>
            <h1>홈페이지</h1>
            <p>React Router를 이용해 페이지를 전환합니다.</p>
            <br/>
            <Link to="/home">
                <button>홈페이지로 이동</button>
            </Link>
            <br/>
            <Link to="/login">
                <button>로그인페이지로 이동</button>
            </Link>
            <br/>
            <Link to="/main">
                <button>메인페이지로 이동</button>
            </Link>
            <br/>
            <Link to="/mypage">
                <button>마이페이지로 이동</button>
            </Link>
            <br/>
        </div>
    );
};

export default Home;