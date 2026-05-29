# Food Delivery Platform

A full-stack food delivery application that enables customers to browse restaurants, place orders, make payments, and track deliveries while providing restaurant owners and delivery partners with dedicated dashboards.

## Features

### Customer

* User registration and login
* Browse restaurants
* View menus
* Add items to cart
* Place orders
* Online payment integration

---

## Tech Stack

### Frontend

* React
* TypeScript
* Vite
* Tailwind CSS
* Axios
* React Router

### Backend

* Spring Boot
* Spring Security
* Spring Data JPA
* Maven

### Database

* MySQL

### Payment Gateway

* Razorpay

### DevOps

* Docker
* GitHub Actions

---

## Project Structure

```text
food-delivery-platform
│
├── frontend
│   ├── src
│   ├── components
│   ├── pages
│   └── services
│
├── backend
│   ├── src
│   ├── controllers
│   ├── services
│   ├── repositories
│   └── entities
│
└── docker
```

## Getting Started

### Clone Repository

```bash
git clone https://github.com/BharathReddy61/Food-Delivery-Platform.git
cd Food-Delivery-Platform
```

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

### Backend Setup

```bash
cd backend
./mvnw spring-boot:run
```

### Database Setup

1. Create a MySQL database.
2. Update database credentials in `application.properties`.
3. Run the backend application.

---

## Environment Variables

### Frontend

```env
VITE_API_BASE_URL=http://localhost:8080
```

### Backend

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
RAZORPAY_KEY_ID=
RAZORPAY_KEY_SECRET=
```

---

## Screenshots

Add screenshots of:

* Home Page
* Restaurant Listing
* Cart Page
* Checkout Page
* Owner Dashboard
* Delivery Dashboard

---

## Future Improvements

* Real-time order tracking
* Push notifications
* AI-based restaurant recommendations
* Multi-vendor support
* Cloud deployment

### Restaurant Owner

* Restaurant management
* Menu management
* Order management
* Dashboard analytics
   
### Delivery Partner

* View assigned deliveries
* Update delivery status
* Delivery tracking workflow

### Admin

* User management
* Restaurant monitoring
* Platform oversight

---

## Author

Bharath Reddy

GitHub: https://github.com/BharathReddy61

```
```
