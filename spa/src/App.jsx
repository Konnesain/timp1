import { BrowserRouter, Routes, Route, Link, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import EmployeeList from './components/EmployeeList';
import EmployeeForm from './components/EmployeeForm';
import EmployeeDetails from './components/EmployeeDetails';
import BuildingDetails from './components/BuildingDetails';
import BuildingForm from './components/BuildingForm';
import BuildingMap from './components/BuildingMap';
import LogList from './components/LogList';
import Login from './components/Login';
import SensorList from './components/SensorList';
import ReportPage from './components/ReportPage';

function AppHeader() {
  const { user, logout } = useAuth();
  if (!user) return null;

  return (
    <header className="app-header">
      <Link to="/" className="app-title">Система управления</Link>
      <nav className="app-nav">
        <Link to="/">Карта</Link>
        <Link to="/employees">Сотрудники</Link>
        <Link to="/logs">Журнал событий</Link>
        <Link to="/sensors">Датчики</Link>
        <Link to="/report">Отчёты</Link>
        <span className="user-info">{user.username}</span>
        <button onClick={logout} className="btn btn-small btn-danger" style={{marginLeft: '0.5rem'}}>
          Выйти
        </button>
      </nav>
    </header>
  );
}

function AppRoutes() {
  const { user } = useAuth();

  if (!user) {
    return <Routes><Route path="*" element={<Login />} /></Routes>;
  }

  return (
    <Routes>
      <Route path="/login" element={<Navigate to="/" replace />} />
      <Route path="/" element={<BuildingMap />} />
      <Route path="/employees" element={<EmployeeList />} />
      <Route path="/add" element={<EmployeeForm />} />
      <Route path="/:id" element={<EmployeeDetails />} />
      <Route path="/:id/edit" element={<EmployeeForm />} />
      <Route path="/buildings/add" element={<BuildingForm />} />
      <Route path="/buildings/:id" element={<BuildingDetails />} />
      <Route path="/buildings/:id/edit" element={<BuildingForm />} />
      <Route path="/logs" element={<LogList />} />
      <Route path="/sensors" element={<SensorList />} />
      <Route path="/report" element={<ReportPage />} />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="app">
          <AppHeader />
          <main className="app-content">
            <AppRoutes />
          </main>
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;