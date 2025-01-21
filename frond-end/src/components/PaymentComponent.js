import React from 'react';
import { useHistory } from 'react-router-dom';

const PaymentComponent = ({ paymentDetails }) => {
  const history = useHistory();

  const handlePayment = () => {
    const options = {
      key: 'your-razorpay-key', // Use your actual Razorpay key
      amount: paymentDetails.amount * 100, // Amount in paise
      currency: 'INR',
      name: 'Online Learning Platform',
      description: 'Course Payment',
      image: '/logo.png',
      order_id: paymentDetails.paymentId,
      handler: function (response) {
        // Handle payment success
        history.push('/payment-success', { paymentId: response.razorpay_payment_id });
      },
      prefill: {
        name: paymentDetails.userName,
        email: paymentDetails.userEmail,
      },
      notes: {
        user_id: paymentDetails.userId,
      },
    };

    const razorpay = new window.Razorpay(options);
    razorpay.open();
  };

  return (
    <div>
      <h3>Payment Details</h3>
      <p>Amount: ₹{paymentDetails.amount}</p>
      <button onClick={handlePayment}>Pay Now</button>
    </div>
  );
};

export default PaymentComponent;
