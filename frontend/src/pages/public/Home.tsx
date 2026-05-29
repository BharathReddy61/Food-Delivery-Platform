
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-[70vh]">
      <h1 className="text-5xl font-extrabold text-gray-900 mb-6">Delicious Food, Delivered Fast</h1>
      <p className="text-lg text-gray-600 mb-8 text-center max-w-2xl">
        Experience the best restaurants in town from the comfort of your home. 
        Fast delivery, secure payments, and amazing customer service.
      </p>
      <div className="flex space-x-4">
        <Link to="/restaurants" className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-md font-semibold transition-colors shadow-md">
          Browse Restaurants
        </Link>
        <Link to="/register" className="bg-white hover:bg-gray-50 text-blue-600 border border-blue-600 px-6 py-3 rounded-md font-semibold transition-colors shadow-sm">
          Sign Up Now
        </Link>
      </div>
    </div>
  );
};

export default Home;
