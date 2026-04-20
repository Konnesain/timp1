import { useState, useEffect } from 'react';
import { logApi } from '../api/employeeApi';

function LogList() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [filter, setFilter] = useState('ALL');
  const [eventTypes, setEventTypes] = useState({});

  useEffect(() => {
    loadEventTypes();
  }, []);

  useEffect(() => {
    loadLogs();
  }, [page, filter]);

  const loadEventTypes = async () => {
    try {
      const data = await logApi.getEventTypes();
      setEventTypes(data.types);
    } catch (err) {
      console.error('Failed to load event types:', err);
    }
  };

  const loadLogs = async () => {
    try {
      setLoading(true);
      const data = await logApi.getEvents(page, 50, filter);
      setLogs(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  return (
    <div className="log-list">
      <div className="log-header">
        <h1>Журнал событий</h1>
        <div className="log-controls">
          <button onClick={loadLogs} className="btn btn-primary">Обновить</button>
        </div>
      </div>

      <div className="log-filters">
        <label>Фильтр по типу:</label>
        <select value={filter} onChange={(e) => { setFilter(e.target.value); setPage(0); }}>
          <option value="ALL">Все события</option>
          {Object.entries(eventTypes).map(([key, label]) => (
            <option key={key} value={key}>{label}</option>
          ))}
        </select>
      </div>

      {error && <div className="error">Ошибка: {error}</div>}

      <div className="log-info">
        Всего записей: {totalElements}
      </div>

      {loading && logs.length === 0 ? (
        <div className="loading">Загрузка...</div>
      ) : logs.length === 0 ? (
        <div className="empty-message">Записей не найдено</div>
      ) : (
        <table className="table log-table">
          <thead>
            <tr>
              <th>Время</th>
              <th>Тип события</th>
              <th>Пользователь</th>
              <th>ID</th>
              <th>Детали</th>
              <th>Статус</th>
            </tr>
          </thead>
          <tbody>
            {logs.map(log => (
              <tr key={log.id} className={log.success ? 'log-success' : 'log-fail'}>
                <td className="log-time">{formatDate(log.timestamp)}</td>
                <td>
                  <span className="badge badge-info">
                    {eventTypes[log.type] || log.type}
                  </span>
                </td>
                <td>{log.userName}</td>
                <td className="log-user-id">{log.userId || '-'}</td>
                <td className="log-details">{log.details || '-'}</td>
                <td>
                  <span className={`badge ${log.success ? 'badge-success' : 'badge-danger'}`}>
                    {log.success ? 'Успешно' : 'Отказ'}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {totalPages > 1 && (
        <div className="pagination">
          <button
            onClick={() => setPage(p => Math.max(0, p - 1))}
            disabled={page === 0}
            className="btn"
          >
            ← Назад
          </button>
          <span className="page-info">
            Страница {page + 1} из {totalPages}
          </span>
          <button
            onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
            className="btn"
          >
            Вперед →
          </button>
        </div>
      )}
    </div>
  );
}

export default LogList;