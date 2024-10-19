import {getToken, isTokenExpired} from "./JwtService";

const AUTH_BASE_URL = 'http://localhost:8080/auth';

const registerUser = async (requestData) => {
    const response = await fetch(AUTH_BASE_URL + '/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData),
    });

    if (!response.ok) {
        throw new Error('Network response was not ok');
    }

    return await response.json();
};

const checkUsernameAvailability = async (username) => {
    const response = await fetch(`${AUTH_BASE_URL}/check-username/${username}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    if (!response.ok) {
        throw new Error('Network response was not ok');
    }

    const data = await response.json();
    console.log(data);
    return data;
};

const authenticateUser = async (credentials) => {
    const response = await fetch(AUTH_BASE_URL + '/token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
    });

    if (!response.ok) {
        throw new Error('Network response was not ok');
    }

    return await response.json();
};


const storeTokens = (authResponse) => {
    localStorage.setItem('accessToken', authResponse.accessToken);
    localStorage.setItem('refreshToken', authResponse.refreshToken);
    localStorage.setItem('accessTokenExpiry', authResponse.accessTokenExpiry);
    localStorage.setItem('refreshTokenExpiry', authResponse.refreshTokenExpiry);
};

const logoutUser = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('accessTokenExpiry');
    localStorage.removeItem('refreshTokenExpiry');
};

const isAuthenticated = () => {
    const token = getToken();
    return token && !isTokenExpired(token);
};


export { registerUser, authenticateUser, storeTokens, logoutUser, isAuthenticated, checkUsernameAvailability };
