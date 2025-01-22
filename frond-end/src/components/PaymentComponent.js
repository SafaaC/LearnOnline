import { useNavigate } from 'react-router-dom';

function PaymentComponent() {
  const navigate = useNavigate();

  const handlePaymentSuccess = () => {
    navigate('/success');
  };

  return (
    <div>
      {/* Payment form */}
      <button onClick={handlePaymentSuccess}>Pay Now</button>
    </div>
  );
}

export default PaymentComponent;