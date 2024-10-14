import React from 'react';
import './Statistics.css'; // Import the CSS styles for this component

const Statistics = ({ deckName, deckType, cardsLearnt, totalCards, totalTime }) => {
    return (
        <div id="total-time-container">
            <ul>
                <li><strong>Deck Name</strong>: <span id="deck-name">{deckName}</span></li>
                <li><strong>Deck Type</strong>: <span id="deck-type">{deckType}</span></li>
                <li><strong>Cards Learnt</strong>: <span id="cards-learnt">{cardsLearnt} /</span> <span id="cards-total">{totalCards}</span></li>
                <li><strong>Total Time Spent</strong>: <span id="total-time">{totalTime} seconds</span></li>
            </ul>
        </div>
    );
};

export default Statistics;
