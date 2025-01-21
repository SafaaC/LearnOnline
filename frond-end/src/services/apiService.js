import axios from 'axios';

// API Gateway URL
const apiUrl = 'http://localhost:8080'; // API Gateway running on port 8080

export const getCourses = async () => {
  try {
    const response = await axios.get(`${apiUrl}/courses`);
    return response.data;
  } catch (error) {
    console.error('Error fetching courses:', error);
  }
};

export const getCourseById = async (id) => {
  try {
    const response = await axios.get(`${apiUrl}/courses/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching course:', error);
  }
};
