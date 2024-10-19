import React, { useState } from 'react';
import './Card.css';

const Card = ({
                  cardId,
                  handleMemorized,
                  cardsLeft,
                  startTimer,
                  frontText,
                  backText,
                  audioUrl,
                  imageUrl
              }) => {
    const [isFlipped, setIsFlipped] = useState(false);
    const [animationClass, setAnimationClass] = useState('');

    const toggleFlip = () => {
        setIsFlipped(prev => !prev);
        if (!isFlipped) startTimer();
    };

    const handleAnimation = (animationType, callback) => {
        setAnimationClass(animationType);
        setTimeout(() => {
            setAnimationClass('');
            callback();
        }, animationType === 'pop-animation' ? 500 : 250);
    };

    const handleMemorizedClick = (event) => {
        event.preventDefault();
        event.stopPropagation();

        if (isFlipped) {
            handleAnimation('pop-animation', () => {
                toggleFlip();
                setTimeout(() => {
                    handleMemorized();
                }, 300);
            });
        } else {
            handleMemorized();
        }
    };

    const handleRepeatClick = event => {
        event.preventDefault();
        event.stopPropagation();
        handleAnimation('shake-animation', toggleFlip);
    };

    const handleSoundButtonClick = event => {
        event.stopPropagation();
        if (audioUrl) {
            const audio = new Audio(audioUrl);
            audio.play().catch(error => console.error('Error playing audio:', error));
        }
    };
    console.log('Image URL:', imageUrl);

    return (
        <div className='cardrow justify-content-center'>
            <div className={`card ${isFlipped ? 'is-flipped' : ''} ${animationClass}`} onClick={toggleFlip}>
                <div className="cover item-a">
                    <h1 id="frontText">{frontText}</h1>
                    <div
                        className="card-back"
                        style={{ backgroundImage: `url(${imageUrl})` }}
                    >
                        <h2 id="backText">{backText}</h2>
                        <div className="button-container">
                            <button className="repeat-btn" onClick={handleRepeatClick}>Повторить</button>
                            <button className="memorized-btn" onClick={handleMemorizedClick}>Помню</button>
                        </div>
                        {audioUrl ? (
                            <div className="sound-container" onClick={handleSoundButtonClick}>
                                <span id="sound-icon" className="sound">🔊</span>
                            </div>
                        ) : null}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Card;
