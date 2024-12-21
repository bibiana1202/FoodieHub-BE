// src/App.js
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home/Home";
import MainPage from "./pages/MainPage/MainPage";
import LoginPage from "./pages/LoginPage/LoginPage";
import MyPage from "./pages/MyPage/MyPage";

function App() {
  return (
      <Router>
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/main" element={<MainPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/mypage" element={<MyPage />} />
        </Routes>
      </Router>
  );
}

export default App;
