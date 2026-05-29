import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const RequireAuth = () => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
};

export const RequireRole = ({ allowedRoles }: { allowedRoles: string[] }) => {
  const { user } = useAuth();

  // If user role doesn't match, safely redirect to the base path
  // The backend remains the final authority if they try to bypass this
  if (!user || !allowedRoles.includes(user.role)) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
};
