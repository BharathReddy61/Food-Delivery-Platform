import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';
import { useAuth } from '../../context/AuthContext';

const Login = () => {

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const { login } = useAuth();

  const handleLogin = async (e: React.FormEvent) => {

    e.preventDefault();

    setError('');
    setLoading(true);

    try {

      const response = await api.post('/api/auth/login', {
        email,
        password
      });

      console.log('LOGIN RESPONSE:', response.data);

      /*
        Backend returns RAW JWT STRING.
        Example:
        eyJhbGciOiJIUzI1Ni...
      */

      const token = response.data;

      /*
        Decode JWT payload
      */

      const payload = JSON.parse(
        atob(token.split('.')[1])
      );

      console.log('JWT PAYLOAD:', payload);

      const user = {
        email: payload.sub,
        role: payload.role || 'CUSTOMER'
      };

      login(token, user);

      /*
        Role-based navigation
      */

      if (user.role === 'ADMIN') {

        navigate('/admin/dashboard');

      } else if (user.role === 'RESTAURANT_OWNER') {

        navigate('/owner/dashboard');

      } else if (user.role === 'DELIVERY_PARTNER') {

        navigate('/delivery/dashboard');

      } else {

        navigate('/restaurants');
      }

    } catch (err: any) {

      console.error('LOGIN ERROR:', err);

      setError(
        err.response?.data?.message ||
        err.message ||
        'Login failed. Please check your credentials.'
      );

    } finally {

      setLoading(false);
    }
  };

  return (

    <div className="max-w-md mx-auto mt-16 bg-white p-8 rounded-lg shadow-sm border border-gray-200">

      <h2 className="text-2xl font-bold text-gray-900 mb-6 text-center">
        Login
      </h2>

      {error && (
        <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm mb-4">
          {error}
        </div>
      )}

      <form onSubmit={handleLogin} className="space-y-4">

        <div>

          <label className="block text-sm font-medium text-gray-700 mb-1">
            Email
          </label>

          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

        </div>

        <div>

          <label className="block text-sm font-medium text-gray-700 mb-1">
            Password
          </label>

          <input
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-md transition-colors disabled:opacity-50"
        >
          {loading ? 'Logging in...' : 'Sign In'}
        </button>

      </form>

    </div>
  );
};

export default Login;