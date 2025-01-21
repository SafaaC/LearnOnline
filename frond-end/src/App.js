import React from 'react';
import { Route, Routes } from 'react-router-dom';
import HomePage from './pages/HomePage';
import CoursePage from './pages/CoursePage';
import PaymentPage from './pages/PaymentPage';
import Header from './components/Header';
import Footer from './components/Footer';

const App = () => {
  return (
    <div className="App">
      <Header />
      <div className="content">
        <Routes>
          <Route path="/" exact component={HomePage} />
          <Route path="/course/:id" component={CoursePage} />
          <Route path="/payment" component={PaymentPage} />
        </Routes>
      </div>
      <Footer />
    </div>
  );
};

export default App;

