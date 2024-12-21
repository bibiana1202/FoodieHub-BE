export async function httpRequest(method, url, body = null) {
    const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('access_token')}`,
    };

    // 로그아웃 시 Authorization 헤더 제외
    if (url === '/logout') {
        delete headers.Authorization;
    }

    const options = {
        method,
        headers,
        credentials: 'include', // 쿠키 포함
        body: body ? JSON.stringify(body) : null,
    };

    try {
        const response = await fetch(url, options);

        if (response.ok) {
            return await response.json();
        }

        // 토큰 만료 시 갱신 처리
        if (response.status === 401 && getCookie('refresh_token')) {
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
                headers.Authorization = `Bearer ${accessToken}`;
                const retryResponse = await fetch(url, options);
                if (retryResponse.ok) {
                    return await retryResponse.json();
                }
            }
        }

        throw new Error(`HTTP 요청 실패: ${response.status}`);
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
