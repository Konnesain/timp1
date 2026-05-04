import { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { buildingApi } from '../api/buildingApi';
import { sensorApi } from '../api/sensorApi';

function BuildingDetails() {
  const { id } = useParams();
  const [building, setBuilding] = useState(null);
  const [sensors, setSensors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadData();
  }, [id]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [b, sen] = await Promise.all([
        buildingApi.getById(id),
        sensorApi.getByBuilding(id)
      ]);
      setBuilding(b);
      setSensors(sen);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm(`Удалить здание "${building.name}"?`)) return;
    try {
      await buildingApi.delete(id);
      window.location.href = '/buildings';
    } catch (err) {
      alert('Ошибка при удалении: ' + err.message);
    }
  };

  const formatValue = (sensor) => {
    if (sensor.type === 'Камера') return '—';
    if (sensor.value !== null) return `${sensor.value}°C`;
    return '—';
  };

  const getTypeLabel = (type) => {
    if (type === 'Температура') return 'Температура';
    if (type === 'Камера') return 'Камера';
    return type;
  };

  return (
    <div className="building-details">
      <div className="details-header">
        <h1>{building?.name || 'Здание'}</h1>
        <div className="details-actions">
          <Link to={`/buildings/${id}/edit`} className="btn btn-primary">Редактировать</Link>
          <button onClick={handleDelete} className="btn btn-danger">Удалить</button>
          <Link to="/buildings" className="btn">Назад к карте</Link>
        </div>
      </div>

      {error && <div className="error">Ошибка: {error}</div>}

      {loading ? (
        <div className="loading">Загрузка...</div>
      ) : !building ? (
        <div className="empty-message">Здание не найдено</div>
      ) : (
        <>

      <div className="details-card">
        <div className="detail-row">
          <span className="detail-label">ID:</span>
          <span className="detail-value">{building.id}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Название:</span>
          <span className="detail-value">{building.name}</span>
        </div>
      </div>

      <h2 style={{marginTop: '2rem', marginBottom: '1rem'}}>
        Сотрудники с доступом ({building.employeesWithAccess?.length || 0})
      </h2>

      {(!building.employeesWithAccess || building.employeesWithAccess.length === 0) ? (
        <p className="empty-message">Нет сотрудников с доступом</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Имя</th>
              <th>Должность</th>
            </tr>
          </thead>
          <tbody>
            {building.employeesWithAccess.map(e => (
              <tr key={e.id}>
                <td>{e.id}</td>
                <td>{e.name}</td>
                <td>{e.position}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <h2 style={{marginTop: '2rem', marginBottom: '1rem'}}>
        Сотрудники внутри ({building.employeesInside?.length || 0})
      </h2>

      {(!building.employeesInside || building.employeesInside.length === 0) ? (
        <p className="empty-message">Нет сотрудников в здании</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Имя</th>
              <th>Должность</th>
            </tr>
          </thead>
          <tbody>
            {building.employeesInside.map(e => (
              <tr key={e.id}>
                <td>{e.id}</td>
                <td>{e.name}</td>
                <td>{e.position}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <h2 style={{marginTop: '2rem', marginBottom: '1rem'}}>
        Датчики ({sensors.length})
      </h2>

      {sensors.length === 0 ? (
        <p className="empty-message">Нет датчиков в здании</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Название</th>
              <th>Тип</th>
              <th>Статус</th>
              <th>Значение</th>
              <th>Последний сигнал</th>
            </tr>
          </thead>
          <tbody>
            {sensors.map(s => (
              <tr key={s.id}>
                <td>{s.name}</td>
                <td>{getTypeLabel(s.type)}</td>
                <td>
                  <span className={`badge ${s.online ? 'badge-success' : 'badge-warning'}`}>
                    {s.online ? 'Онлайн' : 'Офлайн'}
                  </span>
                </td>
                <td>{formatValue(s)}</td>
                <td>{s.lastSeen ? new Date(s.lastSeen).toLocaleString('ru-RU') : 'Никогда'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
        </>
      )}
    </div>
  );
}

export default BuildingDetails;
