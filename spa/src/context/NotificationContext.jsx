import { useState, useContext, createContext, useEffect, useCallback } from 'react';
import { useAuth } from './AuthContext';
import { useLocation } from 'react-router-dom';

const NotificationContext = createContext(null);

export const useNotification = () => useContext(NotificationContext);

export function NotificationProvider({ children }) {
  const [notifications, setNotifications] = useState([]);
  const { user } = useAuth();
  const location = useLocation();

  const dismissNotification = useCallback((id) => {
    setNotifications(prev => prev.filter(n => n.id !== id));
  }, []);

  useEffect(() => {
    if (!user) return;

    let eventSource;
    let reconnectTimeout;

    const connect = () => {
      eventSource = new EventSource('/api/notifications/stream');

      eventSource.addEventListener('critical-alert', (event) => {
        try {
          if (location.pathname === '/buildings') return;

          const data = JSON.parse(event.data);
          const notification = {
            id: Date.now() + Math.random(),
            ...data
          };
          setNotifications(prev => [...prev, notification]);

          setTimeout(() => {
            dismissNotification(notification.id);
          }, 30000);
        } catch (e) {
          console.error('Failed to parse notification:', e);
        }
      });

      eventSource.onerror = (error) => {
        eventSource.close();
        if (eventSource.readyState === EventSource.CLOSED) {
          console.log('SSE connection closed');
          return;
        }
        reconnectTimeout = setTimeout(connect, 5000);
      };
    };

    connect();

    return () => {
      if (reconnectTimeout) clearTimeout(reconnectTimeout);
      if (eventSource) eventSource.close();
    };
  }, [user, dismissNotification, location.pathname]);

  return (
    <NotificationContext.Provider value={{ notifications, dismissNotification }}>
      {children}
    </NotificationContext.Provider>
  );
}
