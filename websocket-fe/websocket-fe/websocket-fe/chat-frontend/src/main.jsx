import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import Home from './Home';
import Login from './Login';
import LoginSuccess from './LoginSuccess';
import PlayWithFriend from './PlayWithFriend';
import Game from './Game'; // Import the Game component

createRoot(document.getElementById('root')).render(
  <Router>
    <Routes>
      <Route path="/login" element={<Login></Login>}></Route>
      <Route path="/login-success" element={<LoginSuccess></LoginSuccess>}></Route>
      <Route path="/" element={<Home />} />
      <Route path="/game" element={<Game />} />
      <Route path="/play-with-friend" element={<PlayWithFriend />} />
    </Routes>
  </Router>
);
