import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { buildingApi } from '../api/buildingApi';

function BuildingMap() {
  const [buildings, setBuildings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all');
  const [searchName, setSearchName] = useState('');
  const [hoveredBuilding, setHoveredBuilding] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadBuildings(true);
    const interval = setInterval(() => loadBuildings(false), 1000);
    return () => clearInterval(interval);
  }, []);

  const loadBuildings = async (isInitial = false) => {
    try {
      if (isInitial) setLoading(true);
      const data = await buildingApi.getAll();
      setBuildings(data);
      setError(null);
    } catch (err) {
      if (isInitial) setError(err.message);
    } finally {
      if (isInitial) setLoading(false);
    }
  };

  const handleBuildingClick = (building) => {
    navigate(`/buildings/${building.id}`);
  };

  const getStatusColor = (building) => {
    const status = building.sensorStatus;
    if (status === 'OK') return '#4caf50';
    if (status === 'WARNING') return '#ffc107';
    if (status === 'CRITICAL') return '#f44336';
    return '#90a4ae';
  };

  const getStrokeColor = (building, isHovered) => {
    if (isHovered) return '#3f51b5';
    const status = building.sensorStatus;
    if (status === 'OK') return '#388e3c';
    if (status === 'WARNING') return '#f57c00';
    if (status === 'CRITICAL') return '#d32f2f';
    return '#607d8b';
  };

  const isBuildingVisible = useCallback((building) => {
    if (filter === 'all') return true;
    if (filter === 'byName' && searchName) {
      return building.name.toLowerCase().includes(searchName.toLowerCase());
    }
    if (filter === 'warning') {
      return building.sensorStatus === 'WARNING' || building.sensorStatus === 'CRITICAL';
    }
    if (filter === 'critical') {
      return building.sensorStatus === 'CRITICAL';
    }
    return true;
  }, [filter, searchName]);

  const getStatusBadge = (status) => {
    if (status === 'OK') return 'В норме';
    if (status === 'WARNING') return 'Требует проверки';
    if (status === 'CRITICAL') return 'Критическое состояние';
    return 'Нет датчиков';
  };

  const svgWidth = 700;
  const svgHeight = 400;

  return (
    <div className="building-map-page">
      <div className="map-header">
        <h1>Интерактивная карта зданий</h1>
        <div className="map-controls">
          <select value={filter} onChange={(e) => setFilter(e.target.value)}>
            <option value="all">Все здания</option>
            <option value="warning">Требуют проверки</option>
            <option value="critical">Критические</option>
            <option value="byName">По имени</option>
          </select>
          {filter === 'byName' && (
            <input
              type="text"
              placeholder="Поиск по имени..."
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
              className="map-search"
            />
          )}
          <button onClick={() => loadBuildings(false)} className="btn btn-primary">Обновить</button>
        </div>
      </div>

      {error && <div className="error">Ошибка: {error}</div>}

      {loading && buildings.length === 0 ? (
        <div className="loading">Загрузка карты...</div>
      ) : (
        <div className="map-container">
        <svg
          className="map-svg"
          width={svgWidth}
          height={svgHeight}
          viewBox={`0 0 ${svgWidth} ${svgHeight}`}
        >
          <defs>
            <pattern id="grid" width="20" height="20" patternUnits="userSpaceOnUse">
              <path d="M 20 0 L 0 0 0 20" fill="none" stroke="#e8e8e8" strokeWidth="0.5"/>
            </pattern>
          </defs>
          <rect width="100%" height="100%" fill="url(#grid)" />

          {buildings.map((building) => {
            if (!isBuildingVisible(building)) return null;
            const isHovered = hoveredBuilding && hoveredBuilding.id === building.id;
            const statusColor = getStatusColor(building);
            const strokeColor = getStrokeColor(building, isHovered);

            return (
              <g
                key={building.id}
                className={`map-building ${isHovered ? 'hovered' : ''}`}
                onClick={() => handleBuildingClick(building)}
                onMouseEnter={() => setHoveredBuilding(building)}
                onMouseLeave={() => setHoveredBuilding(null)}
                style={{ cursor: 'pointer' }}
              >
                <rect
                  x={building.positionX + 3}
                  y={building.positionY + 3}
                  width={building.width}
                  height={building.height}
                  fill="rgba(0,0,0,0.15)"
                  rx="4"
                />
                <rect
                  x={building.positionX}
                  y={building.positionY}
                  width={building.width}
                  height={building.height}
                  fill={statusColor}
                  stroke={strokeColor}
                  strokeWidth={isHovered ? 3 : 1}
                  rx="4"
                />
                <text
                  x={building.positionX + building.width / 2}
                  y={building.positionY + building.height / 2 - 5}
                  textAnchor="middle"
                  fill="white"
                  fontSize="13"
                  fontWeight="bold"
                  pointerEvents="none"
                >
                  {building.name}
                </text>
                {building.sensorStatus && (
                  <text
                    x={building.positionX + building.width / 2}
                    y={building.positionY + building.height / 2 + 15}
                    textAnchor="middle"
                    fill="rgba(255,255,255,0.9)"
                    fontSize="11"
                    pointerEvents="none"
                  >
                    {getStatusBadge(building.sensorStatus)}
                  </text>
                )}
              </g>
            );
          })}
        </svg>

        {hoveredBuilding && (
          <div
            className="map-tooltip"
            style={{
              left: hoveredBuilding.positionX + hoveredBuilding.width / 2,
              top: hoveredBuilding.positionY - 10,
            }}
          >
            <strong>{hoveredBuilding.name}</strong>
            {hoveredBuilding.description && <div className="tooltip-desc">{hoveredBuilding.description}</div>}
            <div className="tooltip-status">Статус: {getStatusBadge(hoveredBuilding.sensorStatus)}</div>
            <div className="tooltip-hint">Нажмите для просмотра</div>
          </div>
        )}
      </div>
      )}

      <div className="map-legend">
        <h4>Легенда</h4>
        <div className="legend-item">
          <span className="legend-color" style={{ background: '#4caf50' }}></span>
          <span>В норме</span>
        </div>
        <div className="legend-item">
          <span className="legend-color" style={{ background: '#ffc107' }}></span>
          <span>Требует проверки</span>
        </div>
        <div className="legend-item">
          <span className="legend-color" style={{ background: '#f44336' }}></span>
          <span>Критическое состояние</span>
        </div>
        <div className="legend-item">
          <span className="legend-color" style={{ background: '#90a4ae' }}></span>
          <span>Нет датчиков</span>
        </div>
      </div>
    </div>
  );
}

export default BuildingMap;