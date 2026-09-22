# Informe Técnico: CocoaShield AI (Sistema de Diagnóstico Fitosanitario de Cacao)

CocoaShield es un ecosistema tecnológico integrado (Móvil + Web + Inteligencia Artificial) diseñado para la detección temprana, geolocalización y control epidemiológico de las principales enfermedades que afectan al cultivo de cacao (*Theobroma cacao*) en entornos agrícolas latinoamericanos, tales como la **Moniliasis** (*Moniliophthora roreri*), la **Escoba de Bruja** (*Crinipellis perniciosa*) y la **Mazorca Negra** (*Phytophthora palmivora*).

El sistema destaca por combinar inferencia de redes neuronales profundas (Deep Learning), arquitectura **Offline-First** y mecanismos de **auto-conexión zero-configuration** en redes locales o hotspots compartidos de campo.

---

## 1. Arquitectura del Ecosistema

El ecosistema CocoaShield está dividido en tres capas principales que colaboran de manera transparente:

```mermaid
graph TD
    subgraph Celular ["Dispositivo Móvil (Aplicación Híbrida)"]
        UI[Interfaz React / Vite]
        DB_Loc[Caché de Respaldo SQLite/LocalStorage]
        Cap[Capacitor Native Bridge]
    end

    subgraph Servidor ["Servidor Central (PC Local)"]
        Exp[Servidor Express API & Estáticos]
        DB_Serv[Base de Datos de Casos (db.json)]
        PyEngine[Motor de IA Python CLI]
        TF[Modelo ResNet-50 Keras/TensorFlow]
    end

    subgraph Técnico ["Panel de Control (Web Dashboard)"]
        Dash[Dashboard Analítico Web]
        Charts[Gráficos de Tendencias e Incidencia]
    end

    UI -->|1. Envío Foto Base64| Exp
    Exp -->|2. Invoca CLI| PyEngine
    PyEngine -->|3. Inferencia Real / Simulación| TF
    TF -->|4. Retorna Clasificación| Exp
    Exp -->|5. Retorna Diagnóstico JSON| UI
    UI -->|6. SQLite Sync / Guardado Local| DB_Loc
    Exp -->|7. Push en Tiempo Real SSE| Dash
    Dash -->|8. Visualización de Reportes| DB_Serv
```

---

## 2. Características Clave de la Aplicación Móvil

### A. Diagnóstico Inteligente de Campo (IA Centralizada)
La aplicación captura fotos de frutos u hojas afectadas y las envía al servidor central.
* **Modelo Utilizado:** **ResNet-50** entrenada sobre un dataset fitosanitario de cacao.
* **Procesamiento:** La inferencia se ejecuta en la PC del servidor (Python/TensorFlow) a través de decodificación Base64, retornando el porcentaje de certeza, detalles del patógeno e instrucciones de manejo inmediato.
* **Motor de Respaldo Heurístico:** Incorpora un motor de análisis de color y reflectancia física en píxeles (basado en canales RGB) en el script Python para demostraciones y diagnósticos veloces si los archivos de pesos del modelo no están cargados en memoria.

### B. Arquitectura Offline-First con Base de Datos Local
Diseñado para la realidad del campo agrícola donde la conectividad celular es inestable o nula.
* **Caché Local:** Los diagnósticos y ubicaciones GPS se guardan localmente en un formato compatible con SQLite (emulado en LocalStorage).
* **Cola de Sincronización:** Si no hay conexión al servidor, el registro se marca como "Pendiente". En cuanto el teléfono detecta conexión al servidor, el sincronizador sube automáticamente todas las fichas pendientes en segundo plano.

### C. Conectividad Adaptativa y Auto-Descubrimiento
Elimina la necesidad de escanear códigos QR o configurar manualmente direcciones IP al cambiar de red.
* **Detección Dinámica:** El servidor escanea sus interfaces de red (Wi-Fi, adaptadores virtuales, VPNs corporativas) y publica las IPs activas en un registro en la nube en tiempo real.
* **Pings en Paralelo:** La app móvil descarga las coordenadas de red y realiza peticiones rápidas concurrentes (pings en paralelo con 1.5s de timeout) a todos los adaptadores locales para establecer la conexión en segundos.
* **Bypass de Restricciones del Navegador (HTTPS/Mixed Content):** Al ejecutarse la aplicación en el mismo entorno de red y protocolo sobre HTTP (puerto `5000` y Vite `5173`), se evitan todas las advertencias de certificados de seguridad y bloqueos de seguridad del navegador.

### D. Geolocalización Fitosanitaria de Alta Precisión
* Utiliza el hardware GPS del dispositivo móvil para capturar las coordenadas geográficas exactas (Latitud y Longitud) de cada mazorca o árbol enfermo evaluado, permitiendo mapear la propagación espacial del patógeno.

---

## 3. Especificaciones del Panel de Control (Web Dashboard)

El panel analítico de escritorio actúa como la consola central para los ingenieros agrónomos y técnicos de campo:
* **Visualización de Brotes en Tiempo Real:** Recibe notificaciones mediante **Server-Sent Events (SSE)** al instante en que un agricultor guarda una ficha en el campo.
* **Modo Oscuro Integrado:** Interfaz de alto contraste adaptada para reducir la fatiga visual de los técnicos durante las revisiones nocturnas en oficinas de campo.
* **Gestión de Reportes y Recetas Técnicas:** Permite a los técnicos firmar recetas electrónicas con prescripciones agrícolas personalizadas y cambiar el estado del terreno (Crítico, En seguimiento, Resuelto).
* **Exportación de Datos:** Descarga de reportes epidemiológicos filtrados por región, fecha o tipo de cultivo directamente a formato binario Microsoft Excel (`.xlsx`) mediante SheetJS.

---

## 4. Clasificación Fitosanitaria y Diagnósticos Incorporados

La aplicación clasifica de manera automática el material evaluado en cuatro categorías críticas:

| Enfermedad / Estado | Patógeno Causante | Gravedad | Sintomatología Clave | Acción Recomendada de Control |
| :--- | :--- | :---: | :--- | :--- |
| **Monilia del Cacao** | *Moniliophthora roreri* | **Crítica** | Polvo blanco/ceniza, mazorca pesada y dura, granos podridos. | Retiro de frutos antes de la esporulación, enterrar bajo hojarasca. |
| **Escoba de Bruja** | *Crinipellis perniciosa* | **Alta** | Deformación de ramas en racimo, mazorcas secas y leñosas. | Poda fitosanitaria 30 cm por debajo de la infección, quemar restos. |
| **Mazorca Negra** | *Phytophthora palmivora* | **Media** | Mancha marrón/negra húmeda que cubre todo el fruto rápidamente. | Mejorar zanjas de drenaje, podar ramas bajas para ventilación solar. |
| **Cacao Saludable** | *Theobroma cacao* | **Ninguna** | Fruto verde/amarillo brillante, ramas fuertes sin necrosis. | Monitoreo semanal regular y mantenimiento preventivo de pasillos. |
