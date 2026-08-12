import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function AppHeader() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
  }

  return (
    <header className="app-header">
      <Link to="/work-orders" className="app-logo">Keystone</Link>
      <div className="app-header-right">
        {user && (
          <span className="muted">
            {user.username} · <strong>{user.role}</strong>
          </span>
        )}
        <button className="btn btn-secondary btn-small" onClick={handleLogout}>
          Log out
        </button>
      </div>
    </header>
  );
}
