import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Modal from '../components/Modal';
import DeckService from '../services/DeckService';
import ImageService from '../services/ImageService';
import './DeckPage.css';

const DeckPage = () => {
    const navigate = useNavigate();
    const [decks, setDecks] = useState([]);
    const [deckImages, setDeckImages] = useState({});
    const [loadingState, setLoadingState] = useState({});
    const [isEditing, setIsEditing] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [imageFile, setImageFile] = useState(null);
    const [imageUrl, setImageUrl] = useState('');
    const [deckName, setDeckName] = useState('');
    const [deckType, setDeckType] = useState('');
    const [deckDescription, setDeckDescription] = useState('');
    const [loading, setLoading] = useState(true);
    const [isImageUploading, setIsImageUploading] = useState(false);
    const [isCreatingDeck, setIsCreatingDeck] = useState(false); // New state for deck creation

    const loadDecks = async () => {
        try {
            const data = await DeckService.getAllDecks();
            setDecks(data);
            preloadDeckImages(data);
        } catch (error) {
            console.error('Error fetching decks:', error);
        }
    };

    const preloadDeckImages = (decks) => {
        const images = {};
        const loading = {};

        decks.forEach((deck) => {
            if (deck.image && deck.image.url) {
                loading[deck.id] = true;

                const img = new Image();
                img.src = deck.image.url;

                img.onload = () => {
                    images[deck.id] = deck.image.url;
                    setLoadingState((prev) => ({ ...prev, [deck.id]: false }));
                };

                img.onerror = () => {
                    images[deck.id] = '/default-image.jpg';
                    setLoadingState((prev) => ({ ...prev, [deck.id]: false }));
                };
            } else {
                images[deck.id] = '/default-image.jpg';
                loading[deck.id] = false;
            }
        });

        setDeckImages(images);
        setLoadingState(loading);
    };

    useEffect(() => {
        loadDecks().finally(() => setLoading(false));
    }, []);


    const handleDelete = async (id) => {
        try {
            await DeckService.deleteDeck(id);
            setDecks((prevDecks) => prevDecks.filter((deck) => deck.id !== id));
        } catch (error) {
            console.error('Error deleting deck:', error);
        }
    };

    const openModal = () => setIsModalOpen(true);
    const closeModal = () => {
        setIsModalOpen(false);
        resetForm();
    };

    const resetForm = () => {
        setImageFile(null);
        setImageUrl('');
        setDeckName('');
        setDeckType('');
        setDeckDescription('');
        document.getElementById('createDeckForm').reset();
    };

    const handleFileInputChange = (e, deckId) => {
        const file = e.target.files[0];
        if (file) {
            setImageFile(file);
            setImageUrl('');
            handleFileInputChangeForEdit(e, deckId);
        }
    };

    const handleImageUrlInput = (e) => {
        setImageUrl(e.target.value);
        if (e.target.value) {
            setImageFile(null);
        }
    };

    const isUrlInputDisabled = Boolean(imageFile);
    const isFileInputDisabled = Boolean(imageUrl);


    const handleCreateDeck = async () => {
        const createDeckRequest = {
            name: deckName || 'Deck',
            type: deckType || 'Type',
            description: deckDescription,
            imageUrl: '',
        };

        try {
            setIsCreatingDeck(true);

            if (imageFile) {
                setIsImageUploading(true);
                const newDeck = await DeckService.createDeck(createDeckRequest);
                const deckId = newDeck.id;

                const uploadedImageUrl = await ImageService.uploadDeckImage(imageFile, deckId);
                const updatedDeck = { ...newDeck, image: { url: uploadedImageUrl } };

                await DeckService.updateDeck(deckId, updatedDeck);

                setDeckImages((prevImages) => ({ ...prevImages, [deckId]: uploadedImageUrl }));
                setLoadingState((prev) => ({ ...prev, [deckId]: false }));
                setDecks((prevDecks) => [...prevDecks, updatedDeck]);

                setIsImageUploading(false);
            } else {
                const newDeck = await DeckService.createDeck(createDeckRequest);
                setDecks((prevDecks) => [...prevDecks, newDeck]);
            }

            closeModal();
        } catch (error) {
            console.error('Error creating deck:', error);
        } finally {
            setIsCreatingDeck(false); // Reset deck creation status
        }
    };

    const handleDeckNameChange = (deckId, newName) => {
        const updatedDeck = decks.find(deck => deck.id === deckId);
        updatedDeck.name = newName;

        setDecks(prevDecks => prevDecks.map(deck => (deck.id === deckId ? updatedDeck : deck)));
    };

    const handleDeckTypeChange = (deckId, newType) => {
        const updatedDeck = decks.find(deck => deck.id === deckId);
        updatedDeck.type = newType;

        setDecks(prevDecks => prevDecks.map(deck => (deck.id === deckId ? updatedDeck : deck)));
    };

    const saveDeckChanges = async (deckId) => {
        const updatedDeck = decks.find(deck => deck.id === deckId);
        try {
            await DeckService.updateDeck(deckId, updatedDeck);
        } catch (error) {
            console.error('Error updating deck:', error);
        }
    };

    const handleFileInputChangeForEdit = async (e, deckId) => {
        const file = e.target.files[0];
        if (file) {
            try {
                setIsImageUploading(true);
                const uploadedImageUrl = await ImageService.uploadDeckImage(file, deckId);
                const updatedDeck = { ...decks.find(deck => deck.id === deckId), image: { url: uploadedImageUrl } };

                await DeckService.updateDeck(deckId, updatedDeck);
                setDeckImages((prevImages) => ({ ...prevImages, [deckId]: uploadedImageUrl }));

                saveDeckChanges(deckId);
            } catch (error) {
                console.error('Error uploading image:', error);
            } finally {
                setIsImageUploading(false);
            }
        }
    };

    const handleDeckClick = (deckId) => {
        if (!isEditing) {
            navigate(`/decks/${deckId}`);
        }
    };
    const handleImageClick = (e, deckId) => {
        e.stopPropagation();
        if (isEditing) {
            document.getElementById(`image-upload-${deckId}`).click();
        } else {
            handleDeckClick(deckId);
        }
    };

    return (
        <section id="deck" className="deck section-show">
            {loading ? (
                <div className="loader-container">
                    <div className="loader"></div>
                </div>
            ) : (
                <div className="container">
                    <div className="section-title">
                        <h2>Decks</h2>
                        <p>Choose your deck</p>
                    </div>
                    <button className="edit-button" onClick={() => setIsEditing((prev) => !prev)}>
                        {isEditing ? 'Done' : 'Edit'}
                    </button>

                    <div className="row">
                        <div className="col-lg-3 col-md-6 d-flex align-items-stretch deck-item" onClick={openModal}>
                            <div className="icon-box add-deck-button">
                                <div className="icon">
                                    <i className="bx bx-plus">+</i>
                                </div>
                                <h4>Add Deck</h4>
                            </div>
                        </div>
                        {decks.length > 0 ? (
                            decks.map((deck) => (
                                <div key={deck.id} className="col-lg-3 col-md-6 d-flex align-items-stretch deck-item" onClick={() => handleDeckClick(deck.id)}>
                                    <div className={`icon-box ${isEditing ? 'show-upload-icon' : ''}`}>
                                        <img
                                            className="icon"
                                            src={deckImages[deck.id] || ''}
                                            alt={deck.name}
                                            style={{
                                                display: loadingState[deck.id] === false ? 'block' : 'none',
                                                filter: isEditing ? 'blur(3px)' : 'none',
                                            }}
                                            onClick={(e) => handleImageClick(e, deck.id)}
                                        />
                                        {loadingState[deck.id] === true && <div>Loading image...</div>}

                                        {isEditing && (
                                            <div
                                                className="upload-icon"
                                                style={{
                                                    position: 'absolute',
                                                    top: '30%',
                                                    left: '50%',
                                                    transform: 'translate(-50%, -50%)',
                                                    fontSize: '24px',
                                                    color: '#18d26e',
                                                    cursor: 'pointer'
                                                }}
                                                onClick={(e) => handleImageClick(e, deck.id)}
                                            >
                                                📤
                                            </div>
                                        )}

                                        {isEditing ? (
                                            <input
                                                type="text"
                                                value={deck.name}
                                                onChange={(e) => {
                                                    handleDeckNameChange(deck.id, e.target.value);
                                                    saveDeckChanges(deck.id);
                                                }}
                                                className="deck-edit-name"
                                            />
                                        ) : (
                                            <h4 onClick={() => handleDeckClick(deck.id)}>{deck.name}</h4>
                                        )}
                                        {isEditing ? (
                                            <input
                                                type="text"
                                                value={deck.type}
                                                onChange={(e) => {
                                                    handleDeckTypeChange(deck.id, e.target.value);
                                                }}
                                                onBlur={() => saveDeckChanges(deck.id)}
                                                className="deck-edit-type"
                                            />
                                        ) : (
                                            <p>{deck.type}</p>
                                        )}
                                        <button className="delete-button" onClick={(e) => { e.stopPropagation(); handleDelete(deck.id); }}>&times;</button>
                                        <input
                                            type="file"
                                            id={`image-upload-${deck.id}`}
                                            onChange={(e) => handleFileInputChange(e, deck.id)}
                                            style={{ display: 'none' }}
                                        />
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p>No decks available</p>
                        )}
                    </div>

                    <Modal className="modal-content" isOpen={isModalOpen} closeModal={closeModal}>
                        <span className="close-icon" onClick={closeModal}>&#10006;</span>
                        <h2>Create New Deck</h2>
                        <form id="createDeckForm">
                            <label htmlFor="name">Name:</label>
                            <input
                                type="text"
                                id="name"
                                name="name"
                                required
                                value={deckName}
                                onChange={(e) => setDeckName(e.target.value)}
                                placeholder="Deck"
                            />

                            <label htmlFor="type">Type:</label>
                            <input
                                type="text"
                                id="type"
                                name="type"
                                required
                                value={deckType}
                                onChange={(e) => setDeckType(e.target.value)}
                                placeholder="Type"
                            />

                            <label className="form-label">Choose a file or enter an image URL:</label>
                            <input
                                type="file"
                                className="form-control"
                                id="imageFile"
                                name="imageFile"
                                onChange={handleFileInputChange}
                                disabled={isFileInputDisabled}
                            />

                            <div className="or-container">
                                <span className="or-text">OR</span>
                            </div>

                            <label className="form-label">Enter an image link:</label>
                            <input
                                type="text"
                                className="form-control"
                                id="imageUrl"
                                name="imageUrl"
                                value={imageUrl}
                                onChange={handleImageUrlInput}
                                disabled={isUrlInputDisabled}
                            />

                            <label htmlFor="description">Description:</label>
                            <textarea
                                id="description"
                                name="description"
                                required
                                value={deckDescription}
                                onChange={(e) => setDeckDescription(e.target.value)}
                            ></textarea>

                            <button
                                type="button"
                                className="create-deck-button"
                                onClick={handleCreateDeck}
                                disabled={isImageUploading || isCreatingDeck} // Disable button during upload or creation
                            >
                                {isImageUploading ? 'Uploading image...' : isCreatingDeck ? 'Creating deck...' : 'Create Deck'}
                            </button>
                        </form>
                    </Modal>
                </div>
            )}
        </section>
    );
};

export default DeckPage;