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
    positionX: 0,
    positionY: 0,
    width: 100,
    height: 100,
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
        positionX: data.positionX ?? 0,
        positionY: data.positionY ?? 0,
        width: data.width ?? 100,
        height: data.height ?? 100,
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? (value === '' ? '' : Number(value)) : value,
    }));
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

  return (
    <div className="employee-form">
      <h1>{isEdit ? 'Редактировать здание' : 'Новое здание'}</h1>
      {error && <div className="error">{error}</div>}
      {loading && isEdit ? (
        <div className="loading">Загрузка...</div>
      ) : (
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

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="positionX">Позиция X:</label>
            <input
              type="number"
              id="positionX"
              name="positionX"
              value={formData.positionX}
              onChange={handleChange}
              placeholder="0"
            />
          </div>

          <div className="form-group">
            <label htmlFor="positionY">Позиция Y:</label>
            <input
              type="number"
              id="positionY"
              name="positionY"
              value={formData.positionY}
              onChange={handleChange}
              placeholder="0"
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="width">Ширина:</label>
            <input
              type="number"
              id="width"
              name="width"
              value={formData.width}
              onChange={handleChange}
              min="10"
              placeholder="100"
            />
          </div>

          <div className="form-group">
            <label htmlFor="height">Высота:</label>
            <input
              type="number"
              id="height"
              name="height"
              value={formData.height}
              onChange={handleChange}
              min="10"
              placeholder="100"
            />
          </div>
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary">{isEdit ? 'Сохранить' : 'Создать'}</button>
          <button type="button" onClick={() => navigate('/')} className="btn">Отмена</button>
        </div>
      </form>
      )}
    </div>
  );
}

export default BuildingForm;
