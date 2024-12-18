function checkNickname() {
    const nickname = document.getElementById("nickname").value; // 입력된 닉네임 가져오기
    const resultDiv = document.getElementById("nickname-check-result");

    if (!nickname) {
        resultDiv.innerText = "닉네임을 입력해주세요.";
        return;
    }

    // 서버에 Ajax 요청 보내기
    fetch(`/check-nickname?nickname=${nickname}`)
        .then(response => response.json())
        .then(data => {
            if (data.isDuplicated) {
                resultDiv.innerText = "이미 사용 중인 닉네임입니다.";
            } else {
                resultDiv.style.color = "green";
                resultDiv.innerText = "사용 가능한 닉네임입니다.";
            }
        })
        .catch(error => {
            console.error("Error:", error);
            resultDiv.innerText = "닉네임 확인 중 오류가 발생했습니다.";
        });
}
