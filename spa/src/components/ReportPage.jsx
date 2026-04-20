import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { buildingApi, reportApi } from '../api/reportApi';

function ReportPage() {
  const [buildings, setBuildings] = useState([]);
  const [selectedBuildingId, setSelectedBuildingId] = useState('');
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadBuildings();
  }, []);

  const loadBuildings = async () => {
    try {
      setLoading(true);
      const data = await buildingApi.getAll();
      setBuildings(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateReport = async () => {
    if (!selectedBuildingId) {
      setError('Выберите здание');
      return;
    }
    try {
      setGenerating(true);
      setError(null);
      await reportApi.downloadBuildingReport(parseInt(selectedBuildingId));
    } catch (err) {
      setError('Ошибка при генерации отчёта: ' + err.message);
    } finally {
      setGenerating(false);
    }
  };

  if (loading) return <div className="loading">Загрузка...</div>;

  return (
    <div className="report-page">
      <h1>Генерация отчёта о безопасности</h1>

      {error && <div className="error">{error}</div>}

      <div className="form-group">
        <label htmlFor="building-select">Выберите здание:</label>
        <select
          id="building-select"
          value={selectedBuildingId}
          onChange={(e) => setSelectedBuildingId(e.target.value)}
          className="form-control"
        >
          <option value="">-- Выберите здание --</option>
          {buildings.map((building) => (
            <option key={building.id} value={building.id}>
              {building.name}
            </option>
          ))}
        </select>
      </div>

      <div className="form-actions">
        <button
          onClick={handleGenerateReport}
          disabled={!selectedBuildingId || generating}
          className="btn btn-primary"
        >
          {generating ? 'Генерация...' : 'Скачать отчёт'}
        </button>
        <button onClick={() => navigate('/')} className="btn">
          Назад
        </button>
      </div>
    </div>
  );
}

export default ReportPage;