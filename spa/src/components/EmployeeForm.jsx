import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { employeeApi } from '../api/employeeApi';

function EmployeeForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [formData, setFormData] = useState({
    name: '',
    position: '',
    buildingAccessIds: []
  });
  const [buildings, setBuildings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadBuildings();
    if (isEdit) {
      loadEmployee();
    }
  }, [id]);

  const loadBuildings = async () => {
    try {
      const data = await employeeApi.getBuildings();
      setBuildings(data);
    } catch (err) {
      console.error('Failed to load buildings:', err);
    }
  };

  const loadEmployee = async () => {
    try {
      setLoading(true);
      const employee = await employeeApi.getById(id);
      setFormData({
        name: employee.name,
        position: employee.position,
        buildingAccessIds: employee.buildingAccessIds || []
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleBuildingToggle = (buildingId) => {
    setFormData(prev => {
      const ids = prev.buildingAccessIds || [];
      const updated = ids.includes(buildingId)
        ? ids.filter(id => id !== buildingId)
        : [...ids, buildingId];
      return { ...prev, buildingAccessIds: updated };
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      if (isEdit) {
        await employeeApi.update(id, formData);
      } else {
        await employeeApi.create(formData);
      }
      navigate('/employees');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="employee-form">
      <h1>{isEdit ? 'Редактировать сотрудника' : 'Новый сотрудник'}</h1>

      {error && <div className="error">{error}</div>}

      {loading && isEdit ? (
        <div className="loading">Загрузка...</div>
      ) : (
        <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="name">Имя:</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            placeholder="Введите имя сотрудника"
          />
        </div>

        <div className="form-group">
          <label htmlFor="position">Должность:</label>
          <input
            type="text"
            id="position"
            name="position"
            value={formData.position}
            onChange={handleChange}
            required
            placeholder="Введите должность"
          />
        </div>

        {buildings.length > 0 && (
          <div className="form-group">
            <label>Доступ к зданиям:</label>
            <div className="building-access">
              {buildings.map(b => (
                <label key={b.id} className="building-check">
                  <input
                    type="checkbox"
                    checked={(formData.buildingAccessIds || []).includes(b.id)}
                    onChange={() => handleBuildingToggle(b.id)}
                  />
                  {b.name}
                </label>
              ))}
            </div>
          </div>
        )}

        <div className="form-actions">
          <button type="submit" className="btn btn-primary">
            {isEdit ? 'Сохранить' : 'Создать'}
          </button>
          <button type="button" onClick={() => navigate('/employees')} className="btn">
            Отмена
          </button>
        </div>
      </form>
      )}
    </div>
  );
}

export default EmployeeForm;
