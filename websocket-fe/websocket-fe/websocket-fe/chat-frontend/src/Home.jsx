import React from 'react';
import './App.css';

function Home() {
  const handleRedirectToGame = () => {
    window.location.href = 'http://localhost:5173/game';
  };

  const handleRedirectToPlayWithFriend = () => {
    window.location.href = 'http://localhost:5173/play-with-friend';
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Welcome to My React Vite App</h1>
        <button onClick={handleRedirectToGame} className="redirect-button">
          Go to Game
        </button>
        <button onClick={handleRedirectToPlayWithFriend} className="redirect-button">
          Play with a Friend
        </button>
      </header>
    </div>
  );
}

export default Home;
