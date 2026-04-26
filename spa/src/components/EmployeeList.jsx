import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { employeeApi } from '../api/employeeApi';

function EmployeeList() {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadEmployees();
  }, []);

  const loadEmployees = async () => {
    try {
      setLoading(true);
      const data = await employeeApi.getAll();
      setEmployees(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id, name) => {
    if (!confirm(`Удалить сотрудника "${name}"?`)) {
      return;
    }

    try {
      await employeeApi.delete(id);
      setEmployees(employees.filter(emp => emp.id !== id));
    } catch (err) {
      alert('Ошибка при удалении: ' + err.message);
    }
  };

  if (loading) {
    return <div className="loading">Загрузка...</div>;
  }

  if (error) {
    return <div className="error">Ошибка: {error}</div>;
  }

  return (
    <div className="employee-list">
      <div className="list-header">
        <h1>Сотрудники</h1>
        <Link to="/employees/add" className="btn btn-primary">Добавить сотрудника</Link>
      </div>

      {employees.length === 0 ? (
        <p className="empty-message">Сотрудников пока нет</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Имя</th>
              <th>Должность</th>
              <th>Здание</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {employees.map(emp => (
              <tr key={emp.id}>
                <td>{emp.id}</td>
                <td>{emp.name}</td>
                <td>{emp.position}</td>
                <td>{emp.buildingName || '—'}</td>
                <td className="actions">
                  <Link to={`/employees/${emp.id}`} className="btn btn-small">Просмотр</Link>
                  <Link to={`/employees/${emp.id}/edit`} className="btn btn-small btn-primary">Редактировать</Link>
                  <button
                    onClick={() => handleDelete(emp.id, emp.name)}
                    className="btn btn-small btn-danger"
                  >
                    Удалить
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default EmployeeList;
