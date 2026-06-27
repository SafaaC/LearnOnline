import React, { useState, useEffect } from 'react';
import { getCourses, deleteCourse } from '../api';
import { Link } from 'react-router-dom';

const Courses = () => {
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    fetchCourses();
  }, []);

  const fetchCourses = async () => {
    const response = await getCourses();
    setCourses(response.data);
  };

  const handleDelete = async (id) => {
    await deleteCourse(id);
    fetchCourses(); // Refresh the list
  };

  return (
    <div>
      <h1>Courses</h1>
      <Link to="/courses/new">Add New Course</Link>
      <ul>
        {courses.map((course) => (
          <li key={course.id}>
            <Link to={`/course/${course.id}`}>{course.title}</Link>
            <button onClick={() => handleDelete(course.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Courses;