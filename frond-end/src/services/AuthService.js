import axios from 'axios';

// User Service URL
const userServiceUrl = 'http://localhost:8081'; // User Service running on port 8081

export const authenticateUser = async (username, password) => {
  try {
    const response = await axios.post(`${userServiceUrl}/auth/login`, { username, password });
    return response.data;
  } catch (error) {
    console.error('Error authenticating user:', error);
  }
};
