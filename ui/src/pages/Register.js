import React, { useEffect, useState, useCallback } from 'react';
import { registerUser, authenticateUser, storeTokens, checkUsernameAvailability } from '../services/AuthService';
import { isAuthenticated } from '../services/AuthService';
import debounce from 'lodash.debounce'; // Add debounce to reduce API calls.

const Register = () => {
    const [isRegistering, setIsRegistering] = useState(false);
    const [username, setUsername] = useState('');
    const [usernameAvailable, setUsernameAvailable] = useState(true);
    const [usernameChecked, setUsernameChecked] = useState(false); // New state to handle when a check is done.

    useEffect(() => {
        if (isAuthenticated()) {
            window.location.href = '/';
        }
    }, []);

    const checkUsername = useCallback(
        debounce(async (newUsername) => {
            if (newUsername) {
                console.log('Проверяю юзернейм:', newUsername); // Логируем проверку имени пользователя.
                try {
                    const isAvailable = await checkUsernameAvailability(newUsername);
                    console.log("Ну что там? " + isAvailable)
                    setUsernameAvailable(isAvailable);
                    setUsernameChecked(true); // Устанавливаем статус после проверки.
                    console.log(isAvailable ? 'Username is available.' : 'Username is already taken.'); // Логируем результат проверки.
                } catch (error) {
                    console.error('Error checking username availability:', error);
                    setUsernameAvailable(false);
                    setUsernameChecked(true); // Устанавливаем статус после неудачной проверки.
                    console.log('Произошла ошибка, но статус изменен на usernameChecked = true.');
                }
            } else {
                setUsernameAvailable(true);
                setUsernameChecked(false); // Устанавливаем статус, если поле пустое.
                console.log('Поле юзернейм пустое, сброс состояния.');
            }
        }, 300),
        []
    );

    const handleUsernameChange = (event) => {
        const newUsername = event.target.value;
        setUsername(newUsername);
        setUsernameChecked(false); // Отключаем статус во время ввода.
        console.log('Отключаю статус на время ввода, usernameChecked = false.');
        checkUsername(newUsername); // Проверяем имя пользователя с задержкой.
    };

    const submitForm = async (event) => {
        event.preventDefault();

        // Ensure username is checked before submitting.
        if (!usernameChecked) {
            alert('Please wait until username availability is confirmed.');
            console.log('Пожалуйста, подождите, пока будет подтверждена доступность юзернейма.');
            return;
        }

        if (!usernameAvailable) {
            alert('Username is already taken. Please choose another one.');
            console.log('Юзернейм занят. Выберите другой.');
            return;
        }

        setIsRegistering(true);
        console.log('Регистрация началась.');
        const form = event.target;
        const formData = new FormData(form);
        const requestData = {};
        formData.forEach((value, key) => {
            requestData[key] = value;
        });

        try {
            const registrationResponse = await registerUser(requestData);
            const authRequest = {
                username: requestData.username,
                password: requestData.password,
            };
            const authResponse = await authenticateUser(authRequest);
            storeTokens(authResponse);
            console.log('Регистрация успешна, перенаправление на главную страницу.');
            window.location.href = '/';
        } catch (error) {
            console.error('Ошибка при регистрации:', error);
            if (error.response) {
                console.error('Данные ответа:', error.response.data);
                console.error('Статус ответа:', error.response.status);
                if (error.response.status === 409) {
                    alert('This username is already taken. Please choose another one.');
                    console.log('Юзернейм уже занят, статус 409.');
                } else {
                    alert('Registration failed. Please try again.');
                    console.log('Регистрация не удалась. Повторите попытку.');
                }
            } else {
                alert('Registration failed. Please try again.');
                console.log('Ошибка при регистрации без ответа сервера.');
            }
        } finally {
            setIsRegistering(false);
            console.log('Сброс состояния регистрации, isRegistering = false.');
        }
    };

    return (
        <section className="bg-light min-vh-100 top-0 align-items-center section-show">
            <div className="container-fluid">
                <div className="row g-0">
                    <div className="col-12 col-md-6">
                        <img
                            className="img-fluid h-100 object-fit-cover"
                            loading="lazy"
                            src="big-logo.jpg"
                            alt="Welcome to our platform!"
                        />
                    </div>
                    <div className="col-12 col-md-6 d-flex align-items-center justify-content-center">
                        <div className="col-12 col-lg-10 col-xl-8">
                            <div className="card border-light-subtle shadow-sm p-4">
                                <h4 className="text-center mb-4">Join us today!</h4>
                                <form id="registrationForm" onSubmit={submitForm}>
                                    <div className="row gy-3">
                                        <div className="col-12">
                                            <div className="form-floating mb-3">
                                                <input
                                                    type="text"
                                                    className="form-control"
                                                    name="username"
                                                    id="username"
                                                    placeholder="Username"
                                                    required
                                                    value={username}
                                                    onChange={handleUsernameChange}
                                                />
                                                <label htmlFor="username">Username</label>
                                            </div>
                                            {/* Показать сообщение, если имя пользователя занято */}
                                            {!usernameAvailable && usernameChecked && (
                                                <div style={{ color: 'red' }}>Username is already taken.</div>
                                            )}
                                        </div>
                                        <div className="col-12">
                                            <div className="form-floating mb-3">
                                                <input
                                                    type="password"
                                                    className="form-control"
                                                    name="password"
                                                    id="password"
                                                    placeholder="Password"
                                                    required
                                                />
                                                <label htmlFor="password">Password</label>
                                            </div>
                                        </div>
                                        <div className="col-12">
                                            <div className="d-grid">
                                                <button className="btn btn-dark btn-lg" type="submit" disabled={isRegistering || !usernameChecked}>
                                                    {isRegistering ? 'Registering...' : 'Register now'}
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                                <div className="row">
                                    <div className="col-12">
                                        <div className="d-flex gap-2 gap-md-4 flex-column flex-md-row justify-content-md-center mt-5">
                                            <a href="/login" className="link-secondary text-decoration-none">Already have an account? Sign in</a>
                                            <a href="#!" className="link-secondary text-decoration-none">Forgot password</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
};

export default Register;
