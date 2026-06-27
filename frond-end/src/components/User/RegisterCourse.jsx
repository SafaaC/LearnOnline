import React from "react";
import { useParams } from "react-router-dom";
import { registerCourse } from "../../utils/api";

const RegisterCourse = () => {
  const { id } = useParams();

  const handleRegister = async () => {
    try {
      await registerCourse(id);
      alert("Registered successfully!");
    } catch (error) {
      alert("Registration failed!");
    }
  };

  return (
    <button onClick={handleRegister}>Register for this Course</button>
  );
};

export default RegisterCourse;