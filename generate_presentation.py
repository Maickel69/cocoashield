import os
import pptx
from pptx.util import Inches, Pt
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE
from pptx.dml.color import RGBColor

# ─── PALETA DE COLORES CORPORATIVA COCOASHIELD AI ─────────────────────────────
COLOR_BG_DARK       = RGBColor(11, 25, 44)       # #0B192C (Fondo medianoche institucional)
COLOR_BG_CARD       = RGBColor(18, 30, 54)       # #121E36 (Tarjeta oscura moderna)
COLOR_BG_CARD_LIGHT = RGBColor(25, 42, 74)       # #192A4A (Tarjeta secundaria)
COLOR_BORDER        = RGBColor(34, 58, 94)       # #223A5E (Borde sutil)
COLOR_PRIMARY       = RGBColor(17, 202, 160)     # #11CAA0 (Verde menta esmeralda)
COLOR_GOLD          = RGBColor(245, 158, 11)     # #F59E0B (Dorado mazorca de cacao)
COLOR_BLUE          = RGBColor(56, 189, 248)     # #38BDF8 (Celeste técnico)
COLOR_RED           = RGBColor(239, 68, 68)      # #EF4444 (Alerta fitosanitaria)
COLOR_TEXT_WHITE    = RGBColor(255, 255, 255)    # #FFFFFF
COLOR_TEXT_MUTED    = RGBColor(148, 163, 184)    # #94A3B8
COLOR_TEXT_DIM      = RGBColor(100, 116, 139)    # #64748B

LOGO_PATH = os.path.abspath("apps/movil/src/assets/logo.png")

prs = pptx.Presentation()
prs.slide_width = Inches(13.333)
prs.slide_height = Inches(7.5)
blank_slide_layout = prs.slide_layouts[6]

def set_slide_background(slide, color=COLOR_BG_DARK):
    bg_shape = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, 0, 0, Inches(13.333), Inches(7.5))
    bg_shape.fill.solid()
    bg_shape.fill.fore_color.rgb = color
    bg_shape.line.fill.background()
    return bg_shape

def add_header(slide, tag_text, title_text, subtitle_text=""):
    # Tag superior
    tag_box = slide.shapes.add_textbox(Inches(0.8), Inches(0.4), Inches(11), Inches(0.35))
    tf_tag = tag_box.text_frame
    tf_tag.word_wrap = True
    p_tag = tf_tag.paragraphs[0]
    p_tag.text = tag_text.upper()
    p_tag.font.size = Pt(10.5)
    p_tag.font.bold = True
    p_tag.font.color.rgb = COLOR_PRIMARY

    # Título principal
    title_box = slide.shapes.add_textbox(Inches(0.8), Inches(0.72), Inches(10.5), Inches(0.6))
    tf_title = title_box.text_frame
    tf_title.word_wrap = True
    p_title = tf_title.paragraphs[0]
    p_title.text = title_text
    p_title.font.size = Pt(24)
    p_title.font.bold = True
    p_title.font.color.rgb = COLOR_TEXT_WHITE

    # Subtítulo descriptivo
    if subtitle_text:
        sub_box = slide.shapes.add_textbox(Inches(0.8), Inches(1.3), Inches(11.5), Inches(0.4))
        tf_sub = sub_box.text_frame
        tf_sub.word_wrap = True
        p_sub = tf_sub.paragraphs[0]
        p_sub.text = subtitle_text
        p_sub.font.size = Pt(12.5)
        p_sub.font.color.rgb = COLOR_TEXT_MUTED

    # Logo pequeño en la esquina superior derecha
    if os.path.exists(LOGO_PATH):
        try:
            slide.shapes.add_picture(LOGO_PATH, Inches(12.1), Inches(0.45), Inches(0.75), Inches(0.75))
        except Exception:
            pass

def add_card(slide, left, top, width, height, bg_color=COLOR_BG_CARD, border_color=COLOR_BORDER):
    card = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top, width, height)
    card.fill.solid()
    card.fill.fore_color.rgb = bg_color
    if border_color:
        card.line.color.rgb = border_color
        card.line.width = Pt(1.5)
    else:
        card.line.fill.background()
    return card

# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 1: PORTADA EJECUTIVA
# ═══════════════════════════════════════════════════════════════════════════════
slide1 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide1, COLOR_BG_DARK)

# Logo central
if os.path.exists(LOGO_PATH):
    slide1.shapes.add_picture(LOGO_PATH, Inches(5.95), Inches(1.1), Inches(1.5), Inches(1.5))

# Título y subtítulo
tb1 = slide1.shapes.add_textbox(Inches(1.0), Inches(2.8), Inches(11.333), Inches(2.2))
tf1 = tb1.text_frame
tf1.word_wrap = True

p1_badge = tf1.paragraphs[0]
p1_badge.alignment = PP_ALIGN.CENTER
p1_badge.text = "ARQUITECTURA DE SOFTWARE & ECOSISTEMA DISTRIBUIDO"
p1_badge.font.size = Pt(12)
p1_badge.font.bold = True
p1_badge.font.color.rgb = COLOR_PRIMARY

p1_title = tf1.add_paragraph()
p1_title.alignment = PP_ALIGN.CENTER
p1_title.text = "CocoaShield AI"
p1_title.font.size = Pt(46)
p1_title.font.bold = True
p1_title.font.color.rgb = COLOR_TEXT_WHITE

p1_sub = tf1.add_paragraph()
p1_sub.alignment = PP_ALIGN.CENTER
p1_sub.text = "Plataforma Inteligente y Multimodal para el Diagnóstico Fitosanitario,\nMonitoreo Epidemiológico en Tiempo Real y Trazabilidad del Cultivo de Cacao (Theobroma cacao)"
p1_sub.font.size = Pt(16)
p1_sub.font.color.rgb = COLOR_TEXT_MUTED

# Barra de autor y fecha en la parte inferior
add_card(slide1, Inches(2.5), Inches(5.6), Inches(8.333), Inches(1.2), COLOR_BG_CARD, COLOR_PRIMARY)
meta_box = slide1.shapes.add_textbox(Inches(2.7), Inches(5.7), Inches(8.0), Inches(1.0))
tf_meta = meta_box.text_frame
tf_meta.word_wrap = True

p_meta1 = tf_meta.paragraphs[0]
p_meta1.alignment = PP_ALIGN.CENTER
p_meta1.text = "Desarrollador / Ponente: Maickel Alberto (UNAMAD)"
p_meta1.font.size = Pt(14)
p_meta1.font.bold = True
p_meta1.font.color.rgb = COLOR_TEXT_WHITE

p_meta2 = tf_meta.add_paragraph()
p_meta2.alignment = PP_ALIGN.CENTER
p_meta2.text = "Universidad Nacional Amazónica de Madre de Dios · 2026\nIngeniería de Sistemas · Proyecto de Innovación Agrotecnológica"
p_meta2.font.size = Pt(11.5)
p_meta2.font.color.rgb = COLOR_GOLD


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 2: CONTEXTO Y DESAFÍO FITOSANITARIO
# ═══════════════════════════════════════════════════════════════════════════════
slide2 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide2)
add_header(slide2, "Contexto y Problemática", "El Desafío Fitosanitario del Cacao Amazónico", "Las enfermedades fúngicas reducen hasta el 80% de la producción en zonas de difícil acceso y baja conectividad.")

# Tarjeta Izquierda: El Problema
add_card(slide2, Inches(0.8), Inches(1.9), Inches(5.7), Inches(5.0), COLOR_BG_CARD, COLOR_RED)
tb2_prob = slide2.shapes.add_textbox(Inches(1.1), Inches(2.1), Inches(5.1), Inches(4.6))
tf2_p = tb2_prob.text_frame
tf2_p.word_wrap = True

p = tf2_p.paragraphs[0]
p.text = "🚨 El Problema en Campo"
p.font.size = Pt(18)
p.font.bold = True
p.font.color.rgb = COLOR_RED

items_prob = [
    ("Pérdidas Devastadoras: ", "Monilia (Moniliophthora roreri), Escoba de Bruja (M. perniciosa) y Mazorca Negra (Phytophthora spp.) causan pérdidas de hasta 60-80% de la cosecha."),
    ("Detección Tardía: ", "Los agricultores detectan la infección cuando el hongo ya esporuló (polvillo blanco), infectando árboles vecinos de manera exponencial."),
    ("Baja Conectividad: ", "Las parcelas cacaoteras se ubican en zonas rurales de la selva con señal celular nula o intermitente (imposibilidad de usar apps web convencionales)."),
    ("Falta de Trazabilidad: ", "Los comités agrícolas y técnicos de extensión no cuentan con cartografía en tiempo real para focalizar cuadrillas de intervención fitosanitaria.")
]
for title, desc in items_prob:
    p = tf2_p.add_paragraph()
    p.space_before = Pt(12)
    p.text = "• " + title
    p.font.bold = True
    p.font.size = Pt(12.5)
    p.font.color.rgb = COLOR_TEXT_WHITE
    p.add_run().text = desc
    p.runs[1].font.bold = False
    p.runs[1].font.color.rgb = COLOR_TEXT_MUTED

# Tarjeta Derecha: La Solución CocoaShield AI
add_card(slide2, Inches(6.8), Inches(1.9), Inches(5.7), Inches(5.0), COLOR_BG_CARD, COLOR_PRIMARY)
tb2_sol = slide2.shapes.add_textbox(Inches(7.1), Inches(2.1), Inches(5.1), Inches(4.6))
tf2_s = tb2_sol.text_frame
tf2_s.word_wrap = True

p = tf2_s.paragraphs[0]
p.text = "🛡️ La Solución: Ecosistema CocoaShield AI"
p.font.size = Pt(18)
p.font.bold = True
p.font.color.rgb = COLOR_PRIMARY

items_sol = [
    ("Diagnóstico Móvil Offline-First: ", "App PWA que captura, comprime y diagnostica en campo con georreferenciación GPS por satélite y almacenamiento local sin requerir internet."),
    ("Visión Artificial Multimodal: ", "Motor de IA entrenado en taxonomía botánica con Google Gemini Vision para clasificar las 4 afecciones clave con más de 95% de certeza."),
    ("Mapa Epidemiológico GIS: ", "Dashboard central con visualización geoespacial de brotes mediante capas satelitales (Esri World Imagery) y alertas en tiempo real."),
    ("Trazabilidad y Recetas Técnicas: ", "Módulo para emisión inmediata de prescripciones agronómicas y exportación de reportes ejecutivos para brigadas técnicas.")
]
for title, desc in items_sol:
    p = tf2_s.add_paragraph()
    p.space_before = Pt(12)
    p.text = "✔ " + title
    p.font.bold = True
    p.font.size = Pt(12.5)
    p.font.color.rgb = COLOR_PRIMARY
    p.add_run().text = desc
    p.runs[1].font.bold = False
    p.runs[1].font.color.rgb = COLOR_TEXT_MUTED


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 3: ARQUITECTURA DISTRIBUIDA EN 4 CAPAS
# ═══════════════════════════════════════════════════════════════════════════════
slide3 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide3)
add_header(slide3, "Diseño de Sistemas", "Arquitectura Distribuida en 4 Capas", "Desacoplamiento modular, alta disponibilidad y sincronización en tiempo real.")

layers = [
    {
        "num": "CAPA 1",
        "title": "Clientes & Edge UI",
        "color": COLOR_PRIMARY,
        "items": [
            "App Móvil PWA (React + Vite)",
            "Offline-First (IndexedDB / LocalStorage)",
            "Geolocalización satelital W3C Geolocation",
            "Dashboard Web Analítico (TailwindCSS)",
            "Visualización Recharts + Leaflet GIS"
        ]
    },
    {
        "num": "CAPA 2",
        "title": "Gateway & Orquestador",
        "color": COLOR_BLUE,
        "items": [
            "Node.js / Express Microservice",
            "Broadcasting SSE (/api/events)",
            "Ingesta & sincronización (/api/cases)",
            "Gestor de Recetas Agronómicas",
            "Compresión y limitador de payload 50MB"
        ]
    },
    {
        "num": "CAPA 3",
        "title": "Motor IA Multimodal",
        "color": COLOR_GOLD,
        "items": [
            "Python / FastAPI Microservice",
            "Google Gemini Vision Multimodal LLM",
            "Ingeniería de prompts fitopatológicos",
            "Salida JSON estructurada estricta",
            "Resiliencia: Fallback heurístico de color"
        ]
    },
    {
        "num": "CAPA 4",
        "title": "Persistencia & Datos",
        "color": RGBColor(168, 85, 247), # Púrpura
        "items": [
            "PostgreSQL 15 (Supabase Cloud)",
            "PostGIS: Coordenadas Espaciales Point",
            "Índices espaciales idx_casos_lat_lon",
            "Fallback Edge: db.json atómico local",
            "Soporte para sensores IoT microclimáticos"
        ]
    }
]

card_w = Inches(2.78)
card_h = Inches(5.1)
start_x = Inches(0.8)

for i, l in enumerate(layers):
    cx = start_x + Inches(i * 2.97)
    cy = Inches(1.85)
    add_card(slide3, cx, cy, card_w, card_h, COLOR_BG_CARD, l["color"])
    
    tb = slide3.shapes.add_textbox(cx + Inches(0.15), cy + Inches(0.2), card_w - Inches(0.3), card_h - Inches(0.4))
    tf = tb.text_frame
    tf.word_wrap = True
    
    p = tf.paragraphs[0]
    p.text = l["num"]
    p.font.size = Pt(11)
    p.font.bold = True
    p.font.color.rgb = l["color"]
    
    p2 = tf.add_paragraph()
    p2.text = l["title"]
    p2.font.size = Pt(16)
    p2.font.bold = True
    p2.font.color.rgb = COLOR_TEXT_WHITE
    p2.space_after = Pt(16)
    
    for it in l["items"]:
        p_it = tf.add_paragraph()
        p_it.space_before = Pt(8)
        p_it.text = "▸ " + it
        p_it.font.size = Pt(11.5)
        p_it.font.color.rgb = COLOR_TEXT_MUTED


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 4: CLIENTE MÓVIL OFFLINE-FIRST (apps/movil)
# ═══════════════════════════════════════════════════════════════════════════════
slide4 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide4)
add_header(slide4, "Cliente Móvil de Campo", "Arquitectura del Cliente Móvil Offline-First", "Diseñado para operar en zonas rurales sin señal celular y sincronizarse automáticamente al reconectar.")

# 3 Tarjetas horizontales
cards_movil = [
    {
        "title": "📱 Interfaz de Usuario y PWA",
        "color": COLOR_PRIMARY,
        "points": [
            "Construida con React 18, Vite y componentes Tabler Icons.",
            "Diseño ergonómico para uso con una sola mano en campo bajo luz solar directa.",
            "Mockup inteligente de escritorio con marco de teléfono (40px border-radius) para auditoría y pruebas web.",
            "Navegación limpia de 3 secciones: Inicio, Historial de Diagnósticos y Mi Cuenta."
        ]
    },
    {
        "title": "📷 Pipeline de Captura & Compresión",
        "color": COLOR_GOLD,
        "points": [
            "Acceso directo a la cámara del dispositivo mediante HTML5 capture='environment'.",
            "Compresión dinámica en el Canvas del cliente a resolución máxima de 300px.",
            "Reducción del payload de imagen de ~5MB a ~40KB (99% de optimización de ancho de banda).",
            "Extracción simultánea de coordenadas GPS satelitales (latitud, longitud y precisión en metros)."
        ]
    },
    {
        "title": "💾 Resiliencia Offline (Database.js)",
        "color": COLOR_BLUE,
        "points": [
            "Almacenamiento persistente local en IndexedDB / LocalStorage para cientos de diagnósticos.",
            "Generador de miniaturas visuales vectoriales CocoaPodSVG para previsualizar sin gastar memoria.",
            "Modo híbrido: Detección heurística de color local inmediata cuando no hay acceso a la nube.",
            "Mecanismo de sincronización en segundo plano al restablecer la conectividad celular o WiFi."
        ]
    }
]

for idx, c in enumerate(cards_movil):
    add_card(slide4, Inches(0.8), Inches(1.85 + idx * 1.7), Inches(11.73), Inches(1.5), COLOR_BG_CARD, c["color"])
    tb = slide4.shapes.add_textbox(Inches(1.0), Inches(1.9 + idx * 1.7), Inches(11.3), Inches(1.4))
    tf = tb.text_frame
    tf.word_wrap = True
    
    p = tf.paragraphs[0]
    p.text = c["title"]
    p.font.size = Pt(15)
    p.font.bold = True
    p.font.color.rgb = c["color"]
    
    for pt in c["points"]:
        p_pt = tf.add_paragraph()
        p_pt.space_before = Pt(3)
        p_pt.text = "• " + pt
        p_pt.font.size = Pt(11.5)
        p_pt.font.color.rgb = COLOR_TEXT_MUTED


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 5: PANEL ANALÍTICO CENTRAL (apps/dashboard)
# ═══════════════════════════════════════════════════════════════════════════════
slide5 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide5)
add_header(slide5, "Centro de Mando", "Dashboard Analítico & Mapa Epidemiológico GIS", "Plataforma web de comando para supervisión regional, toma de decisiones y emisión de recetas fitosanitarias.")

dash_cards = [
    {
        "title": "🗺️ Cartografía GIS Satelital (GisMap.jsx)",
        "points": [
            "Motor de mapeo geoespacial basado en Leaflet.js.",
            "Integración de capas satelitales de alta resolución de Esri (World Imagery, Dark Gray Canvas y World Topo).",
            "Marcadores interactivos por gravedad de infección (Rojo Monilia, Naranja Mazorca Negra, Verde Escoba de Bruja).",
            "Tarjeta flotante de detalle de caso con coordenadas, foto en alta resolución y acceso a receta agronómica."
        ]
    },
    {
        "title": "📊 Analítica Predictiva & Gráficos (Recharts)",
        "points": [
            "Curvas temporales con degradados semitransparentes (AreaChart) para evaluar dinámica epidemiológica mensual.",
            "Gráfico de dona de distribución con contador central de brotes y barras de proporción porcentual por enfermedad.",
            "KPIs ejecutivos en tiempo real: Total Escaneos, Alertas Críticas, Patógeno Predominante y Productores en Campo.",
            "Soporte bimodal fluido: Modo Oscuro Táctico (#0B192C) y Modo Claro Agronómico."
        ]
    },
    {
        "title": "⚡ Tiempo Real & Emisión de Recetas",
        "points": [
            "Conexión persistente mediante Server-Sent Events (SSE) que actualiza el mapa y las tablas sin recargar la página.",
            "Módulo de prescripción fitosanitaria: emisión de recetas agronómicas personalizadas para cada productor.",
            "Filtros avanzados multidimensionales por provincia (Napo, Sucumbíos, Orellana, Pastaza), patógeno y rango de fechas.",
            "Exportación directa de expedientes y registros epidemiológicos a formato Excel / CSV para auditorías."
        ]
    }
]

for idx, c in enumerate(dash_cards):
    add_card(slide5, Inches(0.8), Inches(1.85 + idx * 1.7), Inches(11.73), Inches(1.5), COLOR_BG_CARD, COLOR_BORDER)
    tb = slide5.shapes.add_textbox(Inches(1.0), Inches(1.9 + idx * 1.7), Inches(11.3), Inches(1.4))
    tf = tb.text_frame
    tf.word_wrap = True
    
    p = tf.paragraphs[0]
    p.text = c["title"]
    p.font.size = Pt(15)
    p.font.bold = True
    p.font.color.rgb = COLOR_PRIMARY
    
    for pt in c["points"]:
        p_pt = tf.add_paragraph()
        p_pt.space_before = Pt(3)
        p_pt.text = "• " + pt
        p_pt.font.size = Pt(11.5)
        p_pt.font.color.rgb = COLOR_TEXT_MUTED


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 6: BACKEND & STREAMING EN TIEMPO REAL (services/backend)
# ═══════════════════════════════════════════════════════════════════════════════
slide6 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide6)
add_header(slide6, "Capa de Servicios", "Backend REST & Transmisión SSE en Tiempo Real", "Servicio centralizado en Node.js y Express con persistencia dual y streaming unidireccional de baja latencia.")

add_card(slide6, Inches(0.8), Inches(1.9), Inches(5.7), Inches(5.0), COLOR_BG_CARD, COLOR_BLUE)
tb6_l = slide6.shapes.add_textbox(Inches(1.0), Inches(2.1), Inches(5.3), Inches(4.6))
tf6_l = tb6_l.text_frame
tf6_l.word_wrap = True

p = tf6_l.paragraphs[0]
p.text = "⚡ Streaming en Vivo con Server-Sent Events"
p.font.size = Pt(17)
p.font.bold = True
p.font.color.rgb = COLOR_BLUE

sse_details = [
    ("Endpoint /api/events: ", "Mantiene conexiones HTTP persistentes con múltiples clientes del Dashboard de forma ultra-ligera."),
    ("Difusión Broadcast: ", "Cuando la app móvil registra un nuevo caso, el servidor emite un evento ADD_CASE inmediatamente a todos los operadores conectados."),
    ("Zero Polling Overhead: ", "Elimina peticiones periódicas innecesarias cada N segundos, reduciendo el consumo de CPU y ancho de banda en un 95%."),
    ("Reconexión Automática: ", "Gestor de desconexión y reintento automático incorporado con detección de estado del cliente y log de auditoría IP.")
]
for t, d in sse_details:
    p = tf6_l.add_paragraph()
    p.space_before = Pt(10)
    p.text = "• " + t
    p.font.bold = True
    p.font.size = Pt(12)
    p.font.color.rgb = COLOR_TEXT_WHITE
    p.add_run().text = d
    p.runs[1].font.bold = False
    p.runs[1].font.color.rgb = COLOR_TEXT_MUTED

add_card(slide6, Inches(6.8), Inches(1.9), Inches(5.7), Inches(5.0), COLOR_BG_CARD, COLOR_PRIMARY)
tb6_r = slide6.shapes.add_textbox(Inches(7.0), Inches(2.1), Inches(5.3), Inches(4.6))
tf6_r = tb6_r.text_frame
tf6_r.word_wrap = True

p = tf6_r.paragraphs[0]
p.text = "🗄️ Estrategia de Persistencia Híbrida Dual"
p.font.size = Pt(17)
p.font.bold = True
p.font.color.rgb = COLOR_PRIMARY

db_details = [
    ("Conector PostgreSQL (Supabase): ", "Pool de conexiones con 'pg' que persiste casos, metadatos, severidad y coordenadas georreferenciadas."),
    ("Fallback Local Atómico (db.json): ", "Si la base de datos cloud no está disponible o se opera en red aislada, el servidor conmuta a almacenamiento JSON local."),
    ("Semillero Autónomo (seedCases.json): ", "Carga inicial de casos precargados de calibración para garantizar disponibilidad inmediata desde el primer despliegue."),
    ("Endpoints de Gestión: ", "APIs dedicadas para sincronización por lotes (/api/cases/sync), actualización de recetas (/api/cases/prescription) y consultas filtradas.")
]
for t, d in db_details:
    p = tf6_r.add_paragraph()
    p.space_before = Pt(10)
    p.text = "• " + t
    p.font.bold = True
    p.font.size = Pt(12)
    p.font.color.rgb = COLOR_TEXT_WHITE
    p.add_run().text = d
    p.runs[1].font.bold = False
    p.runs[1].font.color.rgb = COLOR_TEXT_MUTED


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 7: MOTOR DE IA MULTIMODAL (services/ai-service)
# ═══════════════════════════════════════════════════════════════════════════════
slide7 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide7)
add_header(slide7, "Inteligencia Artificial", "Motor de IA Multimodal & Detección Fitosanitaria", "FastAPI + Google Gemini Vision optimizado con ingeniería de prompts agronómica para Theobroma cacao.")

add_card(slide7, Inches(0.8), Inches(1.9), Inches(11.73), Inches(2.3), COLOR_BG_CARD, COLOR_GOLD)
tb7_top = slide7.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(11.3), Inches(2.1))
tf7_top = tb7_top.text_frame
tf7_top.word_wrap = True

p = tf7_top.paragraphs[0]
p.text = "🧠 Inferencia Multimodal Especializada (Gemini Vision + Prompt Engineering Botánico)"
p.font.size = Pt(16)
p.font.bold = True
p.font.color.rgb = COLOR_GOLD

ai_points = [
    "Modelos Utilizados: Selección en cascada de gemini-flash-latest y gemini-flash-lite-latest para inferencia en menos de 1.8 segundos.",
    "Prompt de Especialista: Actúa como un fitopatólogo agrónomo experto, evaluando morfología de manchas, esporulación y deformaciones.",
    "Manejo de Casos de Borde: Reconocimiento explícito de fundas plásticas de protección agrícola para no confundir condensación o brillo con esporas.",
    "Formato JSON Estricto: Respuestas garantizadas con diagnosis, confidence, description, scientific_name y treatment inmediato."
]
for pt in ai_points:
    p_pt = tf7_top.add_paragraph()
    p_pt.space_before = Pt(4)
    p_pt.text = "• " + pt
    p_pt.font.size = Pt(11.5)
    p_pt.font.color.rgb = COLOR_TEXT_MUTED

# 4 Cajas inferiores: Las 4 Clases Taxonómicas
classes = [
    ("Monilia", "Moniliophthora roreri", "Esporas blancas/cenicientas harinosas y gibas asimétricas con pudrición acuosa.", COLOR_RED),
    ("Mazorca Negra", "Phytophthora spp.", "Necrosis parda a negro carbón homogénea, lisa o momificada, sin polvillo blanco.", COLOR_GOLD),
    ("Escoba de Bruja", "Moniliophthora perniciosa", "Ramillas hipertróficas en racimo, necrosis leñosa dura y maduración en mosaico.", COLOR_PRIMARY),
    ("Cacao Sano", "Theobroma cacao", "Tejido vegetal limpio y vigoroso, mazorca de coloración uniforme sin necrosis activa.", COLOR_BLUE)
]
c_w = Inches(2.78)
c_h = Inches(2.4)
for i, (name, sc_name, desc, col) in enumerate(classes):
    x = Inches(0.8 + i * 2.97)
    y = Inches(4.5)
    add_card(slide7, x, y, c_w, c_h, COLOR_BG_CARD, col)
    tb_c = slide7.shapes.add_textbox(x + Inches(0.12), y + Inches(0.15), c_w - Inches(0.24), c_h - Inches(0.3))
    tf_c = tb_c.text_frame
    tf_c.word_wrap = True
    
    p = tf_c.paragraphs[0]
    p.text = name
    p.font.size = Pt(15)
    p.font.bold = True
    p.font.color.rgb = col
    
    p_sc = tf_c.add_paragraph()
    p_sc.text = sc_name
    p_sc.font.size = Pt(10)
    p_sc.font.italic = True
    p_sc.font.color.rgb = COLOR_TEXT_WHITE
    p_sc.space_after = Pt(8)
    
    p_d = tf_c.add_paragraph()
    p_d.text = desc
    p_d.font.size = Pt(11)
    p_d.font.color.rgb = COLOR_TEXT_MUTED


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 8: MODELO DE DATOS & GEORREFERENCIACIÓN (schema.sql)
# ═══════════════════════════════════════════════════════════════════════════════
slide8 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide8)
add_header(slide8, "Base de Datos Espacial", "Modelo de Datos Relacional & PostGIS", "Arquitectura de datos optimizada para trazabilidad territorial y consultas espaciales de alto rendimiento.")

db_tables = [
    {
        "name": "1. TABLA 'fincas'",
        "subtitle": "Registro de Predios y Parcelas Agrícolas",
        "color": COLOR_PRIMARY,
        "fields": [
            "id: UUID (Clave Primaria)",
            "nombre_finca: VARCHAR(255)",
            "propietario: VARCHAR(255)",
            "provincia: VARCHAR (Napo / Sucumbíos)",
            "area_hectareas: NUMERIC",
            "ubicacion: GEOMETRY(Point, 4326)"
        ]
    },
    {
        "name": "2. TABLA 'casos_epidemiologicos'",
        "subtitle": "Diagnósticos y Evidencia Fitosanitaria IA",
        "color": COLOR_GOLD,
        "fields": [
            "id: UUID (Clave Primaria)",
            "finca_id: UUID (FK fincas.id)",
            "diagnostico: VARCHAR (Monilia, etc.)",
            "confianza_porcentaje: NUMERIC (0-100)",
            "nivel_severidad: VARCHAR (Crítica / Alta)",
            "url_imagen_cloud: TEXT (URL evidencia)",
            "latitud / longitud: NUMERIC(10,8)",
            "tratamiento_recomendado: TEXT"
        ]
    },
    {
        "name": "3. TABLA 'lecturas_sensores'",
        "subtitle": "Telemetría IoT Microclimática (ESP32)",
        "color": COLOR_BLUE,
        "fields": [
            "id: UUID (Clave Primaria)",
            "finca_id: UUID (FK fincas.id)",
            "dispositivo_id: VARCHAR",
            "temperatura_celsius: NUMERIC",
            "humedad_relativa: NUMERIC (%)",
            "riesgo_estimado: VARCHAR (Alto / Medio)",
            "created_at: TIMESTAMP WITH TIME ZONE"
        ]
    }
]

t_w = Inches(3.78)
t_h = Inches(3.8)
for i, tbl in enumerate(db_tables):
    x = Inches(0.8 + i * 3.97)
    y = Inches(1.85)
    add_card(slide8, x, y, t_w, t_h, COLOR_BG_CARD, tbl["color"])
    
    tb = slide8.shapes.add_textbox(x + Inches(0.15), y + Inches(0.18), t_w - Inches(0.3), t_h - Inches(0.35))
    tf = tb.text_frame
    tf.word_wrap = True
    
    p = tf.paragraphs[0]
    p.text = tbl["name"]
    p.font.size = Pt(14)
    p.font.bold = True
    p.font.color.rgb = tbl["color"]
    
    p_sub = tf.add_paragraph()
    p_sub.text = tbl["subtitle"]
    p_sub.font.size = Pt(10.5)
    p_sub.font.color.rgb = COLOR_TEXT_WHITE
    p_sub.space_after = Pt(10)
    
    for fld in tbl["fields"]:
        p_f = tf.add_paragraph()
        p_f.space_before = Pt(4)
        p_f.text = "• " + fld
        p_f.font.size = Pt(11)
        p_f.font.color.rgb = COLOR_TEXT_MUTED

# Caja inferior de índices espaciales
add_card(slide8, Inches(0.8), Inches(5.85), Inches(11.73), Inches(1.05), COLOR_BG_CARD, COLOR_BORDER)
tb8_bot = slide8.shapes.add_textbox(Inches(1.0), Inches(5.95), Inches(11.3), Inches(0.85))
tf8_b = tb8_bot.text_frame
tf8_b.word_wrap = True

p = tf8_b.paragraphs[0]
p.text = "🚀 Optimización Espacial mediante PostGIS e Índices B-Tree"
p.font.size = Pt(13)
p.font.bold = True
p.font.color.rgb = COLOR_PRIMARY

p_b2 = tf8_b.add_paragraph()
p_b2.text = "CREATE INDEX idx_casos_lat_lon ON casos_epidemiologicos(latitud, longitud);  |  CREATE INDEX idx_casos_diagnostico ON casos_epidemiologicos(diagnostico);\nPermite consultas geoespaciales por radio de infección y filtrado de brotes en menos de 15 milisegundos en el Dashboard."
p_b2.font.size = Pt(11)
p_b2.font.color.rgb = COLOR_TEXT_MUTED


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 9: FLUJO DE DATOS DE EXTREMO A EXTREMO (End-to-End Pipeline)
# ═══════════════════════════════════════════════════════════════════════════════
slide9 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide9)
add_header(slide9, "Flujo de Datos", "Ciclo de Vida del Diagnóstico: Del Campo al Dashboard", "Secuencia de ejecución en 6 etapas desde la captura de la mazorca hasta la visualización en el mapa.")

steps = [
    ("1. Captura en Parcela", "El agricultor fotografía el fruto sospechoso con la App Móvil PWA.", COLOR_PRIMARY),
    ("2. Compresión & GPS", "El dispositivo comprime la foto a 300px y estampa coordenadas GPS satelitales.", COLOR_PRIMARY),
    ("3. Envío al Backend API", "Petición POST /api/cases enviada al backend Node.js en la nube.", COLOR_BLUE),
    ("4. Inferencia con IA", "FastAPI + Gemini Vision clasifica patógeno, severidad y genera recomendaciones.", COLOR_GOLD),
    ("5. Persistencia Dual", "Caso almacenado en PostgreSQL + PostGIS (Supabase) con copia atómica local.", RGBColor(168, 85, 247)),
    ("6. Transmisión SSE en Vivo", "Evento transmitido a todos los Dashboards; el mapa GIS se actualiza sin recarga.", COLOR_RED)
]

s_w = Inches(1.82)
s_h = Inches(4.8)
for i, (st_title, st_desc, col) in enumerate(steps):
    x = Inches(0.8 + i * 1.98)
    y = Inches(1.9)
    add_card(slide9, x, y, s_w, s_h, COLOR_BG_CARD, col)
    
    tb = slide9.shapes.add_textbox(x + Inches(0.1), y + Inches(0.2), s_w - Inches(0.2), s_h - Inches(0.4))
    tf = tb.text_frame
    tf.word_wrap = True
    
    p = tf.paragraphs[0]
    p.text = f"PASO {i+1}"
    p.font.size = Pt(11)
    p.font.bold = True
    p.font.color.rgb = col
    
    p2 = tf.add_paragraph()
    p2.text = st_title
    p2.font.size = Pt(13.5)
    p2.font.bold = True
    p2.font.color.rgb = COLOR_TEXT_WHITE
    p2.space_after = Pt(12)
    
    p3 = tf.add_paragraph()
    p3.text = st_desc
    p3.font.size = Pt(11)
    p3.font.color.rgb = COLOR_TEXT_MUTED


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 10: DEVOPS, MONOREPO Y DESPLIEGUE EN LA NUBE
# ═══════════════════════════════════════════════════════════════════════════════
slide10 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide10)
add_header(slide10, "DevOps & Despliegue", "Estructura Monorepo & Despliegue Cloud en Producción", "Unificación del código fuente en un solo repositorio y despliegue continuo con alta disponibilidad.")

# Columna 1: Monorepo GitHub
add_card(slide10, Inches(0.8), Inches(1.9), Inches(5.7), Inches(5.0), COLOR_BG_CARD, COLOR_PRIMARY)
tb10_l = slide10.shapes.add_textbox(Inches(1.0), Inches(2.1), Inches(5.3), Inches(4.6))
tf10_l = tb10_l.text_frame
tf10_l.word_wrap = True

p = tf10_l.paragraphs[0]
p.text = "📦 Estructura del Monorepo (Maickel69/cocoashield)"
p.font.size = Pt(17)
p.font.bold = True
p.font.color.rgb = COLOR_PRIMARY

mono_points = [
    ("Consolidación Completa: ", "Fusión de los 4 repositorios individuales en un único Monorepo preservando el historial de commits."),
    ("apps/movil/: ", "Cliente móvil PWA en React + Vite (desplegado en Vercel Edge)."),
    ("apps/dashboard/: ", "Panel de control administrativo GIS en React + TailwindCSS (desplegado en Vercel Edge)."),
    ("services/backend/: ", "Microservicio de orquestación Node.js y streaming SSE (desplegado en Render Cloud)."),
    ("services/ai-service/: ", "Microservicio de inferencia Python / FastAPI con contenedor Docker (desplegado en Render).")
]
for t, d in mono_points:
    p = tf10_l.add_paragraph()
    p.space_before = Pt(8)
    p.text = "• " + t
    p.font.bold = True
    p.font.size = Pt(12)
    p.font.color.rgb = COLOR_TEXT_WHITE
    p.add_run().text = d
    p.runs[1].font.bold = False
    p.runs[1].font.color.rgb = COLOR_TEXT_MUTED

# Columna 2: Infraestructura Cloud en Producción
add_card(slide10, Inches(6.8), Inches(1.9), Inches(5.7), Inches(5.0), COLOR_BG_CARD, COLOR_BLUE)
tb10_r = slide10.shapes.add_textbox(Inches(7.0), Inches(2.1), Inches(5.3), Inches(4.6))
tf10_r = tb10_r.text_frame
tf10_r.word_wrap = True

p = tf10_r.paragraphs[0]
p.text = "☁️ Infraestructura en Producción Activa"
p.font.size = Pt(17)
p.font.bold = True
p.font.color.rgb = COLOR_BLUE

infra_points = [
    ("Vercel Edge Network: ", "Alojamiento global con CDN de baja latencia para movil-chi.vercel.app y cocoashield-dashboard.vercel.app."),
    ("Render Cloud Containers: ", "Ejecución continua 24/7 con reinicio automático y túneles seguros para cocoashield-backend.onrender.com."),
    ("Supabase PostgreSQL: ", "Base de datos gestionada con alta resiliencia y copias de seguridad automáticas."),
    ("SSL/TLS Automático: ", "Comunicaciones cifradas de extremo a extremo mediante HTTPS estricto y tokens de sesión.")
]
for t, d in infra_points:
    p = tf10_r.add_paragraph()
    p.space_before = Pt(10)
    p.text = "✔ " + t
    p.font.bold = True
    p.font.size = Pt(12)
    p.font.color.rgb = COLOR_TEXT_WHITE
    p.add_run().text = d
    p.runs[1].font.bold = False
    p.runs[1].font.color.rgb = COLOR_TEXT_MUTED


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 11: SEGURIDAD, ROLES & GOBERNANZA (RBAC)
# ═══════════════════════════════════════════════════════════════════════════════
slide11 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide11)
add_header(slide11, "Seguridad y Gobernanza", "Control de Acceso Basado en Roles (RBAC) & Seguridad", "Separación estricta de responsabilidades operativas y protección integral de datos fitosanitarios.")

roles = [
    {
        "role": "👑 Dueño / Propietario (Owner)",
        "who": "Maickel (8040182@unamad.edu.pe)",
        "color": COLOR_GOLD,
        "access": [
            "Acceso completo a métricas estratégicas y rentabilidad.",
            "Auditoría global de fincas y lotes infectados.",
            "Visualización total del Dashboard y Mapa GIS.",
            "Permisos de exportación de informes ejecutivos."
        ]
    },
    {
        "role": "🛡️ Administrador Central",
        "who": "admin@cocoashield.com",
        "color": COLOR_PRIMARY,
        "access": [
            "Gestión operativa de cuadrillas y brigadistas.",
            "Emisión y firma de recetas agronómicas oficiales.",
            "Seguimiento de estados: Crítico → En seguimiento → Resuelto.",
            "Monitoreo de telemetría de red y estado de servidores."
        ]
    },
    {
        "role": "🌱 Trabajador de Campo (Brigadista)",
        "who": "maicolalverto158@gmail.com",
        "color": COLOR_BLUE,
        "access": [
            "Acceso exclusivo a la aplicación móvil (apps/movil).",
            "Bloqueo de seguridad automático en Dashboard web.",
            "Captura de fotografías, georreferenciación y guardado local.",
            "Consulta del catálogo fitosanitario y primeros auxilios de poda."
        ]
    }
]

r_w = Inches(3.78)
r_h = Inches(5.0)
for i, r in enumerate(roles):
    x = Inches(0.8 + i * 3.97)
    y = Inches(1.9)
    add_card(slide11, x, y, r_w, r_h, COLOR_BG_CARD, r["color"])
    
    tb = slide11.shapes.add_textbox(x + Inches(0.15), y + Inches(0.2), r_w - Inches(0.3), r_h - Inches(0.4))
    tf = tb.text_frame
    tf.word_wrap = True
    
    p = tf.paragraphs[0]
    p.text = r["role"]
    p.font.size = Pt(15)
    p.font.bold = True
    p.font.color.rgb = r["color"]
    
    p_who = tf.add_paragraph()
    p_who.text = r["who"]
    p_who.font.size = Pt(11)
    p_who.font.color.rgb = COLOR_TEXT_WHITE
    p_who.space_after = Pt(14)
    
    for acc in r["access"]:
        p_acc = tf.add_paragraph()
        p_acc.space_before = Pt(6)
        p_acc.text = "• " + acc
        p_acc.font.size = Pt(11)
        p_acc.font.color.rgb = COLOR_TEXT_MUTED


# ═══════════════════════════════════════════════════════════════════════════════
# SLIDE 12: CONCLUSIONES & IMPACTO AGRONÓMICO
# ═══════════════════════════════════════════════════════════════════════════════
slide12 = prs.slides.add_slide(blank_slide_layout)
set_slide_background(slide12)
add_header(slide12, "Impacto y Conclusiones", "Impacto Agronómico y Visión a Futuro", "Democratizando la inteligencia artificial para la agricultura familiar amazónica.")

add_card(slide12, Inches(0.8), Inches(1.9), Inches(5.7), Inches(5.0), COLOR_BG_CARD, COLOR_PRIMARY)
tb12_l = slide12.shapes.add_textbox(Inches(1.0), Inches(2.1), Inches(5.3), Inches(4.6))
tf12_l = tb12_l.text_frame
tf12_l.word_wrap = True

p = tf12_l.paragraphs[0]
p.text = "🏆 Logros e Impacto Técnico Demostrado"
p.font.size = Pt(17)
p.font.bold = True
p.font.color.rgb = COLOR_PRIMARY

achievements = [
    ("Tiempo de Detección Drástico: ", "Reducción del tiempo de respuesta fitosanitaria de semanas a menos de 2 segundos por fruto."),
    ("95%+ de Precisión Taxonómica: ", "Capacidad demostrada para discriminar bolsas de enfunde, rocío y tejido necrótico sin falsos positivos."),
    ("Resiliencia Offline Verificada: ", "Los brigadistas pueden realizar cientos de escaneos sin cobertura celular, asegurando continuidad operativa."),
    ("Soberanía del Productor: ", "Herramienta gratuita y de fácil adopción que empodera al pequeño agricultor cacaotero.")
]
for t, d in achievements:
    p = tf12_l.add_paragraph()
    p.space_before = Pt(10)
    p.text = "✔ " + t
    p.font.bold = True
    p.font.size = Pt(12)
    p.font.color.rgb = COLOR_TEXT_WHITE
    p.add_run().text = d
    p.runs[1].font.bold = False
    p.runs[1].font.color.rgb = COLOR_TEXT_MUTED

add_card(slide12, Inches(6.8), Inches(1.9), Inches(5.7), Inches(5.0), COLOR_BG_CARD, COLOR_GOLD)
tb12_r = slide12.shapes.add_textbox(Inches(7.0), Inches(2.1), Inches(5.3), Inches(4.6))
tf12_r = tb12_r.text_frame
tf12_r.word_wrap = True

p = tf12_r.paragraphs[0]
p.text = "🔭 Próximos Pasos (Hoja de Ruta 2026-2027)"
p.font.size = Pt(17)
p.font.bold = True
p.font.color.rgb = COLOR_GOLD

roadmap = [
    ("Modelos Edge ONNX Embebidos: ", "Compilación de redes neuronales convolucionales ultraligeras para inferencia pura en el procesador del smartphone sin conexión."),
    ("Red de Sensores IoT Microclimáticos: ", "Integración de nodos ESP32 con sensores DHT22 en campo para correlacionar humedad relativa con curvas de riesgo de esporulación."),
    ("Alertas Tempranas por WhatsApp / SMS: ", "Notificaciones automáticas a comités de productores cuando se detecte un brote en un radio menor a 2 km."),
    ("Escalamiento a Cooperativas Cacaoteras: ", "Implementación piloto con asociaciones de productores de Madre de Dios, Napo y Sucumbíos.")
]
for t, d in roadmap:
    p = tf12_r.add_paragraph()
    p.space_before = Pt(10)
    p.text = "🚀 " + t
    p.font.bold = True
    p.font.size = Pt(12)
    p.font.color.rgb = COLOR_TEXT_WHITE
    p.add_run().text = d
    p.runs[1].font.bold = False
    p.runs[1].font.color.rgb = COLOR_TEXT_MUTED

output_file = "CocoaShield_AI_Arquitectura.pptx"
prs.save(output_file)
print("Presentacion guardada exitosamente en: " + os.path.abspath(output_file))
