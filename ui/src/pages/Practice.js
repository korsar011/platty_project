import React, { useEffect, useState } from 'react';
import Card from '../components/Card';
import Statistics from '../components/Statistics';
import './Practice.css';
import CardService from '../services/CardService';
import DeckService from "../services/DeckService";

const Practice = () => {
    const [decks, setDecks] = useState([]);
    const [currentDeckId, setCurrentDeckId] = useState('master');
    const [totalElapsedTimeInSeconds, setTotalElapsedTimeInSeconds] = useState(0);
    const [deckStartTime, setDeckStartTime] = useState(null);
    const [cardsLearnt, setCardsLearnt] = useState(0);
    const [totalCards, setTotalCards] = useState(0);
    const [isDeckMastered, setIsDeckMastered] = useState(false);
    const [timer, setTimer] = useState(null);
    const [currentCard, setCurrentCard] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchAndDisplayDecks();
    }, []);

    useEffect(() => {
        drawNextCard(currentDeckId);
    }, [currentDeckId]);

    useEffect(() => {
        if (timer) {
            const id = setInterval(updateTotalElapsedTime, 1000);
            return () => clearInterval(id);
        }
    }, [timer]);

    const startTimer = () => {
        if (!timer) {
            setDeckStartTime(new Date());
            setTimer(true);
        }
    };

    const stopTimer = () => {
        clearInterval(timer);
        setTimer(null);
    };

    const updateTotalElapsedTime = () => {
        const currentTime = new Date();
        if (deckStartTime !== null) {
            const elapsedTime = Math.floor((currentTime - deckStartTime) / 1000);
            setTotalElapsedTimeInSeconds(elapsedTime);
        }
    };

    const formatElapsedTime = (seconds) => {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secs = seconds % 60;
        return `${hours < 10 ? '0' : ''}${hours}:${minutes < 10 ? '0' : ''}${minutes}:${secs < 10 ? '0' : ''}${secs}`;
    };

    const fetchAndDisplayDecks = async () => {
        try {
            const decksData = await DeckService.getAllDecks();

            const masterDeckElement = {
                id: 'master',
                name: 'Master Deck',
                imgSrc: '/assets/img/favicon.png',
                type: 'Master',
                size: decksData.reduce((sum, deck) => sum + deck.size, 0),
                cardsLearnt: decksData.reduce((sum, deck) => sum + deck.cardsLearnt, 0)
            };

            setDecks([masterDeckElement, ...decksData]);
            setCurrentDeckId(masterDeckElement.id);
            drawNextCard(masterDeckElement.id);
        } catch (error) {
            console.error('Error fetching and displaying decks:', error);
        }
    };

    const drawNextCard = async (deckId) => {
        setLoading(true);
        try {
            let cardData;
            console.log(`Fetching next card for deck: ${deckId}`);

            if (deckId === 'master') {
                cardData = await CardService.getNextCardFromAllUserDecks();

            } else {
                cardData = await CardService.getNextCardFromDeck(deckId);
                const deckInfo = await DeckService.getDeckInfo(deckId);
                setTotalCards(deckInfo.deckSize);
                setCardsLearnt(deckInfo.cardsLearnt);
            }


            console.log('Card data received:', cardData);

            if (cardData && cardData.frontText && cardData.backText) {
                setCurrentCard(cardData);
                return cardData;
            } else {
                console.error('No more cards available.');
                setCurrentCard(null);
                setIsDeckMastered(true);
                return null;
            }
        } catch (error) {
            console.error('Error fetching next card:', error);
            setCurrentCard(null);
            setIsDeckMastered(true);
            return null;
        } finally {
            setLoading(false);
        }
    };
    const handleDeckChange = async (deckId) => {
        console.log(`Switching to deck: ${deckId}`);

        setCurrentDeckId(deckId);
        setCardsLearnt(0);
        setTotalElapsedTimeInSeconds(0);
        stopTimer();
        setTotalCards(0);
        setIsDeckMastered(false);

        const card = await drawNextCard(deckId);

        if (!card) {
            console.error("No cards returned for the selected deck. Setting isDeckMastered to true.");
            setIsDeckMastered(true);
            stopTimer();
        }
    };

    const handleMemorized = async (choice) => {
        if (currentCard) {
            try {
                const userChoice = choice;

                console.log('Updating card rating with data:');
                console.log('Card ID:', currentCard.id);
                console.log('User Choice:', userChoice);

                await CardService.updateCardRating(currentCard.id, userChoice);
                console.log(`Rating for card ${currentCard.id} updated successfully.`);

                setCardsLearnt(prev => prev + 1);

                if (currentDeckId === 'master') {
                    await updateMasterDeckStats();
                }
            } catch (error) {
                console.error('Failed to update card rating:', error);
                alert('Failed to update card rating. Please try again.');
            }
        }

        const nextCard = await drawNextCard(currentDeckId);

        if (!nextCard) {
            setIsDeckMastered(true);
            stopTimer();
        }
    };

    const updateMasterDeckStats = async () => {
        try {
            const decksData = await DeckService.getAllDecks();
            const updatedMasterDeck = {
                id: 'master',
                name: 'Master Deck',
                imgSrc: '/assets/img/favicon.png',
                type: 'Master',
                size: decksData.reduce((sum, deck) => sum + deck.size, 0),
                cardsLearnt: decksData.reduce((sum, deck) => sum + deck.cardsLearnt, 0)
            };

            setDecks(prevDecks => prevDecks.map(deck => deck.id === 'master' ? updatedMasterDeck : deck));
        } catch (error) {
            console.error('Failed to update master deck stats:', error);
        }
    };

    const currentDeck = decks.find(deck => deck.id === currentDeckId);
    const deckName = currentDeck ? currentDeck.name : 'Unknown Deck';
    const deckType = currentDeck ? currentDeck.type : 'Unknown Type';
    const deckSize = currentDeck ? currentDeck.size : 0;
    const deckCardsLearnt = currentDeck ? currentDeck.cardsLearnt : 0;

    return (
        <section id="practice" className="practice section-show">
            <div className="container">
                <div className="section-title">
                    <h2>Practice</h2>
                    <p>Test your knowledge</p>
                </div>

                <div className="row">
                    <div className="col-md-6 d-flex justify-content-center">
                        <div className="cardrow d-flex justify-content-center">
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                {isDeckMastered ? (
                                    <p id="congratulations-message" className="congratulations-message">
                                        Congrats! <br /> You mastered the deck! <br /> Come back later!
                                    </p>
                                ) : (
                                    loading ? (
                                        <div className="loading">Загрузка следующей карточки...</div>
                                    ) : (
                                        currentCard ? (
                                            <Card
                                                cardId={currentCard.id}
                                                handleMemorized={() => handleMemorized('REMEMBER')}
                                                handleForgot={() => handleMemorized('FORGOT')}
                                                cardsLeft={totalCards - cardsLearnt}
                                                startTimer={startTimer}
                                                frontText={currentCard.frontText}
                                                backText={currentCard.backText}
                                                audioUrl={currentCard.audio?.url}
                                                imageUrl={currentCard.image?.url || 'https://default-image-url.com/default.jpg'}
                                            />
                                        ) : (
                                            <div className="loading">Карточек больше нет.</div>
                                        )
                                    )
                                )}
                            </div>
                        </div>
                    </div>

                    <div className="col-md-6 d-flex justify-content-center align-items-center">
                        <Statistics
                            deckName={deckName}
                            deckType={deckType}
                            cardsLearnt={deckCardsLearnt}
                            totalCards={deckSize}
                            totalTime={formatElapsedTime(totalElapsedTimeInSeconds)}
                        />
                    </div>
                </div>

                <div className="decks-quick-select-row">
                    {decks.map(deck => (
                        <div
                            key={deck.id}
                            className='deck-item-practice'
                            onClick={() => handleDeckChange(deck.id)}
                            data-deck-id={deck.id}
                        >
                            <div className="icon-box-practice">
                                <img
                                    style={{ objectFit: 'cover', width: '55px', height: '55px', borderRadius: '50%' }}
                                    src={deck.image?.url || 'https://cdn-icons-png.flaticon.com/512/3802/3802074.png'}
                                    alt={deck.name}
                                    onError={(e) => {
                                        e.target.onerror = null;
                                        e.target.src = 'https://cdn-icons-png.flaticon.com/512/3802/3802074.png';
                                    }}
                                />
                            </div>
                            <div className="deck-info-practice">
                                <h3>{deck.name}</h3>
                                <p>{deck.type}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
};

export default Practice;
