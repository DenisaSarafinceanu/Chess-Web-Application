import React, { useEffect, useState } from "react";

// Function to refresh access token using the refresh token stored in HttpOnly cookies
const refreshAccessToken = async () => {
  const response = await fetch("http://localhost:8080/refresh-token", {
    method: "POST",
    credentials: "include", // Include cookies in the request
  });

  if (!response.ok) {
    throw new Error("Failed to refresh access token.");
  }

  //return response.json(); // The backend should set the new access token as a cookie
};

const LoginSuccess = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [error, setError] = useState(null);
  const [isRefreshing, setIsRefreshing] = useState(false);  // State to track token refresh status

  useEffect(() => {
    // Function to fetch user info
    const fetchUserInfo = async () => {
      let response = await fetch("http://localhost:8080/api/user", {
        method: "GET",
        credentials: "include", // Include cookies in the request
      });

      // If the access token has expired (status 401), refresh it
      if (response.status === 401) {
        if (isRefreshing) {
          // If a refresh is already in progress, wait until it finishes
          await new Promise((resolve) => {
            const interval = setInterval(async () => {
              response = await fetch("http://localhost:8080/api/user", {
                method: "GET",
                credentials: "include",
              });

              if (response.status !== 401) {
                clearInterval(interval);
                resolve();
              }
            }, 1000); // Check every second until refresh completes
          });
        } else {
          try {
            setIsRefreshing(true);  // Set to true to prevent multiple refresh attempts
            await refreshAccessToken();  // Refresh token (cookies will be updated automatically)
            setIsRefreshing(false);  // Reset the refresh flag
            return fetchUserInfo(); // Retry fetching user info after refresh
          } catch (error) {
            //setError("Unable to refresh token. Please log in again.");
            console.log(error);
            setIsRefreshing(false);  // Reset the refresh flag on error
            return; // Don't proceed further in case of error
          }
        }
      }

      if (!response.ok) {
        throw new Error("Failed to fetch user data.");
      }

      const data = await response.json();
      setUserInfo(data);
    };

    // Fetch user info initially
    fetchUserInfo().catch((error) => {
      setError(error.message);
    });
  }, [isRefreshing]);  // Trigger re-fetch when the refresh state changes

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (!userInfo) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h1>Welcome, {userInfo.principal}!</h1>
    </div>
  );
};

export default LoginSuccess;
