import React, { useEffect, useState } from 'react';
import { getCourses } from '../services/apiService'; // Make sure this path is correct

const CourseList = () => {
  const [courses, setCourses] = useState([]);
  const [error, setError] = useState(null); // Add an error state to track API errors

  useEffect(() => {
    getCourses()
      .then((data) => {
        console.log(data); // Log the courses data to inspect the structure
        setCourses(data);
      })
      .catch((error) => {
        console.log('Error fetching courses:', error);
        setError('Failed to fetch courses. Please try again later.');
      });
  }, []);

  if (error) {
    return <div>{error}</div>; // Display the error message if there was an error
  }

  return (
    <div>
      <h2>Courses</h2>
      {courses.length === 0 ? (
        <p>No courses available.</p>
      ) : (
        <ul>
          {courses.map((course) => (
            <li key={course.id}>
              {course.title} - {course.description} {/* Use title instead of name */}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default CourseList;