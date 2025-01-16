import React, { useEffect, useState } from 'react';
import { getCourses } from '../services/apiService';

const CourseList = () => {
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    getCourses().then((data) => setCourses(data));
  }, []);

  return (
    <div>
      <h2>Courses</h2>
      <ul>
        {courses.map((course) => (
          <li key={course.id}>{course.name} - {course.description}</li>
        ))}
      </ul>
    </div>
  );
};

export default CourseList;