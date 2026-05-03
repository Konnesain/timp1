import { useNotification } from '../context/NotificationContext';
import { useNavigate } from 'react-router-dom';

function NotificationToast() {
  const { notifications, dismissNotification } = useNotification();
  const navigate = useNavigate();

  if (notifications.length === 0) return null;

  return (
    <div className="notification-container">
      {notifications.map(notification => (
        <div key={notification.id} className="notification critical">
          <div className="notification-header">
            <strong>КРИТИЧЕСКАЯ ТЕМПЕРАТУРА</strong>
            <button
              className="notification-close"
              onClick={() => dismissNotification(notification.id)}
            >
              ×
            </button>
          </div>
          <div className="notification-body">
            <div><strong>Здание:</strong> {notification.buildingName}</div>
            <div><strong>Датчик:</strong> {notification.sensor}</div>
            <button
              className="notification-action"
              onClick={() => {
                dismissNotification(notification.id);
                navigate('/buildings');
              }}
            >
              Перейти на карту
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}

export default NotificationToast;
