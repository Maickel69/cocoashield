import React, { useState, useEffect, useCallback } from "react";
import DiagnosisTab from "./components/DiagnosisTab";
import HistoryTab from "./components/HistoryTab";
import ConfigTab from "./components/ConfigTab";
import MobileLoginPage from "./components/MobileLoginPage";
import { getHistory, saveDiagnosis, BACKEND_URL, runOnlineDiagnosis } from "./components/Database";
import AutoUpdateBanner from "./components/AutoUpdateBanner";
import {
  IconUser,
  IconHome,
  IconHistory
} from "@tabler/icons-react";

// El estado del servidor ahora se muestra como chip dentro del encabezado de Inicio
// (antes era un aviso flotante que se superponía al contenido).

// ── APP PRINCIPAL ─────────────────────────────────────────────────────────────
export default function App() {
  const [currentUser, setCurrentUser]     = useState(null);
  const [authLoading, setAuthLoading]     = useState(true);
  const [history, setHistory]             = useState([]);
  const [activeTab, setActiveTab]         = useState("diagnostico");
  const [backendStatus, setBackendStatus] = useState("checking");
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [isDetailOpen, setIsDetailOpen]   = useState(false);

  const [isDarkMode, setIsDarkMode] = useState(
    () => localStorage.getItem("cocoashield_dark_mode") === "true"
  );
  const [farmerName, setFarmerName] = useState(
    () => localStorage.getItem("cocoashield_farmer_name") || "Maicol Alberto"
  );
  const [farmName, setFarmName] = useState(
    () => localStorage.getItem("cocoashield_farm_name") || "Finca Cacaotera Lote 1"
  );
  const [currentGPS, setCurrentGPS] = useState({ name: "Obteniendo ubicacion...", lat: 0, lng: 0 });
  const [deviceBattery, setDeviceBattery] = useState(null);

  // Cargar sesión persistente de trabajador
  useEffect(() => {
    try {
      const savedLocal = localStorage.getItem("cocoashield_worker_user");
      const savedSession = sessionStorage.getItem("cocoashield_worker_user");
      const saved = savedLocal || savedSession;
      if (saved) {
        const user = JSON.parse(saved);
        setCurrentUser(user);
        if (user.name) setFarmerName(user.name);
        if (user.defaultFarm && !localStorage.getItem("cocoashield_farm_name")) {
          setFarmName(user.defaultFarm);
        }
      }
    } catch (e) {
      console.error("Error cargando sesion de trabajador", e);
    } finally {
      setAuthLoading(false);
    }
  }, []);

  const handleLoginSuccess = (user) => {
    setCurrentUser(user);
    if (user.name) setFarmerName(user.name);
    if (user.defaultFarm) setFarmName(user.defaultFarm);
  };

  const handleLogout = () => {
    localStorage.removeItem("cocoashield_worker_user");
    sessionStorage.removeItem("cocoashield_worker_user");
    setCurrentUser(null);
  };

  useEffect(() => {
    localStorage.setItem("cocoashield_dark_mode", isDarkMode);
    document.body.classList.toggle("dark", isDarkMode);
  }, [isDarkMode]);

  useEffect(() => { localStorage.setItem("cocoashield_farmer_name", farmerName); }, [farmerName]);
  useEffect(() => { localStorage.setItem("cocoashield_farm_name", farmName); }, [farmName]);

  useEffect(() => {
    let active = true;
    const fetchBattery = async () => {
      try {
        if (navigator.getBattery) {
          const battery = await navigator.getBattery();
          if (active) setDeviceBattery(Math.round(battery.level * 100));
          battery.addEventListener("levelchange", () => {
            if (active) setDeviceBattery(Math.round(battery.level * 100));
          });
        }
      } catch (_) {}
    };
    fetchBattery();
    return () => { active = false; };
  }, []);

  const fetchRealGPS = useCallback(() => {
    if (!navigator.geolocation) return;
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const acc = Math.round(pos.coords.accuracy);
        setCurrentGPS({ name: `Ubicacion GPS (+-${acc}m)`, lat: pos.coords.latitude, lng: pos.coords.longitude });
      },
      () => { setCurrentGPS({ name: farmName, lat: 0, lng: 0 }); },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    );
  }, [farmName]);

  const checkBackend = useCallback(async () => {
    try {
      const res = await fetch(`${BACKEND_URL}/api/cases`, { signal: AbortSignal.timeout(15000) });
      setBackendStatus(res.ok ? "online" : "offline");
    } catch {
      setBackendStatus("offline");
    }
  }, []);

  useEffect(() => {
    if (!currentUser) return;
    fetchRealGPS();
    checkBackend();
    getHistory().then(setHistory);
  }, [currentUser, fetchRealGPS, checkBackend]);

  useEffect(() => {
    if (!currentUser) return;
    const id = setInterval(checkBackend, 30000);
    return () => clearInterval(id);
  }, [currentUser, checkBackend]);

  const getDeviceModel = () => {
    const ua = navigator.userAgent;
    if (/Android/i.test(ua)) {
      const match = /Android[^;]+;\s*([^;)]+)/.exec(ua);
      return match ? match[1].replaceAll("_", " ").trim() : "Android";
    }
    if (/iPhone/i.test(ua)) return "iPhone";
    return "Dispositivo Movil";
  };

  const handleRunOnlineDiagnosis = async (base64Photo) => {
    const result = await runOnlineDiagnosis(
      base64Photo, farmName, farmerName, currentGPS,
      deviceBattery ?? 100, getDeviceModel()
    );
    const updated = await getHistory();
    setHistory(updated);
    return result;
  };

  const handleSaveDiagnosis = async (diagnosisData) => {
    saveDiagnosis({ ...diagnosisData, farmer: farmerName, locationName: farmName, battery: deviceBattery ?? 100, deviceModel: getDeviceModel() });
    const updated = await getHistory();
    setHistory(updated);
  };

  const handleSelectRecord = (record) => {
    setSelectedRecord(record);
    setIsDetailOpen(Boolean(record));
  };

  const renderTabContent = () => (
    <>
      {activeTab === "diagnostico" && (
        <DiagnosisTab
          onSaveDiagnosis={handleSaveDiagnosis}
          onRunOnlineDiagnosis={handleRunOnlineDiagnosis}
          currentGPS={currentGPS}
          addLog={() => {}}
          recentHistory={history}
          onNavigateTab={setActiveTab}
          currentUser={currentUser}
          onSelectRecord={handleSelectRecord}
          farmerName={farmerName}
          farmName={farmName}
          backendStatus={backendStatus}
        />
      )}
      {(activeTab === "historial" || selectedRecord) && (
        <div style={{ display: activeTab === "historial" ? "block" : "none" }}>
          <HistoryTab
            history={history}
            isOnline={backendStatus === "online"}
            onSyncNow={() => getHistory().then(setHistory)}
            unsyncedCount={0}
            onDetailOpen={setIsDetailOpen}
            selectedRecord={selectedRecord}
            onSelectRecord={handleSelectRecord}
          />
        </div>
      )}
      {activeTab === "configuracion" && !selectedRecord && (
        <ConfigTab
          currentUser={currentUser}
          onLogout={handleLogout}
          farmerName={farmerName} setFarmerName={setFarmerName}
          farmName={farmName} setFarmName={setFarmName}
          isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode}
          currentGPS={currentGPS} fetchRealGPS={fetchRealGPS}
          backendStatus={backendStatus}
          onBack={setActiveTab}
        />
      )}
    </>
  );

  if (authLoading) {
    return (
      <div style={{
        minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center",
        background: "#0B192C", color: "#fff", fontFamily: "'Outfit', sans-serif"
      }}>
        <div style={{ textAlign: "center" }}>
          <div style={{
            width: 34, height: 34, border: "3px solid rgba(17,202,160,0.25)", borderTopColor: "#11CAA0",
            borderRadius: "50%", margin: "0 auto 12px", animation: "spin 0.8s linear infinite"
          }} />
          <p style={{ color: "rgba(255,255,255,0.5)", fontSize: 13 }}>Cargando CocoaShield...</p>
        </div>
        <style>{`@keyframes spin{to{transform:rotate(360deg)}}`}</style>
      </div>
    );
  }

  // Si no está autenticado, mostrar Login
  if (!currentUser) {
    return (
      <div className="mobile-app-root">
        <MobileLoginPage onLoginSuccess={handleLoginSuccess} />
      </div>
    );
  }

  return (
    <div className="mobile-app-root">
      <div className="mobile-screen">
        {renderTabContent()}
      </div>
      {!isDetailOpen && (
        <nav className="mobile-bottom-nav">
          <div className="mobile-nav-island">
            {[
              { id: "diagnostico",   icon: <IconHome size={22} stroke={1.8} />,    label: "Inicio" },
              { id: "historial",     icon: <IconHistory size={22} stroke={1.8} />, label: "Historial" },
              { id: "configuracion", icon: <IconUser size={22} stroke={1.8} />,    label: "Mi Cuenta" },
            ].map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`mobile-nav-btn ${activeTab === tab.id ? "active" : ""}`}
                aria-current={activeTab === tab.id ? "page" : undefined}
              >
                <span className="mobile-nav-icon">{tab.icon}</span>
                <span className="mobile-nav-label">{tab.label}</span>
              </button>
            ))}
          </div>
        </nav>
      )}
      <AutoUpdateBanner />
    </div>
  );
}
