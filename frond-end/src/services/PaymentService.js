import axios from 'axios';

// Payment Service URL
const paymentServiceUrl = 'http://localhost:8083'; // Payment Service running on port 8083

export const createPaymentOrder = async (amount, userId) => {
  try {
    const response = await axios.post(`${paymentServiceUrl}/payment`, { amount, userId });
    return response.data;
  } catch (error) {
    console.error('Error creating payment order:', error);
  }
};
