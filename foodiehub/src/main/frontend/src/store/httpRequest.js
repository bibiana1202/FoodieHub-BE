export async function httpRequest(method, url, body = null) {
    const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('access_token')}`,
    };

    // 로그아웃 시 Authorization 헤더 제외
    if (url === '/api/auth/logout') {
        console.log('Deleting Authorization UserInfo for Logout Request');
        delete headers.Authorization;
    }

    const options = {
        method,
        headers,
        credentials: 'include', // 쿠키 포함
        body: body ? JSON.stringify(body) : null,
    };

    try {
        // 요청 전 디버깅 로그
        console.log(`HTTP 요청 시작: ${method} ${url}`);
        console.log('요청 옵션:', options);

        const response = await fetch(url, options);

        // 응답 디버깅 로그
        console.log(`HTTP 응답: ${response.status}`);
        console.log('응답 헤더:', response.headers); // 헤더 확인

        // 디버깅 로그 추가
        console.log(`HTTP 요청: ${method} ${url}, 상태: ${response.status}`);
        console.log('Response 객체:', response); // 서버 응답 전체 출력
        console.log('Response 상태:', response.ok); // 응답 상태 확인

        if (response.ok) {
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            return response.text(); // JSON이 아니면 텍스트로 처리
        }

        // 토큰 만료 시 갱신 처리
        if (response.status === 401 && getCookie('refresh_token')) {
            console.log('토큰 만료, 새 토큰 발급 시도');
            const refreshResponse = await fetch('/api/token', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ refreshToken: getCookie('refresh_token') }),
            });

            if (refreshResponse.ok) {
                const { accessToken } = await refreshResponse.json();
                localStorage.setItem('access_token', accessToken);

                // 새 토큰으로 재요청
                // 새로 생성한 헤더와 옵션으로 재요청
                const retryOptions = {
                    ...options,
                    headers: {
                        ...headers,
                        Authorization: `Bearer ${accessToken}`,
                    },
                };
                const retryResponse = await fetch(url, retryOptions);
                if (retryResponse.ok) {
                    return await retryResponse.json();
                }
            }
        }
        // 에러 처리 추가
        const errorData = await response.json(); // JSON 응답 파싱
        console.error(`HTTP 요청 실패: ${response.status}`, errorData);
        throw { status: response.status, response: errorData }; // 에러 객체를 throw


        if (!response.ok) {
            const errorData = await response.json(); // JSON 응답 파싱
            console.error(`HTTP 요청 실패: ${response.status}`, errorData);
            throw { status: response.status, response: errorData };
        }

    } catch (error) {
        console.error('HTTP 요청 중 오류 발생:', error);
        throw error;
    }
}

// 쿠키 가져오는 함수
function getCookie(key) {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [k, v] = cookie.trim().split('=');
        if (k === key) return v;
    }
    return null;
}

