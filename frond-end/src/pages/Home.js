import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div>
      <h1>Welcome to the Online Learning Platform</h1>
      <p>Explore and enroll in the best courses.</p>
      <nav>
        <Link to="/login">Login</Link>
        <br />
        <Link to="/courses">View Courses</Link>
      </nav>
    </div>
  );
};

export default Home;