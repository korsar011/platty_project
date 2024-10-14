
const getToken = () => {
    return localStorage.getItem('accessToken');
};

const isTokenExpired = (token) => {
    if (!token) return true;

    const payload = JSON.parse(atob(token.split('.')[1]));
    const expiryTime = payload.exp * 1000;
    return Date.now() >= expiryTime;
};


export { getToken, isTokenExpired };
