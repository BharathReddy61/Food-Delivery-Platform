import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="text-2xl font-bold tracking-tight text-blue-600">
              Crave<span className="text-gray-900">Delivery</span>
            </Link>
          </div>
          
          <div className="flex items-center space-x-6">
            {isAuthenticated ? (
              <>
                {user?.role === 'CUSTOMER' && (
                  <>
                    <Link to="/restaurants" className="text-gray-600 hover:text-blue-600 font-medium transition-colors">Restaurants</Link>
                    <Link to="/cart" className="text-gray-600 hover:text-blue-600 font-medium transition-colors">Cart</Link>
                    <Link to="/orders" className="text-gray-600 hover:text-blue-600 font-medium transition-colors">Orders</Link>
                  </>
                )}
                
                {user?.role === 'ADMIN' && (
                  <Link to="/admin/dashboard" className="text-red-600 font-medium hover:text-red-800 transition-colors">Admin Area</Link>
                )}

                {user?.role === 'RESTAURANT_OWNER' && (
                  <>
                    <Link to="/owner/dashboard" className="text-green-700 hover:text-green-900 font-medium transition-colors">Dashboard</Link>
                    <Link to="/owner/orders" className="text-green-700 hover:text-green-900 font-medium transition-colors">Orders</Link>
                    <Link to="/owner/menu" className="text-green-700 hover:text-green-900 font-medium transition-colors">Menu</Link>
                  </>
                )}

                {user?.role === 'DELIVERY_PARTNER' && (
                  <Link to="/delivery/dashboard" className="text-purple-700 hover:text-purple-900 font-medium transition-colors">Logistics</Link>
                )}

                <div className="h-6 w-px bg-gray-300 mx-2"></div>

                <span className="text-sm text-gray-500">{user?.email}</span>

                <button 
                  onClick={handleLogout}
                  className="bg-gray-100 hover:bg-gray-200 text-gray-800 px-4 py-2 rounded-md text-sm font-medium transition-colors"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="text-gray-600 hover:text-blue-600 font-medium transition-colors">Login</Link>
                <Link to="/register" className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-md text-sm font-medium transition-colors shadow-sm">
                  Sign Up
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
