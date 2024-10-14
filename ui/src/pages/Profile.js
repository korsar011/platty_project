import React, { useState, useEffect } from 'react';
import './Profile.css';
import UserService from '../services/UserService';
import CountryService from '../services/CountryService';
import Select from 'react-select';
import { FaUpload } from 'react-icons/fa';

const Profile = () => {
    const [isEditing, setIsEditing] = useState(false);
    const [profile, setProfile] = useState(null);
    const [countries, setCountries] = useState([]);
    const [languages, setLanguages] = useState([]);
    const [selectedFile, setSelectedFile] = useState(null);
    const [usernameAvailable, setUsernameAvailable] = useState(true);

    useEffect(() => {
        const fetchProfileAndData = async () => {
            const fetchedProfile = await UserService.getProfile();
            setProfile(fetchedProfile);

            const countryList = await CountryService.fetchCountries();
            setCountries(countryList);

            const languageList = await CountryService.fetchLanguages();
            setLanguages(languageList.map(lang => ({ value: lang, label: lang })));
        };

        fetchProfileAndData();
    }, []);

    const toggleEdit = () => {
        setIsEditing(!isEditing);
    };

    const handleChange = async (e) => {
        const { id, value, type } = e.target;

        if (id === 'username-input') {
            setProfile((prevProfile) => ({
                ...prevProfile,
                username: value,
            }));

            if (value === profile.username) {
                setUsernameAvailable(true);
            } else {
                const isAvailable = await UserService.checkUsernameAvailability(value);
                setUsernameAvailable(isAvailable);
            }
        } else if (type === 'file') {
            const file = e.target.files[0];
            const imageUrl = URL.createObjectURL(file);
            setProfile((prevProfile) => ({
                ...prevProfile,
                imageUrl,
            }));
            setSelectedFile(file);
        } else {
            setProfile((prevProfile) => ({
                ...prevProfile,
                [id.replace('-input', '').replace('-select', '')]: value,
            }));
        }
    };

    const uploadProfileImage = async (file, userId) => {
        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await UserService.uploadProfileImage(formData, userId);
            setProfile((prevProfile) => ({
                ...prevProfile,
                imageUrl: response.url,
            }));
            return true;
        } catch (error) {
            console.error('Error uploading profile image:', error);
            return false;
        }
    };

    const handleLanguagesChange = (selectedOptions) => {
        const selectedLanguages = selectedOptions.map(option => option.value);
        setProfile((prevProfile) => ({
            ...prevProfile,
            languages: selectedLanguages,
        }));
    };

    const handleSave = async () => {
        let uploadSuccess = true;

        if (!usernameAvailable) {
            alert('Username is already taken. Please choose another one.');
            return;
        }

        if (selectedFile) {
            uploadSuccess = await uploadProfileImage(selectedFile, profile.id);
        }

        if (uploadSuccess) {
            try {
                await UserService.saveProfile(profile);
                window.dispatchEvent(new Event('profileUpdated'));
                toggleEdit();
            } catch (error) {
                if (error.response?.status === 409) {
                    alert('Conflict: This username is already taken. Please choose another one.');
                } else {
                    console.error('Error saving profile:', error);
                    alert('An unexpected error occurred while saving your profile. Please try again.');
                }
            }
        }
    };


    if (!profile) {
        return <div>Loading...</div>;
    }

    return (
        <section id="about" className="about section-show">
            <div className="about-me container">
                <div className="edit-save-buttons">
                    <button id="editProfileBtn" onClick={toggleEdit}>
                        {isEditing ? 'Cancel' : 'Edit'}
                    </button>
                    {isEditing && (
                        <button id="saveProfileBtn" onClick={handleSave}>
                            <i className="icon-ok" aria-hidden="true"></i> Save
                        </button>
                    )}
                </div>

                <div className="section-title">
                    <h2>Profile</h2>
                    <p>About you</p>
                </div>

                <div className="row">
                    <div className="col-lg-4" data-aos="fade-right">
                        <div className={`image-container ${isEditing ? 'edit-mode' : ''}`}>
                            <img
                                src={profile.imageUrl || 'me.jpg'}
                                className="img-fluid"
                                alt="Profile"
                            />
                            {isEditing && (
                                <label htmlFor="image-upload-input" className="upload-icon">
                                    <input
                                        type="file"
                                        accept="image/*"
                                        onChange={handleChange}
                                        id="image-upload-input"
                                        style={{ display: 'none' }}
                                    />
                                    <FaUpload className="upload-icon" />
                                </label>
                            )}
                            {isEditing && <span className="upload-caption">Upload a new photo</span>} {/* Caption below the icon */}
                        </div>
                    </div>

                    <div id="profile-info" className="col-lg-8 pt-4 pt-lg-0 content" data-aos="fade-left">
                        <h3>
                            {isEditing ? (
                                <>
                                    <input
                                        type="text"
                                        id="username-input"
                                        value={profile.username}
                                        onChange={handleChange}
                                    />
                                    {!usernameAvailable && <span style={{ color: 'red' }}>Username is already taken</span>}
                                </>
                            ) : (
                                <span id="username-text">{profile.username}</span>
                            )}
                        </h3>
                        <div className="row">
                            <div className="col-lg-6">
                                <ul>
                                    <li>
                                        <strong>First Name:</strong>
                                        {isEditing ? (
                                            <input
                                                type="text"
                                                id="firstName-input"
                                                value={profile.firstName}
                                                onChange={handleChange}
                                            />
                                        ) : (
                                            <span id="first-name-text">{profile.firstName}</span>
                                        )}
                                    </li>
                                    <li>
                                        <strong>Last Name:</strong>
                                        {isEditing ? (
                                            <input
                                                type="text"
                                                id="lastName-input"
                                                value={profile.lastName}
                                                onChange={handleChange}
                                            />
                                        ) : (
                                            <span id="last-name-text">{profile.lastName}</span>
                                        )}
                                    </li>
                                    <li>
                                        <strong>Birthday:</strong>
                                        {isEditing ? (
                                            <input
                                                type="date"
                                                id="birthday-input"
                                                value={profile.birthday}
                                                onChange={handleChange}
                                            />
                                        ) : (
                                            <span id="birthday-text">{profile.birthday}</span>
                                        )}
                                    </li>
                                </ul>
                            </div>
                            <div className="col-lg-6">
                                <ul>
                                    <li>
                                        <strong>Email:</strong>
                                        {isEditing ? (
                                            <input
                                                type="email"
                                                id="email-input"
                                                value={profile.email}
                                                onChange={handleChange}
                                            />
                                        ) : (
                                            <span id="email-text">{profile.email}</span>
                                        )}
                                    </li>
                                    <li>
                                        <strong>Languages:</strong>
                                        {isEditing ? (
                                            <Select
                                                id="languages-select"
                                                isMulti
                                                options={languages}
                                                value={languages.filter(language => profile.languages.includes(language.value))}
                                                onChange={handleLanguagesChange}
                                                classNamePrefix="react-select"
                                            />
                                        ) : (
                                            <span id="languages-text">{profile.languages.join(', ')}</span>
                                        )}
                                    </li>
                                    <li>
                                        <strong>Country:</strong>
                                        {isEditing ? (
                                            <select
                                                id="country-select"
                                                value={profile.country}
                                                onChange={handleChange}
                                            >
                                                {countries.map((country) => (
                                                    <option key={country.code} value={country.name}>
                                                        {country.name}
                                                    </option>
                                                ))}
                                            </select>
                                        ) : (
                                            <span id="country-text">{profile.country}</span>
                                        )}
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
};

export default Profile;