import './init';
import React, { useState, useEffect, useRef } from 'react';
import ChessBoard from 'chessboardjsx';
import { Chess } from 'chess.js';
import { useNavigate, useLocation } from 'react-router-dom'; 

async function getBotMove(fen) {
  const response = await fetch('http://localhost:8080/bot-move', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ fen }),  // Send current FEN to bot
  });
  const data = await response.json();
  return data.move;  // The bot's move (e.g., "e2e4")
}

const BotGame = () => {
  const [fen, setFen] = useState('start');
  const [color, setColor] = useState('white');
  const [text, setText] = useState('');
  const game = useRef(null);
  const navigate = useNavigate();
  const { search } = useLocation();
  
  const gameId = new URLSearchParams(search).get('gameId');
  
  useEffect(() => {
    game.current = new Chess();

    // Initialize the game (choose color)
    if (!gameId) {
      setColor('white');  // Player starts as white
    }

    // Start the bot move cycle if it's the bot's turn
    if (color === 'white') {
      setText("Your turn (White)");
    } else {
      setText("Bot's turn (Black)");
      makeBotMove();
    }
  }, [color]);  // Re-run when color changes

  const makeBotMove = async () => {
    const botMove = await getBotMove(game.current.fen()); // Get bot move based on current FEN
    game.current.ugly_move(game.current.move(botMove));  // Apply the move to the game
    setFen(game.current.fen());  // Update the board
    setText("Your turn (White)");  // Now it's the player's turn
  };

  const onDrop = ({ sourceSquare, targetSquare }) => {
    if (
      (game.current.turn() === 'b' && color === 'black') ||
      (game.current.turn() === 'w' && color === 'white')
    ) {
      const move = game.current.move({
        from: sourceSquare,
        to: targetSquare,
      });
      setFen(game.current.fen());

      if (move) {
        setText("Bot's turn (Black)");  // After player's move, bot should move
        makeBotMove();
      } else {
        setText('Invalid move, try again');
      }
    }
  };

  return (
    <>
      <div className="info">
        <h2>You play {color}</h2>
        {text === '' ? (
          game.current?.turn() === 'b' ? (
            <h2>Black's turn</h2>
          ) : (
            <h2>White's turn</h2>
          )
        ) : (
          <h2>{text}</h2>
        )}
      </div>

      <div className="App">
        <ChessBoard position={fen} onDrop={onDrop} orientation={color === 'white' ? 'white' : 'black'} />
      </div>
    </>
  );
};

export default BotGame;
