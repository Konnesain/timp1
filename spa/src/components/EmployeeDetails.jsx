import { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { employeeApi } from '../api/employeeApi';
import { buildingApi } from '../api/buildingApi';

function EmployeeDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [employee, setEmployee] = useState(null);
  const [buildings, setBuildings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadData();
  }, [id]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [emp, bldgs] = await Promise.all([
        employeeApi.getById(id),
        buildingApi.getAll()
      ]);
      setEmployee(emp);
      setBuildings(bldgs);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getBuildingName = (buildingId) => {
    const building = buildings.find(b => b.id === buildingId);
    return building ? building.name : `Здание #${buildingId}`;
  };

  const handleDelete = async () => {
    if (!confirm(`Удалить сотрудника "${employee.name}"?`)) {
      return;
    }

    try {
      await employeeApi.delete(id);
      navigate('/employees');
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

  if (!employee) {
    return <div className="empty-message">Сотрудник не найден</div>;
  }

  const accessIds = employee.buildingAccessIds || [];

  return (
    <div className="employee-details">
      <div className="details-header">
        <h1>Информация о сотруднике</h1>
        <div className="details-actions">
          <Link to={`/employees/${id}/edit`} className="btn btn-primary">Редактировать</Link>
          <button onClick={handleDelete} className="btn btn-danger">Удалить</button>
          <Link to="/employees" className="btn">Назад к списку</Link>
        </div>
      </div>

      <div className="details-card">
        <div className="detail-row">
          <span className="detail-label">ID:</span>
          <span className="detail-value">{employee.id}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Имя:</span>
          <span className="detail-value">{employee.name}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Должность:</span>
          <span className="detail-value">{employee.position}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Текущее здание:</span>
          <span className="detail-value">
            {employee.buildingName ? (
              <span className="badge badge-success">{employee.buildingName}</span>
            ) : (
              <span className="badge badge-warning">Снаружи</span>
            )}
          </span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Доступ к зданиям:</span>
          <span className="detail-value">
            {accessIds.length === 0 ? (
              <span className="badge badge-danger">Нет доступа</span>
            ) : (
              accessIds.map(bid => (
                <span key={bid} className="badge badge-info" style={{marginRight: '4px'}}>
                  {getBuildingName(bid)}
                </span>
              ))
            )}
          </span>
        </div>
      </div>
    </div>
  );
}

export default EmployeeDetails;