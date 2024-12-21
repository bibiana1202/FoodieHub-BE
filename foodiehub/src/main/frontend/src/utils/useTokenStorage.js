import { useEffect } from 'react';

function useTokenStorage() {
    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');

        if (token) {
            localStorage.setItem('access_token', token);
            console.log('Access token 저장 완료:', token);
        }
    }, []);
}

export default useTokenStorage;
