import { Link } from 'react-router-dom';

import type { Restaurant } from '../types';

const RestaurantCard = ({
  restaurant
}: {
  restaurant: Restaurant
}) => (

  <Link
    to={`/restaurants/${restaurant.id}`}
    className="
      group block bg-white rounded-2xl
      overflow-hidden border border-gray-100
      shadow-sm hover:shadow-xl
      transition-all duration-300
      hover:-translate-y-1
    "
  >

    {/* Restaurant Image */}

    <div className="relative h-52 overflow-hidden">

      {restaurant.imageUrl ? (

        <img
          src={restaurant.imageUrl}
          alt={restaurant.name}
          className="
            h-full w-full object-cover
            group-hover:scale-105
            transition-transform duration-500
          "
        />

      ) : (

        <div className="
          h-full w-full
          bg-gradient-to-br
          from-orange-100
          to-red-100
          flex items-center justify-center
        ">

          <span className="text-6xl">
            🍽️
          </span>

        </div>
      )}

      {/* Overlay Gradient */}

      <div className="
        absolute inset-0
        bg-gradient-to-t
        from-black/60
        via-black/10
        to-transparent
      " />

      {/* Open / Closed Badge */}

      <div className="absolute top-4 right-4">

        <span className={`
          text-xs px-3 py-1 rounded-full
          font-semibold backdrop-blur-sm
          ${restaurant.open
            ? 'bg-green-500/90 text-white'
            : 'bg-red-500/90 text-white'
          }
        `}>

          {restaurant.open ? 'Open' : 'Closed'}

        </span>

      </div>

      {/* Distance Badge */}

      {restaurant.distanceKm !== undefined &&
        restaurant.distanceKm !== null && (

          <div className="
          absolute bottom-4 left-4
          bg-white/90 backdrop-blur-sm
          text-gray-900 text-xs
          font-semibold px-3 py-1 rounded-full
        ">

            📍 {restaurant.distanceKm.toFixed(1)} km away

          </div>
        )}

    </div>

    {/* Content */}

    <div className="p-5">

      {/* Name */}

      <div className="
        flex justify-between
        items-start gap-3 mb-2
      ">

        <h3 className="
          text-xl font-bold text-gray-900
          leading-tight line-clamp-1
        ">

          {restaurant.name}

        </h3>

        {/* Rating */}

        <div className="
          flex items-center gap-1
          bg-yellow-50 text-yellow-700
          px-2 py-1 rounded-lg
          text-sm font-semibold
          whitespace-nowrap
        ">

          ⭐ {
            restaurant.rating > 0
              ? restaurant.rating.toFixed(1)
              : 'New'
          }

        </div>

      </div>

      {/* Cuisine */}

      <p className="
        text-sm text-orange-600
        font-medium mb-2
      ">

        {restaurant.cuisineType}

      </p>

      {/* Description */}

      <p className="
        text-sm text-gray-500
        line-clamp-2 mb-4
      ">

        {restaurant.description}

      </p>

      {/* Footer */}

      <div className="
        flex items-center justify-between
        pt-3 border-t border-gray-100
      ">

        {/* City */}

        <div className="
          flex items-center gap-2
          text-sm text-gray-600
        ">

          📍 {restaurant.city}

        </div>

        {/* Delivery Radius */}

        {restaurant.deliveryRadiusKm && (

          <div className="
            text-xs font-medium
            bg-blue-50 text-blue-700
            px-2 py-1 rounded-full
          ">

            🚚 {restaurant.deliveryRadiusKm} km delivery

          </div>
        )}

      </div>

    </div>

  </Link>
);

export default RestaurantCard;