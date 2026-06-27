import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './components/Auth/Login';
import CourseList from './components/User/CourseList';
import CreateCourse from './components/Admin/CreateCourse';
import ManageCourses from './components/Admin/ManageCourses';
import ProtectedRoute from './components/Auth/ProtectedRoute';
import Navbar from './components/Navbar';

const App = () => {
  return (
    <Router>
      <Navbar />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/user/courses" element={<CourseList />} />
        <Route
          path="/admin/create-course"
          element={
            <ProtectedRoute>
              <CreateCourse />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/manage-courses"
          element={
            <ProtectedRoute>
              <ManageCourses />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
};

export default App;