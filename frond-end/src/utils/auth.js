// Store the JWT token in localStorage
export const saveToken = (token) => {
  localStorage.setItem('token', token);
};

// Get the JWT token from localStorage
export const getToken = () => {
  return localStorage.getItem('token');
};

// Remove the JWT token from localStorage
export const removeToken = () => {
  localStorage.removeItem('token');
};

// Check if the user is authenticated (i.e., if token exists)
export const isAuthenticated = () => {
  const token = getToken();
  return token !== null;
};

// Check if the user is an admin
export const isAdmin = () => {
  const token = getToken();
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      // Check if the roles array exists and contains 'ROLE_ADMIN'
      return payload.roles && payload.roles.includes('ROLE_ADMIN');
    } catch (error) {
      console.error('Error decoding token', error);
      return false;
    }
  }
  return false;
};