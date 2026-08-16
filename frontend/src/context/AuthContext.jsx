import { createContext, useContext, useState, useCallback } from 'react';
import { authApi, getToken, setToken } from '../api/client';

const AuthContext = createContext(null);

function loadStoredUser() {
  const raw = localStorage.getItem('keystone_user');
  return raw ? JSON.parse(raw) : null;
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadStoredUser());

  const login = useCallback(async (email, password) => {
    const response = await authApi.login(email, password);
    // response: { token, username, role, expiresAt }
    setToken(response.token);
    const userInfo = { username: response.username, role: response.role, expiresAt: response.expiresAt };
    localStorage.setItem('keystone_user', JSON.stringify(userInfo));
    setUser(userInfo);
    return userInfo;
  }, []);

  const logout = useCallback(() => {
    setToken(null);
    localStorage.removeItem('keystone_user');
    setUser(null);
  }, []);

  const isAuthenticated = Boolean(user && getToken());

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
