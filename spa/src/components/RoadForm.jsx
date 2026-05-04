import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { roadApi } from '../api/roadApi';

function RoadForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    positionX: 0,
    positionY: 0,
    width: 100,
    height: 10,
    angle: 0,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isEdit) {
      loadRoad();
    }
  }, [id]);

  const loadRoad = async () => {
    try {
      setLoading(true);
      const data = await roadApi.getAll();
      const road = data.find(r => r.id === Number(id));
      if (!road) {
        setError('Дорога не найдена');
        return;
      }
      setFormData({
        name: road.name || '',
        description: road.description || '',
        positionX: road.positionX ?? 0,
        positionY: road.positionY ?? 0,
        width: road.width ?? 100,
        height: road.height ?? 10,
        angle: road.angle ?? 0,
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
        await roadApi.update(id, formData);
      } else {
        await roadApi.create(formData);
      }
      navigate('/buildings');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="employee-form">
      <h1>{isEdit ? 'Редактировать дорогу' : 'Новая дорога'}</h1>
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
            placeholder="Введите название дороги"
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
            placeholder="Краткое описание дороги"
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
            <label htmlFor="width">Длина:</label>
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
            <label htmlFor="height">Ширина дороги:</label>
            <input
              type="number"
              id="height"
              name="height"
              value={formData.height}
              onChange={handleChange}
              min="5"
              placeholder="10"
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="angle">Угол (градусы):</label>
            <input
              type="number"
              id="angle"
              name="angle"
              value={formData.angle}
              onChange={handleChange}
              min="0"
              max="360"
              placeholder="0"
            />
          </div>
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary">{isEdit ? 'Сохранить' : 'Создать'}</button>
          <button type="button" onClick={() => navigate('/buildings')} className="btn">Отмена</button>
        </div>
      </form>
      )}
    </div>
  );
}

export default RoadForm;
