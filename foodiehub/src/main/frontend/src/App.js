// src/App.js
import React,{useState,useEffect} from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Header from "./components/Header"; // 공통 헤더
import Home from "./Routes/Home/Home";
import Main from "./Routes/Main/Main";
import Login from "./Routes/Login/Login";
import MyPage from "./Routes/MyPage/MyPage";
import SignUp from "./Routes/SingUp/SignUp";
import SignUpAdmin from "./Routes/SignUpAdmin/SignUpAdmin";
import EditProfile from "./Routes/MyPage/EditProfile"

function App() {
    const [user, setUser] = useState({ username: "", role: "" }); // 사용자 상태 최상위에서 관리

    useEffect(() => {
        console.log("App.js의 현재 사용자 상태:", user);
    }, [user]); // 상태 변경 시 확인

    return (
      <Router>
          {/* 상태와 상태 변경 함수를 Header와 Main에 전달 */}
          <Header user={user} setUser={setUser} />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/main" element={<Main user={user} setUser={setUser} />} /> {/* Main에 상태 전달 */}
            <Route path="/login" element={<Login setUser={setUser} />} /> {/* Login에서도 상태 업데이트 가능 */}
            <Route path="/signup" element={<SignUp />} />
            <Route path="/signup_admin" element={<SignUpAdmin />} />
            <Route path="/mypage" element={<MyPage />} />
            <Route path="/mypage/edit" element={<EditProfile/>} />

        </Routes>
      </Router>
  );
}

export default App;
