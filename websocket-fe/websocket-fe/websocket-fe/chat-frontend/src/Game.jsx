import './init';
import React, { useState, useEffect, useRef } from 'react';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import ChessBoard from 'chessboardjsx';
import { Chess } from 'chess.js';
import { useNavigate, useLocation } from 'react-router-dom'; // Import useNavigate and useLocation

async function createWebSocketConnection(gameId) {
  const response = await fetch(`http://localhost:8080/create-websocket?gameId=${gameId}`);
  const data = await response.json();
  const uid = data.uid;  // Retrieve the generated UID from the response
  return uid;
}

const Game = () => {
  const [stompClient, setStompClient] = useState(null);
  const [fen, setFen] = useState('start');
  const [color, setColor] = useState('');
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState('');
  const [redirecting, setRedirecting] = useState(false); // New state to track redirection
  let game = useRef(null);
  const navigate = useNavigate(); // Initialize the navigate function
  const { search } = useLocation(); // Access query parameters from URL

  const gameId = new URLSearchParams(search).get('gameId'); // Extract gameId from URL query

  useEffect(() => {
    async function initGame() {
      let currentGameId = gameId;

      // If no gameId is provided, create a new game by calling the `create-websocket` endpoint
      if (!currentGameId) {
        const response = await fetch('http://localhost:8080/create-websocket?type=game');
        const data = await response.json();
        currentGameId = data.uid;  // Use the returned UID as the gameId
      }

      // Connect using the gameId
      const socket = new SockJS(`http://localhost:8080/ws?gameId=${currentGameId}`);
      const client = Stomp.over(socket);
      game.current = new Chess();

      client.connect({}, () => {
        // Subscribe to notifications
        client.subscribe('/topic/notifications', (notification) => {
          if (notification) {
            // Check if the notification contains the error message
            if (notification.body.includes("Maximum user limit reached") && !redirecting) {
              setText("Error: Maximum user limit reached");
              setRedirecting(true); // Set the redirecting flag
              client.disconnect(); // Disconnect WebSocket immediately before navigating
              navigate('/error'); // Redirect to the error page
            } else {
              setColor('white');
              console.log(notification.body);
            }
          }
        });

        if (color !== 'white') {
          setColor('black');
        }

        console.log("ALO! " + currentGameId )

        client.subscribe(`/topic/move/${currentGameId}`, (move) => {
          console.log("Move received:", move.body); // Check if the message is received
          const chessMove = JSON.parse(move.body);
          try {
            game.current.move({
              from: chessMove.sourceSquare,
              to: chessMove.targetSquare,
            });
            setFen(game.current.fen());
          } catch {
            console.error('Invalid move received from server');
          }
        });
      });

      setStompClient(client);
    }

    initGame();

    return () => {
      if (stompClient && stompClient.connected) {
        stompClient.disconnect(); // Disconnect WebSocket when component unmounts
      }
    };
  }, []); 

  function onDrop({ sourceSquare, targetSquare }) {
    if (
      (game.current.turn() === 'b' && color === 'black') ||
      (game.current.turn() === 'w' && color === 'white')
    ) {
      const chessMove = {
        sourceSquare,
        targetSquare,
      }; 
     stompClient.send(`/app/chess/${gameId}`, {}, JSON.stringify(chessMove));

    }
  }

  return (
    <>
      <div className="messages-list">
        {messages.map((msg, index) => (
          <div key={index} className="message-item">
            <div className="message-text">
              <p>{msg}</p>
            </div>
          </div>
        ))}
      </div>

      {game.current?.isGameOver() ? (
        <div className="header">
          {game.current?.turn() === 'b' ? <h1>White won!</h1> : <h1>Black won!</h1>}
        </div>
      ) : (
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
      )}

      <div className="App">
        <ChessBoard position={fen} onDrop={onDrop} orientation={color === 'white' ? 'white' : 'black'} />
      </div>
    </>
  );
};

export default Game;
