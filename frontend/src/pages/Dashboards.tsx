

export const AdminDashboard = () => {
  return (
    <div>
      <h1 className="text-3xl font-bold mb-6 text-red-600">Admin Dashboard</h1>
      <div className="bg-white p-8 rounded-lg shadow-sm border border-gray-200">
        <p className="text-gray-600">Welcome to the administrative control panel. Here you can manage global platform settings and monitor all restaurants.</p>
        <p className="text-sm text-gray-500 mt-4 italic">Management features will be implemented in subsequent phases.</p>
      </div>
    </div>
  );
};

export const OwnerDashboard = () => {
  return (
    <div>
      <h1 className="text-3xl font-bold mb-6 text-green-600">Owner Dashboard</h1>
      <div className="bg-white p-8 rounded-lg shadow-sm border border-gray-200">
        <p className="text-gray-600">Welcome to your restaurant management panel. Here you can manage your menu, view active orders, and update restaurant details.</p>
        <p className="text-sm text-gray-500 mt-4 italic">Management features will be implemented in subsequent phases.</p>
      </div>
    </div>
  );
};
