import { useEffect, useState } from 'react';

import { restaurantService } from '../../services/restaurantService';

import RestaurantCard from '../../components/RestaurantCard';

import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';

import type { Restaurant } from '../../types';

const Restaurants = () => {

  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState('');

  const [locationLoading, setLocationLoading] = useState(false);

  const [locationEnabled, setLocationEnabled] = useState(false);

  const [cityFilter, setCityFilter] = useState('');

  /*
    Initial restaurant load
  */

  useEffect(() => {

    loadAllRestaurants();

  }, []);

  /*
    Load all restaurants
  */

  const loadAllRestaurants = async () => {

    try {

      setLoading(true);

      const data = await restaurantService.getAll();

      setRestaurants(data);

    } catch (err: any) {

      setError(
        err.response?.data?.message ||
        'Failed to load restaurants.'
      );

    } finally {

      setLoading(false);
    }
  };

  /*
    Use current location
  */

  const handleUseCurrentLocation = () => {

    if (!navigator.geolocation) {

      setError('Geolocation is not supported in this browser.');

      return;
    }

    setLocationLoading(true);

    navigator.geolocation.getCurrentPosition(

      async (position) => {

        try {

          const latitude = position.coords.latitude;
          const longitude = position.coords.longitude;

          const response = await restaurantService.getNearby(
            latitude,
            longitude
          );

          setRestaurants(response);

          setLocationEnabled(true);

          setError('');

        } catch (err: any) {

          setError(
            err.response?.data?.message ||
            'Failed to load nearby restaurants.'
          );

        } finally {

          setLocationLoading(false);
        }
      },

      () => {

        setError(
          'Location access denied. Please allow location permissions.'
        );

        setLocationLoading(false);
      }
    );
  };

  /*
    City filter
  */

  const handleCitySearch = async () => {

    if (!cityFilter.trim()) {

      loadAllRestaurants();

      return;
    }

    try {

      setLoading(true);

      const response =
        await restaurantService.getByCity(cityFilter);

      setRestaurants(response);

    } catch (err: any) {

      setError(
        err.response?.data?.message ||
        'Failed to filter restaurants.'
      );

    } finally {

      setLoading(false);
    }
  };

  if (loading) {

    return (
      <LoadingSpinner message="Loading restaurants..." />
    );
  }

  return (

    <div>

      {/* Header */}

      <div className="mb-8 flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">

        <div>

          <h1 className="text-3xl font-bold text-gray-900">
            Browse Restaurants
          </h1>

          <p className="text-gray-500 mt-1">
            Discover food near your location
          </p>

        </div>

        {/* Location Actions */}

        <div className="flex flex-col sm:flex-row gap-3">

          <input
            type="text"
            placeholder="Search by city..."
            value={cityFilter}
            onChange={(e) => setCityFilter(e.target.value)}
            className="
              px-4 py-2 border border-gray-300 rounded-lg
              focus:outline-none focus:ring-2 focus:ring-orange-500
            "
          />

          <button
            onClick={handleCitySearch}
            className="
              bg-gray-900 hover:bg-black text-white
              px-4 py-2 rounded-lg transition-colors
            "
          >
            Search
          </button>

          <button
            onClick={handleUseCurrentLocation}
            disabled={locationLoading}
            className="
              bg-orange-500 hover:bg-orange-600
              text-white px-4 py-2 rounded-lg
              transition-colors disabled:opacity-50
            "
          >
            {locationLoading
              ? 'Detecting...'
              : 'Use Current Location'}
          </button>

        </div>

      </div>

      {/* Nearby badge */}

      {locationEnabled && (

        <div className="
          mb-6 inline-flex items-center gap-2
          bg-green-100 text-green-700
          px-4 py-2 rounded-full text-sm font-medium
        ">

          📍 Showing nearby restaurants

        </div>
      )}

      {/* Errors */}

      {error && (
        <ErrorMessage message={error} />
      )}

      {/* Restaurant Grid */}

      <div className="
        grid grid-cols-1
        sm:grid-cols-2
        lg:grid-cols-3
        gap-6
      ">

        {restaurants.map((restaurant) => (

          <RestaurantCard
            key={restaurant.id}
            restaurant={restaurant}
          />

        ))}

      </div>

      {/* Empty State */}

      {!error && restaurants.length === 0 && (

        <div className="
          text-center py-20
          text-gray-500
        ">

          <p className="text-2xl font-semibold mb-2">
            No restaurants found
          </p>

          <p className="text-sm">
            Try another city or enable location access.
          </p>

        </div>
      )}

    </div>
  );
};

export default Restaurants;