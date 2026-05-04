import { useState, useEffect, useRef, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { buildingApi } from '../api/buildingApi';
import { roadApi } from '../api/roadApi';
import { useNotification } from '../context/NotificationContext';

const WORLD_SIZE = 3000;
const ROAD_THICKNESS = 15;

function BuildingMap() {
  const { addNotification, dismissNotification } = useNotification();
  const [buildings, setBuildings] = useState([]);
  const [roads, setRoads] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all');
  const [searchName, setSearchName] = useState('');
  const [hoveredBuilding, setHoveredBuilding] = useState(null);
  const [hoveredRoad, setHoveredRoad] = useState(null);
  const [contextMenu, setContextMenu] = useState(null);
  const [panning, setPanning] = useState(false);
  const [dragging, setDragging] = useState(null);
  const [resizing, setResizing] = useState(null);
  const [viewBox, setViewBox] = useState({ x: 0, y: 0, w: 800, h: 500 });
  const [roadMode, setRoadMode] = useState(false);
  const [roadStart, setRoadStart] = useState(null);
  const [roadPreviewEnd, setRoadPreviewEnd] = useState(null);
  const containerRef = useRef(null);
  const arsonNotifications = useRef([]);
  const navigate = useNavigate();

  useEffect(() => {
    loadData(true);
    const interval = setInterval(() => loadData(false), 1000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (containerRef.current) {
      const rect = containerRef.current.getBoundingClientRect();
      setViewBox(prev => ({ ...prev, w: Math.round(rect.width), h: Math.round(rect.height) }));
    }
  }, [loading]);

  const loadData = async (isInitial = false) => {
    try {
      if (isInitial) setLoading(true);
      const [b, r] = await Promise.all([buildingApi.getAll(), roadApi.getAll()]);
      setBuildings(b);
      setRoads(r);
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

  const handleContextMenu = (e, item, type) => {
    e.preventDefault();
    e.stopPropagation();
    setContextMenu({ x: e.clientX, y: e.clientY, item, type });
  };

  const handleDeleteItem = async () => {
    if (!contextMenu) return;
    const { item, type } = contextMenu;
    if (!confirm(`Удалить ${type === 'road' ? 'дорогу' : 'здание'} "${item.name}"?`)) return;
    try {
      if (type === 'road') {
        await roadApi.delete(item.id);
        setRoads(roads.filter(r => r.id !== item.id));
      } else {
        await buildingApi.delete(item.id);
        setBuildings(buildings.filter(b => b.id !== item.id));
      }
    } catch (err) {
      alert('Ошибка при удалении: ' + err.message);
    }
    setContextMenu(null);
  };

  useEffect(() => {
    const closeMenu = () => setContextMenu(null);
    window.addEventListener('click', closeMenu);
    return () => window.removeEventListener('click', closeMenu);
  }, []);

  const screenToWorld = useCallback((clientX, clientY) => {
    const rect = containerRef.current?.getBoundingClientRect();
    if (!rect) return { x: 0, y: 0 };
    return {
      x: viewBox.x + ((clientX - rect.left) / rect.width) * viewBox.w,
      y: viewBox.y + ((clientY - rect.top) / rect.height) * viewBox.h,
    };
  }, [viewBox]);

  const handleSvgMouseDown = (e) => {
    if (e.button !== 0) return;
    if (roadMode) {
      const world = screenToWorld(e.clientX, e.clientY);
      if (!roadStart) {
        setRoadStart(world);
      } else {
        const dx = world.x - roadStart.x;
        const dy = world.y - roadStart.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        const angle = Math.atan2(dy, dx) * (180 / Math.PI);
        if (dist > 5) {
          roadApi.create({
            name: `Дорога ${roads.length + 1}`,
            positionX: Math.round(Math.min(roadStart.x, world.x)),
            positionY: Math.round(Math.min(roadStart.y, world.y)),
            width: Math.round(dist),
            height: ROAD_THICKNESS,
            angle: Math.round(angle),
            description: '',
          }).then(res => {
            setRoads(prev => [...prev, res]);
          }).catch(err => {
            alert('Ошибка при создании: ' + err.message);
          });
        }
        setRoadStart(null);
        setRoadPreviewEnd(null);
        setRoadMode(false);
      }
      return;
    }
    setPanning({ startX: e.clientX, startY: e.clientY, origX: viewBox.x, origY: viewBox.y });
  };

  const handleSvgMouseMove = useCallback((e) => {
    if (roadMode && roadStart) {
      const world = screenToWorld(e.clientX, e.clientY);
      setRoadPreviewEnd(world);
      return;
    }
    if (!panning) return;
    const svgRect = containerRef.current?.getBoundingClientRect();
    if (!svgRect) return;
    const dx = e.clientX - panning.startX;
    const dy = e.clientY - panning.startY;
    const scaleX = viewBox.w / svgRect.width;
    const scaleY = viewBox.h / svgRect.height;
    const newX = Math.max(-5000, Math.min(WORLD_SIZE - viewBox.w, panning.origX - dx * scaleX));
    const newY = Math.max(-5000, Math.min(WORLD_SIZE - viewBox.h, panning.origY - dy * scaleY));
    setViewBox(prev => ({ ...prev, x: newX, y: newY }));
  }, [panning, viewBox.w, viewBox.h, roadMode, roadStart, screenToWorld]);

  const handleSvgMouseUp = useCallback(() => {
    setPanning(false);
  }, []);

  const handleBuildingMouseDown = (e, building) => {
    if (e.button !== 0) return;
    e.preventDefault();
    e.stopPropagation();
    const world = screenToWorld(e.clientX, e.clientY);
    setDragging({
      type: 'building',
      building,
      origX: building.positionX,
      origY: building.positionY,
      startWorldX: world.x,
      startWorldY: world.y,
    });
  };

  const handleRoadMouseDown = (e, road) => {
    if (e.button !== 0) return;
    e.preventDefault();
    e.stopPropagation();
    const world = screenToWorld(e.clientX, e.clientY);
    setDragging({
      type: 'road',
      road,
      origX: road.positionX,
      origY: road.positionY,
      startWorldX: world.x,
      startWorldY: world.y,
    });
  };

  const handleResizeMouseDown = (e, building, handle) => {
    e.preventDefault();
    e.stopPropagation();
    const world = screenToWorld(e.clientX, e.clientY);
    setResizing({
      building,
      handle,
      origX: building.positionX,
      origY: building.positionY,
      origW: building.width,
      origH: building.height,
      startWorldX: world.x,
      startWorldY: world.y,
    });
  };

  const handleWorldMouseMove = useCallback((e) => {
    if (!dragging && !resizing) return;
    const world = screenToWorld(e.clientX, e.clientY);
    if (dragging) {
      const dx = world.x - dragging.startWorldX;
      const dy = world.y - dragging.startWorldY;
      const updater = { ...dragging };
      if (dragging.type === 'building') {
        setBuildings(prev => prev.map(b =>
          b.id === dragging.building.id ? { ...b, positionX: Math.round(dragging.origX + dx), positionY: Math.round(dragging.origY + dy) } : b
        ));
      } else if (dragging.type === 'road') {
        setRoads(prev => prev.map(r =>
          r.id === dragging.road.id ? { ...r, positionX: Math.round(dragging.origX + dx), positionY: Math.round(dragging.origY + dy) } : r
        ));
      }
    }
    if (resizing) {
      const dx = world.x - resizing.startWorldX;
      const dy = world.y - resizing.startWorldY;
      let newX = resizing.origX, newY = resizing.origY, newW = resizing.origW, newH = resizing.origH;
      if (resizing.handle.includes('e')) newW = Math.max(20, Math.round(resizing.origW + dx));
      if (resizing.handle.includes('w')) { newW = Math.max(20, Math.round(resizing.origW - dx)); newX = Math.round(resizing.origX + resizing.origW - newW); }
      if (resizing.handle.includes('s')) newH = Math.max(20, Math.round(resizing.origH + dy));
      if (resizing.handle.includes('n')) { newH = Math.max(20, Math.round(resizing.origH - dy)); newY = Math.round(resizing.origY + resizing.origH - newH); }
      setBuildings(prev => prev.map(b =>
        b.id === resizing.building.id ? { ...b, positionX: newX, positionY: newY, width: newW, height: newH } : b
      ));
    }
  }, [dragging, resizing, screenToWorld]);

  const handleWorldMouseUp = useCallback(async () => {
    if (dragging) {
      let updated, api;
      if (dragging.type === 'building') {
        updated = buildings.find(b => b.id === dragging.building.id);
        api = buildingApi;
      } else {
        updated = roads.find(r => r.id === dragging.road.id);
        api = roadApi;
      }
      if (updated) {
        try {
          await api.update(updated.id, {
            name: updated.name,
            description: updated.description || '',
            positionX: updated.positionX,
            positionY: updated.positionY,
            width: updated.width,
            height: updated.height,
            angle: updated.angle ?? 0,
          });
        } catch (err) {
          alert('Ошибка при сохранении: ' + err.message);
        }
      }
    }
    if (resizing) {
      const updated = buildings.find(b => b.id === resizing.building.id);
      if (updated) {
        try {
          await buildingApi.update(updated.id, {
            name: updated.name,
            description: updated.description,
            positionX: updated.positionX,
            positionY: updated.positionY,
            width: updated.width,
            height: updated.height,
          });
        } catch (err) {
          alert('Ошибка при сохранении: ' + err.message);
        }
      }
    }
    setDragging(null);
    setResizing(null);
  }, [dragging, resizing, buildings, roads]);

  useEffect(() => {
    if (dragging || resizing) {
      window.addEventListener('mousemove', handleWorldMouseMove);
      window.addEventListener('mouseup', handleWorldMouseUp);
      return () => {
        window.removeEventListener('mousemove', handleWorldMouseMove);
        window.removeEventListener('mouseup', handleWorldMouseUp);
      };
    }
  }, [dragging, resizing, handleWorldMouseMove, handleWorldMouseUp]);

  useEffect(() => {
    if (panning) {
      window.addEventListener('mousemove', handleSvgMouseMove);
      window.addEventListener('mouseup', handleSvgMouseUp);
      return () => {
        window.removeEventListener('mousemove', handleSvgMouseMove);
        window.removeEventListener('mouseup', handleSvgMouseUp);
      };
    }
  }, [panning, handleSvgMouseMove, handleSvgMouseUp]);

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

  const isBuildingVisible = (building) => {
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
  };

  const getStatusBadge = (status) => {
    if (status === 'OK') return 'В норме';
    if (status === 'WARNING') return 'Требует проверки';
    if (status === 'CRITICAL') return 'Критическое состояние';
    return 'Нет датчиков';
  };

  const wrapText = useCallback((text, maxWidth, fontSize) => {
    const charsPerLine = Math.max(1, Math.floor(maxWidth / (fontSize * 0.6)));
    const words = text.split(' ');
    const lines = [];
    let currentLine = '';
    for (const word of words) {
      const testLine = currentLine ? `${currentLine} ${word}` : word;
      if (testLine.length > charsPerLine && currentLine) {
        lines.push(currentLine);
        currentLine = word;
      } else {
        currentLine = testLine;
      }
    }
    if (currentLine) lines.push(currentLine);
    return lines;
  }, []);

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
          <Link to="/buildings/add" className="btn btn-primary">Добавить здание</Link>
          <button onClick={() => { setRoadMode(!roadMode); setRoadStart(null); setRoadPreviewEnd(null); }}
            className={`btn ${roadMode ? 'btn-danger' : 'btn-primary'}`}>
            {roadMode ? 'Отмена дороги' : 'Добавить дорогу'}
          </button>
          <button onClick={() => loadData(false)} className="btn btn-primary">Обновить</button>
          <button onClick={() => {
            if (buildings.length === 0) return;
            arsonNotifications.current.forEach(id => dismissNotification(id));
            arsonNotifications.current = [];
            const arsonRoll = Math.floor(Math.random() * 20) + 1;
            const buildingRoll = Math.floor(Math.random() * buildings.length) + 1;
            const target = buildings[buildingRoll - 1];
            const arsonSuccess = arsonRoll <= 5;
            const n1 = addNotification({ title: 'Кубик поджога', message: `Выпало: ${arsonRoll}/20 — ${arsonSuccess ? 'Успех' : 'Провал'}` });
            const n2 = addNotification({ title: 'Кубик здания', message: `Выпало: ${buildingRoll} → ${target.name}` });
            arsonNotifications.current = [n1.id, n2.id];
            if (arsonSuccess) {
              fetch(`/api/sensors/ignite/${target.id}`, { method: 'POST', credentials: 'include' });
            }
          }} className="btn btn-primary">Поджог</button>
          <button onClick={() => buildings.forEach(b => {
            fetch(`/api/sensors/extinguish/${b.id}`, { method: 'POST', credentials: 'include' });
          })} className="btn btn-primary">Сброс</button>
        </div>
      </div>

      {roadMode && (
        <div className="road-mode-hint">
          {roadStart ? 'Кликните для завершения дороги' : 'Кликните на карту для начала дороги'}
        </div>
      )}

      {error && <div className="error">Ошибка: {error}</div>}

      {loading && buildings.length === 0 && roads.length === 0 ? (
        <div className="loading">Загрузка карты...</div>
      ) : (
        <div className="map-container" ref={containerRef}>
        <svg
          className="map-svg"
          viewBox={`${viewBox.x} ${viewBox.y} ${viewBox.w} ${viewBox.h}`}
          preserveAspectRatio="xMidYMid meet"
          onMouseDown={handleSvgMouseDown}
          onMouseMove={(e) => { if (roadMode && roadStart) handleSvgMouseMove(e); else if (panning) handleSvgMouseMove(e); }}
          onMouseUp={() => { if (panning) handleSvgMouseUp(); }}
          style={{ cursor: dragging ? 'move' : (panning ? 'grabbing' : (roadMode ? 'crosshair' : 'grab')) }}
        >
          <defs>
            <pattern id="grid" width="20" height="20" patternUnits="userSpaceOnUse">
              <path d="M 20 0 L 0 0 0 20" fill="none" stroke="#e8e8e8" strokeWidth="0.5"/>
            </pattern>
          </defs>
          <rect x="0" y="0" width={WORLD_SIZE} height={WORLD_SIZE} fill="url(#grid)" />

          {roads.map((road) => {
            const isHovered = hoveredRoad && hoveredRoad.id === road.id;
            const cx = road.positionX + road.width / 2;
            const cy = road.positionY + road.height / 2;
            return (
              <g
                key={road.id}
                className={`map-road ${isHovered ? 'hovered' : ''}`}
                onClick={(e) => e.stopPropagation()}
                onMouseEnter={() => setHoveredRoad(road)}
                onMouseLeave={() => setHoveredRoad(null)}
                onContextMenu={(e) => handleContextMenu(e, road, 'road')}
                style={{ cursor: 'move' }}
                onMouseDown={(e) => handleRoadMouseDown(e, road)}
              >
                <rect
                  x={road.positionX}
                  y={road.positionY}
                  width={road.width}
                  height={road.height}
                  fill={isHovered ? '#888' : '#999'}
                  stroke={isHovered ? '#3f51b5' : '#666'}
                  strokeWidth={isHovered ? 2 : 1}
                  transform={`rotate(${road.angle || 0}, ${cx}, ${cy})`}
                />
                <text
                  x={cx}
                  y={cy + 4}
                  textAnchor="middle"
                  fill="white"
                  fontSize="10"
                  fontWeight="bold"
                  pointerEvents="none"
                  transform={`rotate(${road.angle || 0}, ${cx}, ${cy})`}
                >
                  {road.name}
                </text>
              </g>
            );
          })}

          {roadMode && roadStart && roadPreviewEnd && (
            <line
              x1={roadStart.x}
              y1={roadStart.y}
              x2={roadPreviewEnd.x}
              y2={roadPreviewEnd.y}
              stroke="#3f51b5"
              strokeWidth={ROAD_THICKNESS}
              strokeDasharray="8,4"
              opacity="0.6"
            />
          )}

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
                onContextMenu={(e) => handleContextMenu(e, building, 'building')}
                style={{ cursor: 'move' }}
                onMouseDown={(e) => handleBuildingMouseDown(e, building)}
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
                {isHovered && (
                  <>
                    <circle cx={building.positionX} cy={building.positionY} r="6" fill="#3f51b5" stroke="white" strokeWidth="1.5"
                      onMouseDown={(e) => handleResizeMouseDown(e, building, 'nw')} style={{ cursor: 'nwse-resize' }} />
                    <circle cx={building.positionX + building.width} cy={building.positionY} r="6" fill="#3f51b5" stroke="white" strokeWidth="1.5"
                      onMouseDown={(e) => handleResizeMouseDown(e, building, 'ne')} style={{ cursor: 'nesw-resize' }} />
                    <circle cx={building.positionX} cy={building.positionY + building.height} r="6" fill="#3f51b5" stroke="white" strokeWidth="1.5"
                      onMouseDown={(e) => handleResizeMouseDown(e, building, 'sw')} style={{ cursor: 'nesw-resize' }} />
                    <circle cx={building.positionX + building.width} cy={building.positionY + building.height} r="6" fill="#3f51b5" stroke="white" strokeWidth="1.5"
                      onMouseDown={(e) => handleResizeMouseDown(e, building, 'se')} style={{ cursor: 'nwse-resize' }} />
                  </>
                )}
                {(() => {
                  const fontSize = 13;
                  const lineHeight = fontSize * 1.2;
                  const badgeLines = building.sensorStatus ? 1 : 0;
                  const badgeHeight = badgeLines * 16;
                  const maxTextH = building.height - 10 - badgeHeight;
                  const maxLines = Math.max(1, Math.floor(maxTextH / lineHeight));
                  const lines = wrapText(building.name, building.width - 10, fontSize);
                  const shownLines = lines.slice(0, maxLines);
                  const totalH = shownLines.length * lineHeight;
                  const startY = building.positionY + (building.height - badgeHeight - totalH) / 2 + fontSize;

                  return (
                    <>
                      {shownLines.map((line, i) => (
                        <text
                          key={i}
                          x={building.positionX + building.width / 2}
                          y={startY + i * lineHeight}
                          textAnchor="middle"
                          fill="white"
                          fontSize={fontSize}
                          fontWeight="bold"
                          pointerEvents="none"
                        >
                          {line}
                        </text>
                      ))}
                      {building.sensorStatus && (
                        <text
                          x={building.positionX + building.width / 2}
                          y={building.positionY + building.height - 10}
                          textAnchor="middle"
                          fill="rgba(255,255,255,0.9)"
                          fontSize="11"
                          pointerEvents="none"
                        >
                          {getStatusBadge(building.sensorStatus)}
                        </text>
                      )}
                    </>
                  );
                })()}
              </g>
            );
          })}
        </svg>

        {(hoveredBuilding || hoveredRoad) && (
          <div
            className="map-tooltip"
            style={{
              left: (hoveredBuilding?.positionX ?? hoveredRoad?.positionX ?? 0) + (hoveredBuilding?.width ?? hoveredRoad?.width ?? 0) / 2,
              top: (hoveredBuilding?.positionY ?? hoveredRoad?.positionY ?? 0) - 10,
            }}
          >
            <strong>{hoveredBuilding?.name || hoveredRoad?.name}</strong>
            {(hoveredBuilding?.description || hoveredRoad?.description) && (
              <div className="tooltip-desc">{hoveredBuilding?.description || hoveredRoad?.description}</div>
            )}
            {hoveredBuilding && (
              <div className="tooltip-status">Статус: {getStatusBadge(hoveredBuilding.sensorStatus)}</div>
            )}
            <div className="tooltip-hint">Нажмите для просмотра</div>
          </div>
        )}
      </div>
      )}

      {contextMenu && (
        <div
          className="context-menu"
          style={{ left: contextMenu.x, top: contextMenu.y }}
          onClick={(e) => e.stopPropagation()}
        >
          <button className="context-menu-item" onClick={() => {
            if (contextMenu.type === 'road') {
              navigate(`/roads/${contextMenu.item.id}/edit`);
            } else {
              navigate(`/buildings/${contextMenu.item.id}/edit`);
            }
          }}>
            Редактировать
          </button>
          <button className="context-menu-item context-menu-danger" onClick={handleDeleteItem}>
            Удалить
          </button>
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
        <div className="legend-item">
          <span className="legend-color" style={{ background: '#999' }}></span>
          <span>Дорога</span>
        </div>
      </div>
    </div>
  );
}

export default BuildingMap;
