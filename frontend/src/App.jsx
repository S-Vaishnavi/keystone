import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import WorkOrderListPage from './pages/WorkOrderListPage';
import WorkOrderDetailPage from './pages/WorkOrderDetailPage';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/work-orders"
            element={
              <ProtectedRoute>
                <WorkOrderListPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/work-orders/:id"
            element={
              <ProtectedRoute>
                <WorkOrderDetailPage />
              </ProtectedRoute>
            }
          />
          <Route path="/" element={<Navigate to="/work-orders" replace />} />
          <Route path="*" element={<Navigate to="/work-orders" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
