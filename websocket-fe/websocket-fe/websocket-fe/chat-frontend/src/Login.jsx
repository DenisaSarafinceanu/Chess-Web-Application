import React from 'react';

const Login = () => {

  const handleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  }

  return (
    <>
      <button onClick={handleLogin}>Sign in with Google</button>
    </>
  );
}

export default Login;
