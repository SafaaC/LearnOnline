import React, { useState } from "react";
import { createCourse } from "../../utils/api";

const CreateCourse = () => {
  const [course, setCourse] = useState({ name: "", description: "" });
  const [error, setError] = useState("");  // Add error state

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await createCourse(course);
      alert("Course created successfully!");
      setCourse({ name: "", description: "" });
      setError("");  // Reset error on success
    } catch (error) {
      setError("Failed to create course!");  // Set error message
    }
  };

  return (
    <div>
      {/* Show error message if there's an error */}
      {error && <div className="error">{error}</div>}
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Course Name"
          value={course.name}
          onChange={(e) => setCourse({ ...course, name: e.target.value })}
        />
        <textarea
          placeholder="Course Description"
          value={course.description}
          onChange={(e) => setCourse({ ...course, description: e.target.value })}
        />
        <button type="submit">Create Course</button>
      </form>
    </div>
  );
};

export default CreateCourse;