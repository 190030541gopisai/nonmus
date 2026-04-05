import { useAuth } from "../context";
import { useNavigate } from "react-router-dom";

export function Dashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-gray-900">Nonmus</h1>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-sm text-gray-600">
                Welcome, {user?.firstName || user?.email}
              </span>
              <button
                onClick={handleLogout}
                className="inline-flex items-center px-3 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Sign out
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="border-4 border-dashed border-gray-200 rounded-lg h-96 flex items-center justify-center">
            <div className="text-center">
              <h2 className="text-2xl font-semibold text-gray-700">
                Dashboard
              </h2>
              <p className="mt-2 text-gray-500">
                You're successfully logged in!
              </p>
              {user && (
                <div className="mt-4 text-sm text-gray-600">
                  <p>
                    <strong>User ID:</strong> {user.userId}
                  </p>
                  <p>
                    <strong>Email:</strong> {user.email}
                  </p>
                  <p>
                    <strong>Name:</strong> {user.firstName}{" "}
                    {user.lastName || ""}
                  </p>
                  <p>
                    <strong>Email Verified:</strong>{" "}
                    {user.emailVerified ? "Yes" : "No"}
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
