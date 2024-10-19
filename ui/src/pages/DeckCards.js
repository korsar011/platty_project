import React, { useEffect, useState, useRef } from 'react';
import { useParams } from 'react-router-dom';
import CardService from '../services/CardService';
import ImageService from '../services/ImageService';
import AudioService from '../services/AudioService';
import Modal from '../components/Modal';
import './DeckCards.css';

const DeckCards = () => {
    const { deckId } = useParams();
    const [cards, setCards] = useState([]);
    const [cardImages, setCardImages] = useState({});
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [currentCardId, setCurrentCardId] = useState(null);
    const [mediaRecorder, setMediaRecorder] = useState(null);
    const [audioBlob, setAudioBlob] = useState(null);
    const audioChunks = useRef([]);
    const [editingCardId, setEditingCardId] = useState(null);
    const [editingField, setEditingField] = useState('');
    const [autoTranslateEnabled, setAutoTranslateEnabled] = useState(false);
    const [autoImageEnabled, setAutoImageEnabled] = useState(false);
    const [autoAudioGenerator, setAutoAudioGenerator] = useState(false);
    const [targetLanguage, setTargetLanguage] = useState('en');
    const [editingFieldValue, setEditingFieldValue] = useState('');

    const loadCards = async () => {
        try {
            const data = await CardService.getAllCardsByDeckId(deckId);
            setCards(data);
            loadCardImages(data);
        } catch (error) {
            console.error('Error fetching cards:', error);
        }
    };

    const loadCardImages = (cards) => {
        const images = {};
        cards.forEach((card) => {
            if (card.image && card.image.url) {
                images[card.id] = card.image.url;
            }
        });
        setCardImages(images);
    };

    useEffect(() => {
        loadCards();
    }, [deckId]);

    const handleDelete = async (id) => {
        try {
            await CardService.deleteCard(id);
            setCards((prevCards) => prevCards.filter((card) => card.id !== id));
        } catch (error) {
            console.error('Error deleting card:', error);
        }
    };

    const handleUpdateCard = async (id, updatedField) => {
        try {
            const updatedCard = await CardService.updateCard(id, updatedField);
            setCards((prevCards) =>
                prevCards.map((card) => (card.id === id ? { ...card, ...updatedField } : card))
            );

            if (autoImageEnabled && updatedField.frontText) {
                const imageRequest = {
                    text: updatedField.frontText,
                    cardId: id,
                };

                console.log("Sending image upload request:", imageRequest);

                try {
                    const uploadedImageUrl = await ImageService.uploadPremiumCardImageFromText(imageRequest);
                    console.log("Uploaded image URL:", uploadedImageUrl);

                    const updatedCardData = { "imageUrl": uploadedImageUrl };
                    await CardService.updateCard(id, updatedCardData);

                    setCards((prevCards) =>
                        prevCards.map((card) => (card.id === id ? { ...card, image: { url: uploadedImageUrl } } : card))
                    );

                    setCardImages((prevImages) => ({
                        ...prevImages,
                        [id]: uploadedImageUrl,
                    }));
                } catch (error) {
                    console.error("Error uploading image:", error);
                }
            }

            if (autoTranslateEnabled && updatedField.frontText) {
                const translateRequest = {
                    cardId: id,
                    targetLanguage: targetLanguage,
                };
                console.log("Sending translation request:", translateRequest);
                const translationResponse = await CardService.updateCardBackTextFromFrontTextTranslation(translateRequest);
                const backText = translationResponse.backText;

                setCards((prevCards) =>
                    prevCards.map((card) => (card.id === id ? { ...card, backText: backText } : card))
                );
                await CardService.updateCard(id, { backText: backText });
            }

            if (autoAudioGenerator && updatedField.frontText) {
                const audioRequest = {
                    text: updatedField.frontText,
                    cardId: id,
                };

                try {
                    const audioUrl = await AudioService.generateAudio(audioRequest);
                    setCards((prevCards) =>
                        prevCards.map((card) => (card.id === id ? { ...card, audioUrl: audioUrl } : card))
                    );
                } catch (error) {
                    console.error("Error generating audio:", error);
                }
            }
        } catch (error) {
            console.error('Error updating card:', error);
        }
    };

    const handleInputBlur = (id, field, value) => {
        handleUpdateCard(id, { [field]: value });
        stopEditing();
    };

    const handleKeyDown = (id, field, value, event) => {
        if (event.key === 'Enter') {
            handleUpdateCard(id, { [field]: value });
            stopEditing();
        }
    };

    const handleImageUpload = async (id, imageFile) => {
        try {
            const uploadedImageUrl = await ImageService.uploadCardImage(imageFile, id);
            await handleUpdateCard(id, { image: { url: uploadedImageUrl } });
            setCardImages((prevImages) => ({
                ...prevImages,
                [id]: uploadedImageUrl,
            }));
        } catch (error) {
            console.error('Error uploading image:', error);
        }
    };


    const handleCreateCard = async () => {
        try {
            const newCard = await CardService.createDefaultCard(deckId);
            setCards((prevCards) => [...prevCards, newCard]);

            if (newCard.image && newCard.image.url) {
                setCardImages((prevImages) => ({
                    ...prevImages,
                    [newCard.id]: newCard.image.url,
                }));
            }
        } catch (error) {
            console.error('Error creating card:', error);
        }
    };

    const handleOpenModal = (cardId) => {
        setCurrentCardId(cardId);
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setCurrentCardId(null);
        if (mediaRecorder) {
            mediaRecorder.stream.getTracks().forEach(track => track.stop());
        }
        setAudioBlob(null);
        audioChunks.current = [];
    };

    const startRecording = async () => {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
            alert("Your browser doesn't support audio recording.");
            return;
        }

        try {
            const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            const recorder = new MediaRecorder(stream);
            setMediaRecorder(recorder);
            audioChunks.current = [];

            recorder.ondataavailable = (event) => {
                audioChunks.current.push(event.data);
            };

            recorder.onstop = () => {
                const blob = new Blob(audioChunks.current, { type: 'audio/ogg; codecs=opus' });
                setAudioBlob(blob);
            };

            recorder.start();
        } catch (error) {
            console.error('Error accessing microphone:', error);
        }
    };

    const stopRecording = () => {
        if (mediaRecorder) {
            mediaRecorder.stop();
        }
    };

    const handleSaveAudio = async () => {
        if (audioBlob && currentCardId) {
            try {
                const audioDto = await AudioService.uploadAudio(currentCardId, audioBlob);
                console.log('Audio uploaded successfully:', audioDto);
            } catch (error) {
                console.error('Error uploading audio:', error);
            }
        }
        handleCloseModal();
    };

    const startEditing = (id, field) => {
        setEditingCardId(id);
        setEditingField(field);
    };

    const stopEditing = () => {
        setEditingCardId(null);
        setEditingField('');
    };

    const handleFrontTextChange = (cardId, newText) => {
        handleUpdateCard(cardId, { frontText: newText });
    };

    return (
        <section id="deck-cards" className="deck-cards section-show">
            <div className="container">
                <div className="section-title">
                    <h2>Cards</h2>
                    <p>Manage your cards</p>
                </div>
                <div className="premium-section">
                    <div className="premium-controls">
                        <label className="custom-checkbox">
                            <input
                                type="checkbox"
                                checked={autoTranslateEnabled}
                                onChange={(e) => setAutoTranslateEnabled(e.target.checked)}
                            />
                            <span className="checkmark"></span>
                            Autotranslate
                        </label>

                        {autoTranslateEnabled && (
                            <div className="target-language-selector">
                                <label className="dropdown-label">
                                    <label>
                                        <select
                                            value={targetLanguage}
                                            onChange={(e) => setTargetLanguage(e.target.value)}
                                        >
                                            <option value="en">English (EN)</option>
                                            <option value="de">German (DE)</option>
                                            <option value="ru">Russian (RU)</option>
                                        </select>
                                    </label>
                                </label>
                            </div>
                        )}

                        <label className="custom-checkbox">
                            <input
                                type="checkbox"
                                checked={autoImageEnabled}
                                onChange={(e) => setAutoImageEnabled(e.target.checked)}
                            />
                            <span className="checkmark"></span>
                            Autoimage
                        </label>

                        <label className="custom-checkbox">
                            <input
                                type="checkbox"
                                checked={autoAudioGenerator}
                                onChange={(e) => setAutoAudioGenerator(e.target.checked)}
                            />
                            <span className="checkmark"></span>
                            Autoaudio
                        </label>
                    </div>
                </div>

                <div className="row">
                    <div className="col-lg-3 col-md-6 d-flex align-items-stretch card-item">
                        <div className="icon-box add-card-button" onClick={handleCreateCard}>
                            <div className="icon">
                                <i className="bx bx-plus">+</i>
                            </div>
                            <h4>Add Card</h4>
                        </div>
                    </div>

                    {cards.length > 0 ? (
                        cards.map((card) => (
                            <div key={card.id} className="col-lg-3 col-md-6 d-flex align-items-stretch card-item">
                                <div className="icon-box">
                                    <button
                                        className="record-button"
                                        onClick={() => handleOpenModal(card.id)}
                                    >
                                        <i className="bx bx-microphone" style={{color: 'white', fontSize: '24px'}}></i>
                                    </button>

                                    {cardImages[card.id] && (
                                        <img
                                            className="icon"
                                            src={cardImages[card.id]}
                                            alt={card.frontText}
                                            onClick={() => document.getElementById(`file-input-${card.id}`).click()}
                                        />
                                    )}
                                    <input
                                        type="file"
                                        id={`file-input-${card.id}`}
                                        style={{display: 'none'}}
                                        onChange={(e) => handleImageUpload(card.id, e.target.files[0])}
                                    />

                                    <div className="card-details">
                                        {editingCardId === card.id && editingField === 'frontText' ? (
                                            <input
                                                type="text"
                                                className="front-text"
                                                value={editingFieldValue}
                                                onChange={(e) => setEditingFieldValue(e.target.value)}
                                                onBlur={() => handleInputBlur(card.id, 'frontText', editingFieldValue)}
                                                onKeyDown={(e) => handleKeyDown(card.id, 'frontText', editingFieldValue, e)}
                                                autoFocus
                                            />
                                        ) : (
                                            <h4 onClick={() => {
                                                setEditingFieldValue(card.frontText);
                                                startEditing(card.id, 'frontText');
                                            }}>
                                                {card.frontText}
                                            </h4>
                                        )}

                                        {editingCardId === card.id && editingField === 'backText' ? (
                                            <input
                                                type="text"
                                                className="back-text"
                                                value={card.backText}
                                                onChange={(e) =>
                                                    handleUpdateCard(card.id, {backText: e.target.value})
                                                }
                                                onBlur={stopEditing}
                                                autoFocus
                                            />
                                        ) : (
                                            <p onClick={() => startEditing(card.id, 'backText')}>
                                                {card.backText}
                                            </p>
                                        )}
                                    </div>

                                    <button
                                        className="delete-button"
                                        onClick={() => handleDelete(card.id)}
                                    >
                                        &times;
                                    </button>

                                    {card.audioUrl && (
                                        <audio controls>
                                            <source src={card.audioUrl} type="audio/ogg" />
                                            Your browser does not support the audio tag.
                                        </audio>
                                    )}
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>No cards available</p>
                    )}
                </div>
            </div>

            {isModalOpen && (
                <Modal isOpen={isModalOpen} closeModal={handleCloseModal}>
                    <div className="modal-content">
                        <h3>Audio Recorder</h3>
                        <button onClick={startRecording}
                                disabled={mediaRecorder && mediaRecorder.state === "recording"}>Start Recording
                        </button>
                        <button onClick={stopRecording}
                                disabled={mediaRecorder && mediaRecorder.state !== "recording"}>Stop Recording
                        </button>
                        <button onClick={handleCloseModal}>Close</button>
                        {audioBlob && (
                            <div>
                                <h4>Recorded Audio:</h4>
                                <audio controls src={URL.createObjectURL(audioBlob)}/>
                                <button onClick={handleSaveAudio}>Save Audio</button>
                            </div>
                        )}
                    </div>
                </Modal>
            )}
        </section>
    );
};

export default DeckCards;