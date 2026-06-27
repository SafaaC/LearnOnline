// components/User/CourseList.jsx
import React, { useEffect, useState } from 'react';
import { getCourses } from '../../utils/api';

const CourseList = () => {
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    const fetchCourses = async () => {
      const data = await getCourses();
      setCourses(data);
    };
    fetchCourses();
  }, []);

  return (
    <div>
      <h2>Course List</h2>
      {courses.length === 0 ? (
        <p>No courses available at the moment. Please check back later.</p>
      ) : (
        <ul>
          {courses.map(course => (
            <li key={course.id}>
              {course.name}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default CourseList;