import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getCourseById } from '../services/ApiService';
import PaymentComponent from '../components/PaymentComponent';

const CoursePage = () => {
  const { id } = useParams();
  const [course, setCourse] = useState(null);

  useEffect(() => {
    const fetchCourse = async () => {
      const courseData = await getCourseById(id);
      setCourse(courseData);
    };
    fetchCourse();
  }, [id]);

  if (!course) return <div>Loading...</div>;

  return (
    <div>
      <h2>{course.name}</h2>
      <p>{course.description}</p>
      <PaymentComponent paymentDetails={{ amount: course.price, paymentId: course.id, userId: 'user123', userName: 'John Doe', userEmail: 'john.doe@example.com' }} />
    </div>
  );
};

export default CoursePage;

