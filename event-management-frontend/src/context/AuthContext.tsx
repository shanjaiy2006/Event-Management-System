import React, { createContext, useContext, useState, useEffect } from 'react';
import { decodeToken, isTokenExpired } from '@/utils/jwt';

interface AuthUser {
  email: string;
  role: 'ADMIN' | 'ORGANIZER' | 'STUDENT';
  name: string;
}

interface AuthContextType {
  user: AuthUser | null;
  token: string | null;
  login: (token: string) => void;
  logout: () => void;
  isAuthenticated: boolean;
  isLoading: boolean;
  profilePicture: string | null;
  updateProfilePicture: (pic: string | null) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('jwt_token'));
  const [user, setUser] = useState<AuthUser | null>(null);
  const [profilePicture, setProfilePicture] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;

    const initializeAuth = async () => {
      if (token) {
        if (isTokenExpired(token)) {
          localStorage.removeItem('jwt_token');
          setToken(null);
          setUser(null);
          setProfilePicture(null);
        } else {
          const decoded = decodeToken(token);
          if (decoded && decoded.email && decoded.role) {
            setUser({
              email: decoded.email,
              role: decoded.role as 'ADMIN' | 'ORGANIZER' | 'STUDENT',
              name: decoded.name || 'User',
            });

            // Fetch complete profile from server
            try {
              const { default: api } = await import('@/services/api');
              const res = await api.get('/profile');
              if (res.data && isMounted) {
                if (res.data.profilePicture) {
                  setProfilePicture(res.data.profilePicture);
                } else {
                  setProfilePicture(null);
                }

                // Update user details if needed from server response
                setUser(prev => prev ? {
                  ...prev,
                  name: res.data.name || prev.name,
                  email: res.data.email || prev.email,
                  role: (res.data.role as 'ADMIN' | 'ORGANIZER' | 'STUDENT') || prev.role
                } : prev);
              }
            } catch (e) {
              console.error("Failed to fetch profile", e);
            }
          } else {
            localStorage.removeItem('jwt_token');
            setToken(null);
            setUser(null);
            setProfilePicture(null);
          }
        }
      } else {
        setUser(null);
        setProfilePicture(null);
      }
      if (isMounted) {
        setIsLoading(false);
      }
    };

    initializeAuth();

    return () => {
      isMounted = false;
    };
  }, [token]);

  const login = (newToken: string) => {
    localStorage.setItem('jwt_token', newToken);
    setToken(newToken);
  };

  const logout = () => {
    localStorage.removeItem('jwt_token');
    setToken(null);
    setUser(null);
    setProfilePicture(null);
  };

  const updateProfilePicture = (pic: string | null) => {
    if (user) {
      setProfilePicture(pic);
    }
  };

  const isAuthenticated = !!token && !isTokenExpired(token);

  return (
    <AuthContext.Provider value={{
      user,
      token,
      login,
      logout,
      isAuthenticated,
      isLoading,
      profilePicture,
      updateProfilePicture
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
