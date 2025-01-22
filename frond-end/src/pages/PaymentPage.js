import React, { useState } from 'react';
import axios from 'axios';

const PaymentPage = () => {
  const [amount, setAmount] = useState(0);
  const [courseId, setCourseId] = useState(null);

  // Function to initialize Razorpay payment
  const handlePayment = async () => {
    try {
      const response = await axios.post('http://localhost:8080/payment', { amount });
      const { orderId, currency, paymentId } = response.data;

      const options = {
        key: 'your-razorpay-key-id', // Replace with your Razorpay Key ID
        amount: amount * 100, // Amount in paisa (100 is equivalent to 1 INR)
        currency: currency,
        name: 'Online Learning Platform',
        description: `Payment for Course ID: ${courseId}`,
        order_id: orderId,
        handler: function (response) {
          alert(`Payment successful! Payment ID: ${response.razorpay_payment_id}`);
          // Handle successful payment logic here, like updating payment status
        },
        prefill: {
          name: 'John Doe',
          email: 'john.doe@example.com',
          contact: '9999999999',
        },
        theme: {
          color: '#F37254',
        },
      };

      const paymentObject = new window.Razorpay(options);
      paymentObject.open();
    } catch (error) {
      console.error('Payment initialization failed:', error);
    }
  };

  return (
    <div>
      <h2>Course Payment</h2>
      <div>
        <label>Enter Course ID:</label>
        <input
          type="text"
          value={courseId}
          onChange={(e) => setCourseId(e.target.value)}
        />
      </div>
      <div>
        <label>Enter Amount (INR):</label>
        <input
          type="number"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />
      </div>
      <button onClick={handlePayment}>Pay Now</button>
    </div>
  );
};

export default PaymentPage;