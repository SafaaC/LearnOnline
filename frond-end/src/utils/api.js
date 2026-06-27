import axios from "axios";
import { getToken } from './auth';

const API_BASE_URL = "http://localhost:8080";

// Authentication API
export const login = async (credentials) => {
  const response = await axios.post(`${API_BASE_URL}/auth/login`, credentials);
  return response.data;
};

// Course APIs
export const getCourses = async () => {
  const response = await axios.get(`${API_BASE_URL}/courses`);
  return response.data;
};

export const getCourseById = async (id) => {
  const response = await axios.get(`${API_BASE_URL}/courses/${id}`);
  return response.data;
};

export const createCourse = async (course) => {
  const token = getToken();
  const response = await axios.post(`${API_BASE_URL}/courses`, course, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;
};

export const deleteCourse = async (id) => {
  const token = getToken();
  await axios.delete(`${API_BASE_URL}/courses/${id}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
};

// Registration API
export const registerCourse = async (id) => {
  const response = await axios.post(`${API_BASE_URL}/courses/${id}/register`);
  return response.data;
};