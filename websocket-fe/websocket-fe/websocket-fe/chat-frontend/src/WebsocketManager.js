// WebSocketConnection.js

export function* createWebSocketConnectionGenerator() {
    try {
      const response = yield fetch('http://localhost:8080/create-websocket?type=game');
      if (!response.ok) {
        throw new Error('Failed to create WebSocket connection');
      }
  
      const data = yield response.json();
      const uid = data.uid;
  
      // Call connectToWebSocket with the UID
      yield connectToWebSocket(uid);
  
      return { success: true, uid };
    } catch (error) {
      console.error('Error in WebSocket connection process:', error);
      return { success: false, error: error.message };
    }
  }
  
  function connectToWebSocket(uid) {
    return new Promise((resolve, reject) => {
      const socket = new SockJS(`http://localhost:8080/ws/${uid}`);
      const stompClient = Stomp.over(socket);
  
      stompClient.connect({}, () => {
        console.log('Connected to WebSocket with UID:', uid);
        stompClient.subscribe(`/topic/move/${uid}`, (message) => {
          console.log('Received move:', message.body);
        });
        resolve();
      }, (error) => {
        reject(error);
      });
    });
  }
  
  export async function initializeWebSocketConnection() {
    const connectionGenerator = createWebSocketConnectionGenerator();
    let result = connectionGenerator.next();
  
    while (!result.done) {
      result = connectionGenerator.next(await result.value);
    }
  
    return result.value;
  }
  