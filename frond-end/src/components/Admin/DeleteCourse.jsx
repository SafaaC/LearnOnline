import React from "react";

const DeleteCourse = ({ course, onDelete, onCancel }) => {
  return (
    <div>
      <h3>Are you sure you want to delete {course.name}?</h3>
      <button onClick={() => onDelete(course.id)}>Yes, Delete</button>
      <button onClick={onCancel}>Cancel</button>
    </div>
  );
};

export default DeleteCourse;