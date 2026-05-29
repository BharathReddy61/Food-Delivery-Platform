import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import MainLayout from './layouts/MainLayout';
import { RequireAuth, RequireRole } from './routes/ProtectedRoutes';

// Public Pages
import Home from './pages/public/Home';
import Login from './pages/public/Login';
import Register from './pages/public/Register';

// Customer Pages
import Restaurants from './pages/customer/Restaurants';
import RestaurantDetail from './pages/customer/RestaurantDetail';
import Cart from './pages/customer/Cart';
import Checkout from './pages/customer/Checkout';
import Orders from './pages/customer/Orders';
import OrderDetail from './pages/customer/OrderDetail';

// Owner Pages
import OwnerDashboard from './pages/owner/OwnerDashboard';
import OwnerOrders from './pages/owner/OwnerOrders';
import OwnerMenu from './pages/owner/OwnerMenu';
import OwnerRestaurant from './pages/owner/OwnerRestaurant';

// Delivery Pages
import DeliveryDashboard from './pages/delivery/DeliveryDashboard';

// Admin placeholder (Phase 8)
import { AdminDashboard } from './pages/Dashboards';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route element={<MainLayout />}>
            {/* Public Routes — no auth required */}
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* All protected routes require a valid JWT */}
            <Route element={<RequireAuth />}>

              {/* ── Customer commerce routes ── */}
              <Route element={<RequireRole allowedRoles={['CUSTOMER', 'ADMIN']} />}>
                <Route path="/restaurants" element={<Restaurants />} />
                <Route path="/restaurants/:id" element={<RestaurantDetail />} />
                <Route path="/cart" element={<Cart />} />
                <Route path="/checkout" element={<Checkout />} />
                <Route path="/orders" element={<Orders />} />
                <Route path="/orders/:orderId" element={<OrderDetail />} />
              </Route>

              {/* ── Admin area ── */}
              <Route element={<RequireRole allowedRoles={['ADMIN']} />}>
                <Route path="/admin/dashboard" element={<AdminDashboard />} />
              </Route>

              {/* ── Owner area ── */}
              <Route element={<RequireRole allowedRoles={['RESTAURANT_OWNER']} />}>
                <Route path="/owner/dashboard" element={<OwnerDashboard />} />
                <Route path="/owner/orders" element={<OwnerOrders />} />
                <Route path="/owner/menu" element={<OwnerMenu />} />
                <Route path="/owner/restaurant/:id" element={<OwnerRestaurant />} />
              </Route>

              {/* ── Delivery Partner area ── */}
              <Route element={<RequireRole allowedRoles={['DELIVERY_PARTNER']} />}>
                <Route path="/delivery/dashboard" element={<DeliveryDashboard />} />
              </Route>

            </Route>
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
