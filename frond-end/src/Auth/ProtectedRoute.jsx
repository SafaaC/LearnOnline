// components/Auth/ProtectedRoute.jsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import { isAuthenticated, isAdmin } from '../../utils/auth';

const ProtectedRoute = ({ children }) => {
  if (!isAuthenticated()) {
    return <Navigate to="/login" />;
  }

  if (isAdmin()) {
    return children;  // Allow access to admin routes
  } else {
    return <Navigate to="/user/courses" />;
  }
};

export default ProtectedRoute;