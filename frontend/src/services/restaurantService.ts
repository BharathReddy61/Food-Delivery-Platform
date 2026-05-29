import api from '../api/axiosConfig';

import type {
  Restaurant,
  MenuItem
} from '../types';

export const restaurantService = {

  /*
    Get all restaurants
  */

  getAll: (): Promise<Restaurant[]> =>

    api
      .get('/api/restaurants')
      .then(response => response.data),

  /*
    Get single restaurant
  */

  getById: (id: number): Promise<Restaurant> =>

    api
      .get(`/api/restaurants/${id}`)
      .then(response => response.data),

  /*
    Nearby restaurants
  */

  getNearby: (
    latitude: number,
    longitude: number
  ): Promise<Restaurant[]> =>

    api
      .get('/api/restaurants/nearby', {
        params: {
          latitude,
          longitude
        }
      })
      .then(response => response.data),

  /*
    Filter by city
  */

  getByCity: (
    city: string
  ): Promise<Restaurant[]> =>

    api
      .get(`/api/restaurants/city/${city}`)
      .then(response => response.data),

  /*
    Restaurant menu
  */

  getMenuByRestaurant: (
    restaurantId: number
  ): Promise<MenuItem[]> =>

    api
      .get(`/api/menu/restaurant/${restaurantId}/available`)
      .then(response => response.data),
};