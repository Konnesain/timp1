import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { buildingApi } from '../api/buildingApi';

function BuildingForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [formData, setFormData] = useState({
    name: '',
    description: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isEdit) {
      loadBuilding();
    }
  }, [id]);

  const loadBuilding = async () => {
    try {
      setLoading(true);
      const data = await buildingApi.getById(id);
      setFormData({
        name: data.name || '',
        description: data.description || '',
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      if (isEdit) {
        await buildingApi.update(id, formData);
      } else {
        await buildingApi.create(formData);
      }
      navigate('/');
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading && isEdit) return <div className="loading">Загрузка...</div>;

  return (
    <div className="employee-form">
      <h1>{isEdit ? 'Редактировать здание' : 'Новое здание'}</h1>
      {error && <div className="error">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="name">Название:</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            placeholder="Введите название здания"
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">Описание:</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows={3}
            placeholder="Краткое описание здания"
          />
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary">{isEdit ? 'Сохранить' : 'Создать'}</button>
          <button type="button" onClick={() => navigate('/')} className="btn">Отмена</button>
        </div>
      </form>
    </div>
  );
}

export default BuildingForm;
