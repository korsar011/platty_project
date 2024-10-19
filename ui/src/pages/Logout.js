import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { logoutUser } from "../services/AuthService";

const Logout = () => {
    const navigate = useNavigate();

    useEffect(() => {
        logoutUser();

        navigate('/login');
    }, [navigate]);

    return null;
};

export default Logout;
