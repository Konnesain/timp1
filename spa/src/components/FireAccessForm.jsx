import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { fireAccessApi } from '../api/fireAccessApi';
import { buildingApi } from '../api/buildingApi';

function FireAccessForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [formData, setFormData] = useState({
    positionX: 0,
    positionY: 0,
    width: 40,
    height: 40,
    angle: 0,
    buildingIds: []
  });
  const [allBuildings, setAllBuildings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadBuildings();
    if (isEdit) {
      loadFireAccess();
    }
  }, [id]);

  const loadBuildings = async () => {
    try {
      const data = await buildingApi.getAll();
      setAllBuildings(data);
    } catch (err) {
      console.error('Failed to load buildings:', err);
    }
  };

  const loadFireAccess = async () => {
    try {
      setLoading(true);
      const data = await fireAccessApi.getAll();
      const fa = data.find(f => f.id === Number(id));
      if (!fa) {
        setError('Пожарный подъезд не найден');
        return;
      }
      setFormData({
        positionX: fa.positionX ?? 0,
        positionY: fa.positionY ?? 0,
        width: fa.width ?? 40,
        height: fa.height ?? 40,
        angle: fa.angle ?? 0,
        buildingIds: fa.buildingIds || []
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: (name === 'positionX' || name === 'positionY' || name === 'width' || name === 'height' || name === 'angle')
        ? (value === '' ? '' : Number(value))
        : value
    }));
  };

  const handleBuildingToggle = (buildingId) => {
    setFormData(prev => {
      const ids = prev.buildingIds || [];
      const updated = ids.includes(buildingId)
        ? ids.filter(id => id !== buildingId)
        : [...ids, buildingId];
      return { ...prev, buildingIds: updated };
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      if (isEdit) {
        await fireAccessApi.update(id, formData);
      } else {
        await fireAccessApi.create(formData);
      }
      navigate('/buildings');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="employee-form">
      <h1>{isEdit ? 'Редактировать пожарный подъезд' : 'Новый пожарный подъезд'}</h1>
      {error && <div className="error">{error}</div>}
      {loading && isEdit ? (
        <div className="loading">Загрузка...</div>
      ) : (
        <form onSubmit={handleSubmit}>
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
                placeholder="40"
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
                placeholder="40"
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

          {allBuildings.length > 0 && (
            <div className="form-group">
              <label>Привязанные здания:</label>
              <div className="building-access">
                {allBuildings.map(b => (
                  <label key={b.id} className="building-check">
                    <input
                      type="checkbox"
                      checked={(formData.buildingIds || []).includes(b.id)}
                      onChange={() => handleBuildingToggle(b.id)}
                    />
                    {b.name}
                  </label>
                ))}
              </div>
            </div>
          )}

          <div className="form-actions">
            <button type="submit" className="btn btn-primary">{isEdit ? 'Сохранить' : 'Создать'}</button>
            <button type="button" onClick={() => navigate('/buildings')} className="btn">Отмена</button>
          </div>
        </form>
      )}
    </div>
  );
}

export default FireAccessForm;