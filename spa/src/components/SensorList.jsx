import { useState, useEffect } from 'react';
import { sensorApi } from '../api/sensorApi';

function SensorList() {
  const [sensors, setSensors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadSensors = async (isInitial = false) => {
    try {
      if (isInitial) setLoading(true);
      const data = await sensorApi.getAll();
      setSensors(data);
      setError(null);
    } catch (err) {
      if (isInitial) setError(err.message);
    } finally {
      if (isInitial) setLoading(false);
    }
  };

  useEffect(() => {
    loadSensors(true);
    const interval = setInterval(() => loadSensors(false), 1000);
    return () => clearInterval(interval);
  }, []);

  const formatLastSeen = (lastSeen) => {
    if (!lastSeen) return 'Никогда';
    const date = new Date(lastSeen);
    return date.toLocaleString('ru-RU');
  };

  const formatValue = (sensor) => {
    if (sensor.value !== null) return `${sensor.value}°C`;
    return '—';
  };

  const getTypeLabel = (type) => {
    return type;
  };

  return (
    <div className="sensor-list">
      <div className="list-header">
        <h1>Датчики</h1>
        <button onClick={() => loadSensors(false)} className="btn btn-primary">Обновить</button>
      </div>

      {error && <div className="error">Ошибка: {error}</div>}

      {loading && sensors.length === 0 ? (
        <div className="loading">Загрузка...</div>
      ) : sensors.length === 0 ? (
        <p className="empty-message">Датчиков пока нет</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Название</th>
              <th>Тип</th>
              <th>Здание</th>
              <th>Статус</th>
              <th>Значение</th>
              <th>Последний сигнал</th>
            </tr>
          </thead>
          <tbody>
            {sensors.map(sensor => (
              <tr key={sensor.id}>
                <td>{sensor.id}</td>
                <td>{sensor.name}</td>
                <td>{getTypeLabel(sensor.type)}</td>
                <td>{sensor.buildingName}</td>
                <td>
                  {sensor.online ? (
                    <span className="badge badge-success">Онлайн</span>
                  ) : (
                    <span className="badge badge-warning">Офлайн</span>
                  )}
                </td>
                <td>{formatValue(sensor)}</td>
                <td>{formatLastSeen(sensor.lastSeen)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default SensorList;