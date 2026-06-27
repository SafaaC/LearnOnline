import React from 'react';
import { Link } from 'react-router-dom';
import { isAuthenticated, isAdmin, removeToken } from '../utils/auth';

const Navbar = () => {
  const handleLogout = () => {
    removeToken();
    window.location.href = '/login';  // Redirect to login page after logout
  };

  return (
    <nav>
      <ul>
        {isAuthenticated() ? (
          <>
            {isAdmin() && (
              <div>
                <span>Admin Logged In</span> {/* Indicator for admin login */}
                <Link to="/admin/create-course">Create Course</Link>
                <Link to="/admin/manage-courses">Manage Courses</Link>
              </div>
            )}
            <Link to="/user/courses">Courses</Link>
            <button onClick={handleLogout}>Logout</button>
          </>
        ) : (
          <Link to="/login">Login</Link>
        )}
      </ul>
    </nav>
  );
};

export default Navbar;