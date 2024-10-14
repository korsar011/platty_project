import React, { useState, useEffect, useRef } from 'react';
import { Link, NavLink, useLocation } from 'react-router-dom';
import './Header.css';
import UserService from "../services/UserService";
import {FaUserCircle} from "react-icons/fa";

function Header() {
    const location = useLocation();
    const [dropdownVisible, setDropdownVisible] = useState(false);
    const [profileImage, setProfileImage] = useState(null);
    const dropdownRef = useRef(null);

    const fetchProfileImage = async () => {
        try {
            const profile = await UserService.getProfile();
            setProfileImage(profile.imageUrl);
        } catch (error) {
            console.error('Failed to fetch profile image', error);
        }
    };

    useEffect(() => {
        fetchProfileImage(); // Initial fetch

        const handleProfileUpdate = () => {
            fetchProfileImage();
        };

        window.addEventListener('profileUpdated', handleProfileUpdate);

        return () => {
            window.removeEventListener('profileUpdated', handleProfileUpdate);
        };
    }, []);

    const handleClickOutside = (event) => {
        if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
            setDropdownVisible(false);
        }
    };

    useEffect(() => {
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const toggleDropdown = () => {
        setDropdownVisible((prevState) => !prevState);
    };

    const hiddenPaths = ['/login', '/register'];

    if (hiddenPaths.includes(location.pathname)) {
        return null;
    }

    const navLinks = (
        <nav id="navbar" className="navbar">
            <ul>
                <li><NavLink to="/" className="nav-link">Home</NavLink></li>
                <li><NavLink to="/practice" className="nav-link">Practice</NavLink></li>
                <li><NavLink to="/decks" className="nav-link">Decks</NavLink></li>
                <li><NavLink to="/collections" className="nav-link">Collection</NavLink></li>
            </ul>
        </nav>
    );

    return (
        <header id="header" className={location.pathname === '/' ? 'header-centered' : 'header-top'}>
            <div className="container">
                <h1><NavLink to="/">Platty</NavLink></h1>
                {navLinks}
            </div>
            <div className="user-menu" ref={dropdownRef}>
                {profileImage ? (
                    <img
                        src={profileImage}
                        alt="User Avatar"
                        className="user-avatar"
                        onClick={toggleDropdown}
                    />
                ) : (
                    <FaUserCircle className="user-avatar" onClick={toggleDropdown} />
                )}
                {dropdownVisible && (
                    <div className="dropdown-menu">
                        <Link className="dropdown-item" to="/profile">My Profile</Link>
                        <Link className="dropdown-item" to="/contact">Contact</Link>
                        <Link className="dropdown-item" to="/logout">Logout</Link>
                    </div>
                )}
            </div>
        </header>
    );
}

export default Header;
