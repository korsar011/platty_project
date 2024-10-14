import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Profile from './pages/Profile';
import Practice from './pages/Practice';
import CollectionsPage from './pages/CollectionsPage';
import Header from './components/Header';
import DeckPage from './pages/DeckPage';
import Login from './pages/Login';
import Register from './pages/Register';
import './App.css';
import Logout from './pages/Logout';
import AuthGuard from './security/AuthGuard';
import DeckCards from './pages/DeckCards';

function App() {
    return (
        <Router>
            <Header />
            <Routes>
                <Route path="/" element={<AuthGuard>null</AuthGuard>} />
                <Route path="/profile" element={<AuthGuard><Profile /></AuthGuard>} />
                <Route path="/practice" element={<AuthGuard><Practice /></AuthGuard>} />
                <Route path="/decks" element={<AuthGuard><DeckPage /></AuthGuard>} />
                <Route path="/collections" element={<AuthGuard><CollectionsPage /></AuthGuard>} />
                <Route path="/decks/:deckId" element={<AuthGuard><DeckCards /></AuthGuard>} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/logout" element={<Logout />} />
            </Routes>
        </Router>
    );
}

export default App;
