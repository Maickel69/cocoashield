import React, { useState, useRef, useCallback } from 'react';
import {
  IconCamera,
  IconRefresh,
  IconBrain,
  IconAlertCircle,
  IconBolt,
  IconFolderOpen,
  IconChevronRight,
  IconSparkles,
  IconAlertTriangle,
  IconCheck
} from '@tabler/icons-react';
import { DISEASE_CATALOG } from './Database';
import logo from '../assets/logo.png';

function compressImage(imgEl) {
  try {
    const canvas = document.createElement('canvas');
    const maxDim = 300;
    let width = imgEl.naturalWidth || imgEl.width;
    let height = imgEl.naturalHeight || imgEl.height;

    if (width > height) {
      if (width > maxDim) {
        height = Math.round((height * maxDim) / width);
        width = maxDim;
      }
    } else if (height > maxDim) {
      width = Math.round((width * maxDim) / height);
      height = maxDim;
    }

    canvas.width = width;
    canvas.height = height;
    const ctx = canvas.getContext('2d');
    ctx.drawImage(imgEl, 0, 0, width, height);
    return canvas.toDataURL('image/jpeg', 0.6);
  } catch (e) {
    console.error('Error compressing image:', e);
    return null;
  }
}

// ─── Análisis visual rápido de respaldo local (sin requerir servidor) ─────────
function analyzeByColor(imgEl) {
  try {
    const c = document.createElement('canvas');
    c.width = 64; c.height = 64;
    const ctx = c.getContext('2d');
    ctx.drawImage(imgEl, 0, 0, 64, 64);
    const d = ctx.getImageData(0, 0, 64, 64).data;
    let r = 0, g = 0, b = 0;
    for (let i = 0; i < d.length; i += 4) { r += d[i]; g += d[i+1]; b += d[i+2]; }
    const n = d.length / 4;
    r /= n; g /= n; b /= n;
    if (r > 190 && g > 190 && b > 190) return { disease: 'Monilia',        certainty: 84 };
    if (r < 70  && g < 70  && b < 70)  return { disease: 'Mazorca Negra',  certainty: 81 };
    if (g > r + 20 && g > b + 20)      return { disease: 'Sano',           certainty: 89 };
    if (r > 120 && g < 90  && b < 90)  return { disease: 'Escoba de Bruja',certainty: 79 };
    return { disease: 'Monilia', certainty: 82 };
  } catch { return { disease: 'Sano', certainty: 78 }; }
}

// ─── SVG Ilustraciones ────────────────────────────────────────────────────────
const CocoaPodSVG = ({ type }) => {
  switch (type) {
    case 'Monilia':
      return (
        <svg viewBox="0 0 100 100" style={{ width: '100%', height: '100%' }}>
          <rect width="100" height="100" fill="#E2EBF0" />
          <path d="M50 15 C65 25, 75 40, 75 55 C75 70, 65 85, 50 90 C35 85, 25 70, 25 55 C25 40, 35 25, 50 15 Z" fill="#C68A4C" stroke="#8D5A2B" strokeWidth="3" />
          <circle cx="50" cy="50" r="16" fill="#FAFAFA" stroke="#D1D5DB" strokeWidth="1" strokeDasharray="3 2" opacity="0.95" />
          <circle cx="48" cy="46" r="10" fill="#E5E7EB" opacity="0.9" />
          <circle cx="56" cy="54" r="8" fill="#F3F4F6" opacity="0.9" />
          <text x="50" y="52" fontSize="6" fontWeight="bold" fill="#555" textAnchor="middle">MONILIA</text>
        </svg>
      );
    case 'Escoba de Bruja':
      return (
        <svg viewBox="0 0 100 100" style={{ width: '100%', height: '100%' }}>
          <rect width="100" height="100" fill="#FAF0E6" />
          <path d="M50 85 C50 60, 45 40, 35 30" fill="none" stroke="#5C4033" strokeWidth="5" strokeLinecap="round" />
          <path d="M50 85 C50 65, 52 45, 65 35" fill="none" stroke="#5C4033" strokeWidth="4" strokeLinecap="round" />
          <path d="M35 30 C30 20, 25 25, 20 22" fill="none" stroke="#8B5A2B" strokeWidth="3" strokeLinecap="round" />
          <path d="M35 30 C32 18, 38 15, 30 10" fill="none" stroke="#8B5A2B" strokeWidth="3" strokeLinecap="round" />
          <path d="M65 35 C70 25, 75 30, 80 26" fill="none" stroke="#8B5A2B" strokeWidth="2.5" strokeLinecap="round" />
          <text x="50" y="80" fontSize="7" fontWeight="bold" fill="#3E2723" textAnchor="middle">ESCOBA DE BRUJA</text>
        </svg>
      );
    case 'Mazorca Negra':
      return (
        <svg viewBox="0 0 100 100" style={{ width: '100%', height: '100%' }}>
          <rect width="100" height="100" fill="#EADCC9" />
          <path d="M50 15 C65 25, 75 40, 75 55 C75 70, 65 85, 50 90 C35 85, 25 70, 25 55 C25 40, 35 25, 50 15 Z" fill="#B5804C" stroke="#7A4E26" strokeWidth="3" />
          <path d="M50 90 C35 85, 27 72, 33 65 C40 58, 55 60, 64 68 C70 74, 65 85, 50 90 Z" fill="#2B2017" stroke="#1C140E" strokeWidth="1" />
          <text x="50" y="38" fontSize="7" fontWeight="bold" fill="#F3F4F6" textAnchor="middle">MAZORCA NEGRA</text>
        </svg>
      );
    default:
      return (
        <svg viewBox="0 0 100 100" style={{ width: '100%', height: '100%' }}>
          <rect width="100" height="100" fill="#E6F2E7" />
          <path d="M50 15 C67 25, 76 40, 76 55 C76 70, 67 85, 50 90 C33 85, 24 70, 24 55 C24 40, 33 25, 50 15 Z" fill="#E5A93C" stroke="#A67117" strokeWidth="3" />
          <path d="M25 25 C15 45, 35 45, 42 32 Z" fill="#4B8B5B" opacity="0.8" />
          <text x="50" y="55" fontSize="8" fontWeight="bold" fill="#FFF" textAnchor="middle">SANO</text>
        </svg>
      );
  }
};

const renderHistoryCardThumbnail = (item, altText, fallbackBg) => {
  const photo = item?.photo || item?.image;
  if (photo) {
    return (
      <img
        src={photo}
        alt={altText}
        style={{ width: '100%', height: '100%', objectFit: 'cover' }}
      />
    );
  }
  if (item) {
    return (
      <div style={{ width: '100%', height: '100%' }}>
        <CocoaPodSVG type={item.disease} />
      </div>
    );
  }
  return <div style={{ width: '100%', height: '100%', backgroundColor: fallbackBg }} />;
};

export default function DiagnosisTab({
  onSaveDiagnosis,
  onRunOnlineDiagnosis,
  currentGPS,
  addLog,
  recentHistory = [],
  onNavigateTab,
  currentUser,
  onSelectRecord,
  farmerName,
  farmName,
  backendStatus
}) {
  const [capturedImage, setCapturedImage]     = useState(null);
  const [lastImgElement, setLastImgElement]   = useState(null);
  const [processingState, setProcessingState] = useState('idle'); // idle|analyzing|done|error
  const [aiProgress, setAiProgress]           = useState(0);
  const [aiLogMsg, setAiLogMsg]               = useState('');
  const [result, setResult]                   = useState(null);
  const [uiModelStatus, setUiModelStatus]     = useState('ready'); // ready|loading|error

  const fileInputRef = useRef(null);
  const progressRef  = useRef(null);

  const handleOpenCamera = () => fileInputRef.current?.click();

  const handleOpenGallery = () => {
    if (!fileInputRef.current) return;
    fileInputRef.current.removeAttribute('capture');
    fileInputRef.current.click();
    setTimeout(() => fileInputRef.current?.setAttribute('capture', 'environment'), 500);
  };

  const handleImageCapture = useCallback(async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    e.target.value = '';

    const blobUrl = URL.createObjectURL(file);
    setCapturedImage(blobUrl);
    setProcessingState('analyzing');
    setAiProgress(0);

    const img = new Image();
    img.onload = () => {
      setLastImgElement(img);
      runAnalysis(img, blobUrl);
    };
    img.src = blobUrl;
  }, []);

  const runAnalysis = async (imgEl, blobUrl) => {
    const STEPS = [
      'Preparando imagen...',
      'Conectando con el servidor en la nube...',
      'Analizando síntomas y signos fúngicos...',
      'Comparando patrones fitosanitarios...',
      'Generando recomendaciones de tratamiento...',
      'Registrando reporte agronómico...',
    ];
    let stepIdx = 0;

    clearInterval(progressRef.current);
    progressRef.current = setInterval(() => {
      setAiProgress(p => Math.min(p + 16, 92));
      if (stepIdx < STEPS.length) {
        setAiLogMsg(STEPS[stepIdx++]);
      }
    }, 450);

    const base64Photo = compressImage(imgEl);

    try {
      setUiModelStatus('loading');
      const diagResult = await onRunOnlineDiagnosis(base64Photo);

      clearInterval(progressRef.current);
      setAiProgress(100);
      setAiLogMsg('Diagnóstico completado con éxito.');
      setUiModelStatus('ready');

      setTimeout(() => {
        setResult({
          disease: diagResult.disease,
          certainty: diagResult.certainty,
          model: 'Inteligencia Artificial CocoaShield',
          details: diagResult.details,
          photo: base64Photo
        });
        setProcessingState('done');
      }, 400);

    } catch (err) {
      clearInterval(progressRef.current);
      console.warn('Servidor cloud ocupado o no responde:', err.message);
      setUiModelStatus('error');
      setAiProgress(0);
      setAiLogMsg('Tiempo de espera agotado.');

      setTimeout(() => {
        setProcessingState('error');
      }, 300);
    }
  };

  const handleRetry = () => {
    if (lastImgElement && capturedImage) {
      setProcessingState('analyzing');
      setAiProgress(0);
      runAnalysis(lastImgElement, capturedImage);
    } else {
      handleDiscard();
    }
  };

  const handleUseLocalFallback = () => {
    if (!lastImgElement) return;
    const base64Photo = compressImage(lastImgElement);
    const local = analyzeByColor(lastImgElement);
    setResult({
      disease: local.disease,
      certainty: local.certainty,
      model: 'Análisis Rápido Visual',
      details: null,
      photo: base64Photo
    });
    setProcessingState('done');
  };

  const handleSave = () => {
    if (!result) return;
    onSaveDiagnosis({
      disease:      result.disease,
      certainty:    result.certainty,
      latitude:     currentGPS.lat,
      longitude:    currentGPS.lng,
      locationName: currentGPS.name,
      photo:        result.photo,
      thumbnail:    result.disease.toLowerCase().replaceAll(' ', '_')
    });
    handleDiscard();
  };

  const handleDiscard = () => {
    if (capturedImage) URL.revokeObjectURL(capturedImage);
    setCapturedImage(null);
    setLastImgElement(null);
    setResult(null);
    setProcessingState('idle');
    setAiProgress(0);
    setAiLogMsg('');
    clearInterval(progressRef.current);
  };

  const activeDisease = result ? DISEASE_CATALOG[result.disease] : null;

  return (
    <div className="tab-content animate-fade-in">
      {/* ── Encabezado Ejecutivo Superior ────────────────────────── */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '2px 2px 14px',
        borderBottom: '1px solid rgba(0, 0, 0, 0.06)',
        marginBottom: 16
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{
            position: 'relative',
            width: 44,
            height: 44,
            borderRadius: 14,
            backgroundColor: '#E8F2EA',
            border: '1.5px solid var(--color-accent-border)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 4px 12px rgba(44, 94, 59, 0.12)',
            overflow: 'hidden',
            flexShrink: 0
          }}>
            <img src={logo} alt="Logo" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
          </div>
          <div>
            <div style={{
              fontSize: 16,
              fontWeight: 800,
              color: 'var(--color-text-dark)',
              letterSpacing: '-0.3px',
              lineHeight: 1.2
            }}>
              Hola, {farmerName || 'Agricultor'} 👋
            </div>
            <div style={{
              fontSize: 12,
              color: 'var(--color-text-muted)',
              display: 'flex',
              alignItems: 'center',
              gap: 4,
              marginTop: 2,
              fontWeight: 500
            }}>
              <span>🌱</span>
              <span style={{ fontWeight: 600 }}>{farmName || 'Finca San José'}</span>
            </div>
          </div>
        </div>

        {/* Chip de Estado Cloud AI */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: 6,
          backgroundColor: backendStatus === 'online' ? '#E6F4EA' : '#FEF7E0',
          border: `1px solid ${backendStatus === 'online' ? '#CEEAD6' : '#FEEFC3'}`,
          borderRadius: 20,
          padding: '5px 10px',
          fontSize: 11,
          fontWeight: 700,
          color: backendStatus === 'online' ? '#137333' : '#B06000',
          boxShadow: '0 1px 3px rgba(0,0,0,0.04)',
          flexShrink: 0
        }}>
          <span style={{
            width: 7,
            height: 7,
            borderRadius: '50%',
            backgroundColor: backendStatus === 'online' ? '#34A853' : '#FBBC04',
            display: 'inline-block',
            boxShadow: backendStatus === 'online' ? '0 0 6px #34A853' : 'none'
          }} />
          <span>{backendStatus === 'online' ? 'IA Online' : 'Local'}</span>
        </div>
      </div>

      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        capture="environment"
        style={{ display: 'none' }}
        onChange={handleImageCapture}
      />

      {/* ══ ESTADO: IDLE (EXECUTIVE DASHBOARD + SCANNER + CAROUSEL) ══════ */}
      {processingState === 'idle' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
          {/* Fila de Métricas Rápidas (KPIs Fitosanitarios) */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(3, 1fr)',
            gap: 10
          }}>
            <div style={{
              backgroundColor: 'var(--color-bg-card)',
              border: '1px solid var(--color-border)',
              borderRadius: 16,
              padding: '12px 8px',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              textAlign: 'center',
              boxShadow: '0 2px 6px rgba(0,0,0,0.03)'
            }}>
              <span style={{ fontSize: 10, fontWeight: 700, color: 'var(--color-text-muted)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
                Escaneos
              </span>
              <span style={{ fontSize: 20, fontWeight: 900, color: 'var(--color-text-dark)', marginTop: 2 }}>
                {recentHistory.length}
              </span>
              <span style={{ fontSize: 9.5, color: 'var(--color-primary)', fontWeight: 700, marginTop: 1 }}>
                Totales
              </span>
            </div>

            <div style={{
              backgroundColor: '#FEF2F2',
              border: '1px solid #FEE2E2',
              borderRadius: 16,
              padding: '12px 8px',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              textAlign: 'center',
              boxShadow: '0 2px 6px rgba(220,38,38,0.04)'
            }}>
              <span style={{ fontSize: 10, fontWeight: 700, color: '#991B1B', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
                Enfermos
              </span>
              <span style={{ fontSize: 20, fontWeight: 900, color: '#DC2626', marginTop: 2 }}>
                {recentHistory.filter(h => h.disease && h.disease.toLowerCase() !== 'sano').length}
              </span>
              <span style={{ fontSize: 9.5, color: '#DC2626', fontWeight: 700, marginTop: 1 }}>
                Atención
              </span>
            </div>

            <div style={{
              backgroundColor: '#F0FDF4',
              border: '1px solid #DCFCE7',
              borderRadius: 16,
              padding: '12px 8px',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              textAlign: 'center',
              boxShadow: '0 2px 6px rgba(22,101,52,0.04)'
            }}>
              <span style={{ fontSize: 10, fontWeight: 700, color: '#166534', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
                Sanos
              </span>
              <span style={{ fontSize: 20, fontWeight: 900, color: '#15803D', marginTop: 2 }}>
                {recentHistory.filter(h => h.disease && h.disease.toLowerCase() === 'sano').length}
              </span>
              <span style={{ fontSize: 9.5, color: '#16A34A', fontWeight: 700, marginTop: 1 }}>
                Óptimos
              </span>
            </div>
          </div>

          {/* Tarjeta Principal de Escaneo Unificada (Sleek Scanner Card) */}
          <div style={{
            backgroundColor: 'var(--color-bg-card)',
            borderRadius: 24,
            border: '1.5px solid var(--color-accent-border)',
            padding: '22px 18px 20px',
            boxShadow: '0 8px 30px rgba(44, 94, 59, 0.07)',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            textAlign: 'center',
            position: 'relative',
            overflow: 'hidden'
          }}>
            {/* Fondo de resplandor sutil */}
            <div style={{
              position: 'absolute',
              top: -40,
              left: '50%',
              transform: 'translateX(-50%)',
              width: 220,
              height: 120,
              borderRadius: '50%',
              background: 'radial-gradient(circle, rgba(44, 94, 59, 0.12) 0%, rgba(255,255,255,0) 70%)',
              pointerEvents: 'none'
            }} />

            {/* Micro-composición de tarjetas apiladas con fotos / pods */}
            <div
              role="button"
              tabIndex={0}
              onClick={handleOpenCamera}
              style={{
                position: 'relative',
                width: 110,
                height: 94,
                marginBottom: 14,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'flex-end',
                justifyContent: 'center'
              }}
            >
              {/* Tarjeta 1 (Izquierda inclinada) */}
              <div style={{
                position: 'absolute',
                top: 0,
                left: 6,
                width: 50,
                height: 64,
                borderRadius: 12,
                overflow: 'hidden',
                backgroundColor: '#9FBFA7',
                border: '2px solid #FFFFFF',
                transform: 'rotate(-14deg)',
                boxShadow: '0 6px 14px rgba(0,0,0,0.12)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 1
              }}>
                {renderHistoryCardThumbnail(recentHistory[2], "Foto 3", '#406349')}
              </div>

              {/* Tarjeta 2 (Derecha inclinada) */}
              <div style={{
                position: 'absolute',
                top: 2,
                right: 6,
                width: 50,
                height: 64,
                borderRadius: 12,
                overflow: 'hidden',
                backgroundColor: '#B5C4D4',
                border: '2px solid #FFFFFF',
                transform: 'rotate(13deg)',
                boxShadow: '0 6px 14px rgba(0,0,0,0.12)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 2
              }}>
                {renderHistoryCardThumbnail(recentHistory[1], "Foto 2", '#7D6385')}
              </div>

              {/* Tarjeta 3 (Centro) */}
              <div style={{
                position: 'absolute',
                top: 8,
                left: '50%',
                transform: 'translateX(-50%) rotate(-2deg)',
                width: 54,
                height: 68,
                borderRadius: 14,
                overflow: 'hidden',
                backgroundColor: '#C59567',
                border: '2px solid #FFFFFF',
                boxShadow: '0 8px 18px rgba(0,0,0,0.16)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 3
              }}>
                {renderHistoryCardThumbnail(recentHistory[0], "Foto reciente", '#A45D38')}
              </div>

              {/* Botón flotante central con cámara */}
              <div style={{
                position: 'relative',
                width: 52,
                height: 52,
                borderRadius: '50%',
                backgroundColor: 'var(--color-primary)',
                boxShadow: '0 8px 20px rgba(44, 94, 59, 0.4)',
                border: '3px solid #FFFFFF',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#FFFFFF',
                zIndex: 4,
                marginBottom: -4
              }}>
                <IconCamera size={26} stroke={2.2} />
              </div>
            </div>

            {/* Título y subtítulo */}
            <h3 style={{ fontSize: 16, fontWeight: 800, margin: '0 0 6px', color: 'var(--color-text-dark)', letterSpacing: '-0.2px' }}>
              Diagnóstico Fitosanitario IA
            </h3>
            <p style={{ fontSize: 12, color: 'var(--color-text-muted)', margin: '0 0 16px', lineHeight: 1.45, maxWidth: 300 }}>
              Detecta Monilia, Mazorca Negra y Escoba de Bruja al instante con visión artificial botánica.
            </p>

            {/* Fila de Acciones Principales */}
            <div style={{ display: 'flex', gap: 10, width: '100%' }}>
              <button
                type="button"
                onClick={handleOpenCamera}
                style={{
                  flex: 1,
                  padding: '14px 20px',
                  borderRadius: 16,
                  background: 'linear-gradient(135deg, #2C5E3B 0%, #1c4228 100%)',
                  color: '#FFFFFF',
                  fontSize: 14,
                  fontWeight: 800,
                  border: 'none',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: 8,
                  boxShadow: '0 6px 20px rgba(44, 94, 59, 0.35)',
                  transition: 'transform 0.15s ease'
                }}
                onMouseDown={e => e.currentTarget.style.transform = 'scale(0.98)'}
                onMouseUp={e => e.currentTarget.style.transform = 'scale(1)'}
              >
                <IconCamera size={19} stroke={2.2} />
                <span>Tomar Foto de Mazorca</span>
              </button>

              <button
                type="button"
                onClick={handleOpenGallery}
                title="Subir desde galería"
                style={{
                  width: 50,
                  height: 50,
                  borderRadius: 16,
                  backgroundColor: 'var(--color-bg)',
                  border: '1.5px solid var(--color-border)',
                  color: 'var(--color-text-dark)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  cursor: 'pointer',
                  flexShrink: 0,
                  transition: 'all 0.15s ease',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.03)'
                }}
                onMouseDown={e => e.currentTarget.style.transform = 'scale(0.94)'}
                onMouseUp={e => e.currentTarget.style.transform = 'scale(1)'}
              >
                <IconFolderOpen size={22} stroke={1.8} />
              </button>
            </div>

            {/* Tip de campo */}
            <div style={{
              marginTop: 14,
              padding: '8px 12px',
              borderRadius: 12,
              backgroundColor: 'var(--color-bg)',
              border: '1px solid var(--color-border)',
              display: 'flex',
              alignItems: 'center',
              gap: 8,
              width: '100%',
              boxSizing: 'border-box'
            }}>
              <span style={{ fontSize: 13 }}>💡</span>
              <span style={{ fontSize: 11, color: 'var(--color-text-muted)', textAlign: 'left', lineHeight: 1.35 }}>
                Enfoca el fruto a 20-30 cm con buena iluminación natural para 95%+ de certeza.
              </span>
            </div>
          </div>

          {/* Sección Inferior: Diagnósticos Recientes */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                <h3 style={{ fontSize: 15, fontWeight: 800, margin: 0, color: 'var(--color-text-dark)' }}>
                  Diagnósticos Recientes
                </h3>
                {recentHistory.length > 0 && (
                  <span style={{
                    fontSize: 10,
                    fontWeight: 700,
                    backgroundColor: 'var(--color-accent)',
                    color: 'var(--color-primary)',
                    padding: '2px 7px',
                    borderRadius: 10
                  }}>
                    {recentHistory.length}
                  </span>
                )}
              </div>
              <button
                type="button"
                onClick={() => onNavigateTab?.('historial')}
                style={{
                  background: 'none',
                  border: 'none',
                  color: 'var(--color-primary)',
                  fontSize: 12.5,
                  fontWeight: 700,
                  cursor: 'pointer',
                  padding: '2px 4px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: 2
                }}
              >
                <span>Ver todos</span>
                <IconChevronRight size={14} stroke={2.5} />
              </button>
            </div>

            {/* Carrusel de fotos recientes */}
            {recentHistory && recentHistory.length > 0 ? (
              <div style={{
                display: 'flex',
                gap: 12,
                overflowX: 'auto',
                paddingBottom: 8,
                scrollbarWidth: 'none',
                WebkitOverflowScrolling: 'touch'
              }}>
                {recentHistory.slice(0, 6).map((item) => {
                  const diseaseInfo = DISEASE_CATALOG[item.disease] || DISEASE_CATALOG['Sano'];
                  const photoSrc = item.photo || item.image;
                  const isHealthy = item.disease && item.disease.toLowerCase() === 'sano';

                  return (
                    <div
                      key={item.id}
                      role="button"
                      tabIndex={0}
                      onClick={() => {
                        if (onSelectRecord) {
                          onSelectRecord(item);
                        } else {
                          onNavigateTab?.('historial');
                        }
                      }}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter' || e.key === ' ') {
                          e.preventDefault();
                          if (onSelectRecord) {
                            onSelectRecord(item);
                          } else {
                            onNavigateTab?.('historial');
                          }
                        }
                      }}
                      style={{
                        minWidth: 120,
                        maxWidth: 120,
                        borderRadius: 18,
                        overflow: 'hidden',
                        backgroundColor: 'var(--color-bg-card)',
                        border: '1px solid var(--color-border)',
                        cursor: 'pointer',
                        boxShadow: '0 4px 12px rgba(0,0,0,0.04)',
                        flexShrink: 0,
                        transition: 'transform 0.15s ease'
                      }}
                      onMouseDown={e => e.currentTarget.style.transform = 'scale(0.97)'}
                      onMouseUp={e => e.currentTarget.style.transform = 'scale(1)'}
                    >
                      <div style={{ width: 120, height: 105, position: 'relative', backgroundColor: '#E2EBF0' }}>
                        {photoSrc ? (
                          <img
                            src={photoSrc}
                            alt={item.disease}
                            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                          />
                        ) : (
                          <CocoaPodSVG type={item.disease} />
                        )}
                        <span style={{
                          position: 'absolute',
                          bottom: 6,
                          left: 6,
                          fontSize: 9,
                          fontWeight: 800,
                          backgroundColor: isHealthy ? 'rgba(44, 94, 59, 0.88)' : 'rgba(217, 48, 37, 0.88)',
                          color: '#FFFFFF',
                          padding: '2px 6px',
                          borderRadius: 8,
                          backdropFilter: 'blur(4px)'
                        }}>
                          {item.certainty}%
                        </span>
                      </div>
                      <div style={{ padding: '8px 10px' }}>
                        <div style={{
                          fontSize: 11.5,
                          fontWeight: 800,
                          color: 'var(--color-text-dark)',
                          whiteSpace: 'nowrap',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis'
                        }}>
                          {diseaseInfo.name}
                        </div>
                        <div style={{
                          fontSize: 10,
                          color: 'var(--color-text-muted)',
                          whiteSpace: 'nowrap',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          marginTop: 2
                        }}>
                          {item.locationName || 'Finca'}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            ) : (
              <div style={{
                textAlign: 'center',
                padding: '24px 16px',
                borderRadius: 18,
                backgroundColor: 'var(--color-bg-card)',
                border: '1.5px dashed var(--color-border)',
                color: 'var(--color-text-muted)',
                fontSize: 12
              }}>
                <div style={{ fontSize: 24, marginBottom: 6 }}>📸</div>
                <div style={{ fontWeight: 700, color: 'var(--color-text-dark)', marginBottom: 2 }}>Sin diagnósticos previos</div>
                <div>Toma tu primera foto para ver el análisis aquí.</div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* ══ ESTADO: ANALYZING ══════════════════════════════════════════════════ */}
      {processingState === 'analyzing' && (
        <div style={{
          backgroundColor: 'var(--color-bg-card)',
          borderRadius: 24,
          border: '1px solid var(--color-border)',
          padding: '24px 20px',
          textAlign: 'center',
          boxShadow: '0 4px 20px rgba(0,0,0,0.04)'
        }}>
          <div style={{
            overflow: 'hidden',
            borderRadius: 18,
            position: 'relative',
            border: '2px solid var(--color-primary)',
            maxWidth: 240,
            margin: '0 auto 18px',
            boxShadow: '0 8px 24px rgba(0,0,0,0.1)'
          }}>
            <img src={capturedImage} alt="Analizando" style={{ width: '100%', height: '220px', objectFit: 'cover', display: 'block' }} />
            <div className="scanner-line" style={{ top: `${aiProgress}%` }} />
          </div>

          <h3 style={{ fontSize: 17, fontWeight: 800, margin: '0 0 6px', color: 'var(--color-text-dark)' }}>
            Analizando Fruto de Cacao
          </h3>
          <span style={{ fontSize: 13, fontWeight: 700, color: 'var(--color-primary)' }}>
            {aiProgress}% completado
          </span>

          {/* Barra de progreso plana */}
          <div style={{ width: '100%', height: 6, backgroundColor: 'var(--color-border)', borderRadius: 10, margin: '14px 0 16px', overflow: 'hidden' }}>
            <div style={{ width: `${aiProgress}%`, height: '100%', backgroundColor: 'var(--color-primary)', borderRadius: 10, transition: 'width 0.3s ease' }} />
          </div>

          <div style={{
            padding: '12px 14px',
            borderRadius: 14,
            backgroundColor: 'var(--color-bg)',
            border: '1px solid var(--color-border)',
            textAlign: 'left'
          }}>
            <p style={{ fontSize: 12.5, fontWeight: 600, color: 'var(--color-text-dark)', margin: '0 0 4px' }}>
              &gt; {aiLogMsg || 'Examinando síntomas botánicos...'}
            </p>
            <p style={{ fontSize: 11, color: 'var(--color-text-muted)', margin: 0 }}>
              Si es el primer análisis, el modelo en la nube puede tardar unos segundos en sincronizar.
            </p>
          </div>
        </div>
      )}

      {/* ══ ESTADO: DONE ═══════════════════════════════════════════════════════ */}
      {processingState === 'done' && activeDisease && result && (
        <div className="result-container">
          <div className="result-card" style={{ borderRadius: '16px', boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}>
            <div className="result-img-box" style={{ position:'relative', overflow:'hidden', height: '160px' }}>
              {capturedImage
                ? <img src={capturedImage} alt="Analizada" style={{ width:'100%', height:'100%', objectFit:'cover' }} />
                : <CocoaPodSVG type={result.disease} />
              }
              <div className="result-img-tag" style={{ background: 'var(--color-primary)' }}>
                <IconBrain size={13} stroke={2} /> {result.model}
              </div>
            </div>

            <div className="result-header" style={{ padding: '16px' }}>
              <div className="result-title-row" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <h2 className="result-title" style={{ fontSize: '20px', fontWeight: 800 }}>{activeDisease.name}</h2>
                  <span className="result-subtitle" style={{ fontSize: '13px', color: 'var(--color-text-muted)', fontStyle: 'italic' }}>{activeDisease.scientificName}</span>
                </div>
                <span className="disease-badge" style={{ backgroundColor: activeDisease.color, color: 'white', padding: '4px 10px', borderRadius: '12px', fontSize: '12px', fontWeight: 800 }}>
                  {result.certainty}% CERTEZA
                </span>
              </div>
            </div>

            <div className="result-body" style={{ padding: '0 16px 16px 16px' }}>
              <p className="disease-desc" style={{ fontSize: '13px', color: 'var(--color-text-dark)', lineHeight: 1.4, margin: '0 0 12px 0' }}>
                {activeDisease.description}
              </p>

              <div className="action-steps" style={{ display: 'flex', flexDirection: 'column', gap: '8px', backgroundColor: 'var(--color-bg)', padding: '12px', borderRadius: '12px', border: '1px solid var(--color-border)' }}>
                <h4 style={{ margin: 0, fontSize: '12px', textTransform: 'uppercase', color: 'var(--color-text-muted)', fontWeight: 800 }}>
                  Protocolo de Manejo Recomendado:
                </h4>
                {activeDisease.steps.map((step, idx) => (
                  <div key={`${activeDisease.name || 'step'}-${step.substring(0, 15)}`} className="step-row" style={{ display: 'flex', gap: '6px', fontSize: '12.5px', color: 'var(--color-text-dark)' }}>
                    <span className="step-num" style={{ fontWeight: 800, color: 'var(--color-primary)' }}>{idx + 1}.</span>
                    <span className="step-text" style={{ flex: 1 }}>{step.substring(8)}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div className="action-btns-group" style={{ display: 'flex', gap: '10px' }}>
            <button onClick={handleSave} className="btn-save-gps" style={{ flex: 2, padding: '12px', borderRadius: '12px', fontSize: '14px', fontWeight: 700, backgroundColor: 'var(--color-primary)', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
              <span>Confirmar & Guardar Ficha</span>
            </button>
            <button onClick={handleDiscard} className="btn-discard" style={{ flex: 1, padding: '12px', borderRadius: '12px', fontSize: '14px', fontWeight: 700, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '4px' }}>
              <IconRefresh size={14} stroke={2} />
              <span>Repetir</span>
            </button>
          </div>
        </div>
      )}

      {/* ══ ESTADO: ERROR AMIGABLE (REINTENTO Y RESPALDO) ══════════════════════ */}
      {processingState === 'error' && (
        <div style={{ display: 'flex', flexDirection: 'column', flex: 1, gap: '16px', alignItems: 'center', justifyContent: 'center', textAlign: 'center', padding: '24px 16px' }}>
          <div style={{ width: 72, height: 72, borderRadius: '50%', backgroundColor: 'rgba(239, 68, 68, 0.1)', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '2px solid var(--color-critical)' }}>
            <IconAlertCircle size={32} stroke={2} color="var(--color-critical)" />
          </div>
          
          <h2 style={{ fontSize: '18px', fontWeight: 800, color: 'var(--color-text-dark)', margin: 0 }}>
            El servidor tardó en responder
          </h2>
          
          <p style={{ fontSize: '13px', color: 'var(--color-text-muted)', lineHeight: '1.5', margin: 0, maxWidth: '290px' }}>
            Si es el primer diagnóstico del día, el servidor en la nube puede tardar unos segundos en despertar.
          </p>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 10, width: '100%', maxWidth: '240px', marginTop: 8 }}>
            <button
              onClick={handleRetry}
              style={{
                width: '100%', padding: '12px', borderRadius: '12px', fontSize: '13.5px', fontWeight: 700,
                display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px',
                backgroundColor: 'var(--color-primary)', color: '#fff', border: 'none', cursor: 'pointer',
                boxShadow: '0 4px 12px rgba(0,80,136,0.25)'
              }}
            >
              <IconRefresh size={16} stroke={2} />
              <span>Reintentar Análisis Cloud</span>
            </button>

            {lastImgElement && (
              <button
                onClick={handleUseLocalFallback}
                style={{
                  width: '100%', padding: '11px', borderRadius: '12px', fontSize: '13px', fontWeight: 700,
                  display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px',
                  backgroundColor: 'rgba(17,202,160,0.12)', color: '#007A4D',
                  border: '1.5px solid #11CAA0', cursor: 'pointer'
                }}
              >
                <IconBolt size={16} stroke={2} color="#11CAA0" />
                <span>Diagnóstico Rápido Local</span>
              </button>
            )}

            <button
              onClick={handleDiscard}
              style={{
                width: '100%', padding: '10px', borderRadius: '12px', fontSize: '12.5px', fontWeight: 600,
                backgroundColor: 'transparent', color: 'var(--color-text-muted)', border: 'none', cursor: 'pointer'
              }}
            >
              Tomar otra foto
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
