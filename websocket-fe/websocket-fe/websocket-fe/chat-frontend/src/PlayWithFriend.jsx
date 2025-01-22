import React, { useState } from 'react';
import './App.css';

function PlayWithFriend() {
  const [gameId, setGameId] = useState(''); // Store the inputted game UID
  const [errorMessage, setErrorMessage] = useState(''); // For error handling

  const handleInputChange = (event) => {
    setGameId(event.target.value);
  };

  const handleJoinGame = () => {
    if (!gameId) {
      setErrorMessage('Please enter a valid game UID');
      return;
    }

    // Redirect to the game page with the UID (gameId)
    window.location.href = `http://localhost:5173/game?gameId=${gameId}`;
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Play with a Friend</h1>
        <input
          type="text"
          value={gameId}
          onChange={handleInputChange}
          placeholder="Enter existing game UID"
          className="friend-input"
        />
        <button onClick={handleJoinGame} className="start-game-button">
          Join Game
        </button>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
      </header>
    </div>
  );
}

export default PlayWithFriend;
