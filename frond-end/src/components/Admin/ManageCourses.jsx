import React, { useEffect, useState } from "react";
import { getCourses, deleteCourse } from "../../utils/api";

const ManageCourses = () => {
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    const fetchCourses = async () => {
      const data = await getCourses();
      setCourses(data);
    };
    fetchCourses();
  }, []);

  const handleDelete = async (id) => {
    try {
      await deleteCourse(id);
      setCourses(courses.filter((course) => course.id !== id));
      alert("Course deleted successfully!");
    } catch (error) {
      alert("Failed to delete course!");
    }
  };

  return (
    <div>
      <h1>Manage Courses</h1>
      {courses.map((course) => (
        <div key={course.id}>
          <h3>{course.name}</h3>
          <button onClick={() => handleDelete(course.id)}>Delete</button>
        </div>
      ))}
    </div>
  );
};

export default ManageCourses;