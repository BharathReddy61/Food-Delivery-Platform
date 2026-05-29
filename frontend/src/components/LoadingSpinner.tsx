const LoadingSpinner = ({ message = 'Loading...' }: { message?: string }) => (
  <div className="flex flex-col items-center justify-center py-16">
    <div className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mb-4" />
    <p className="text-gray-500 text-sm">{message}</p>
  </div>
);

export default LoadingSpinner;
