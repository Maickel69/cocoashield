# Informe Técnico: Funcionalidades y Arquitectura de la Aplicación Móvil CocoaShield

Este informe detalla de manera exclusiva el diseño, comportamiento, capacidades técnicas y flujos operativos de la **aplicación móvil CocoaShield AI**, estructurado de forma que pueda incorporarse directamente en la sección de metodología o descripción del sistema de un artículo técnico o científico.

---

# Módulo de Campo: Aplicación Móvil CocoaShield AI

La aplicación móvil de CocoaShield actúa como el nodo primario de captura y diagnóstico en el campo de cultivo. Ha sido desarrollada bajo un enfoque híbrido utilizando **React, Vite y Capacitor**, permitiendo el acceso directo a los sensores de hardware del dispositivo móvil (cámara y GPS) a través de una interfaz de usuario optimizada para la interacción en campo (alta visibilidad, modo oscuro táctico y flujos simplificados).

A continuación se describen las características técnicas y operacionales del cliente móvil:

```
[ Cámara Móvil / Galería ]
           │
           ▼
[ Compresión en el Canvas ] ──► JPEG al 60% (Máx. 300px)
           │
           ▼
   [ Codificación Base64 ]
           │
           ├──► [ Red Disponible ] ──► Inferencia en Servidor (TF / ResNet-50)
           │                                      │
           │                                      ▼
           │                             Retorno de Diagnóstico
           │                                      │
           │                                      ▼
           │                           Guardado Local (synced: true)
           │
           └──► [ Sin Cobertura ]  ──► Heurística / Guardado en Cola
                                                  │
                                                  ▼
                                       LocalCache (synced: false)
                                                  │
                                                  ▼
                                       Auto-Sincronización (Lote)
```

---

## 1. Captura Fitosanitaria Inteligente y Compresión en el Borde (Edge)
La aplicación permite a los productores capturar imágenes de mazorcas u hojas de cacao directamente utilizando la cámara nativa del teléfono o importándolas desde la galería.
* **Optimización de Transferencia:** Pensando en la limitada cobertura celular de las zonas agrícolas (redes 2G o 3G inestables), la aplicación no envía la imagen cruda. En su lugar, inicializa un canvas virtual en segundo plano para aplicar una **compresión geométrica y de calidad en caliente**:
  * Redimensiona la imagen limitando su dimensión mayor a **300 píxeles**.
  * Codifica la imagen resultante en formato **JPEG al 60% de calidad**.
  * Convierte el resultado en una cadena **Base64** ligera.
* Esta compresión reduce el tamaño del payload de megabytes a solo unos pocos kilobytes, asegurando que la transmisión de datos sea viable incluso con señales celulares débiles.

---

## 2. Inferencia y Diagnóstico Fitosanitario
Una vez capturada y codificada la imagen, la aplicación coordina el diagnóstico con la estación de inteligencia central:
* **Diagnóstico Online:** Envía el payload Base64 junto con los datos del agricultor y la finca al backend, donde se ejecuta la clasificación con la red neuronal convolucional **ResNet-50** entrenada específicamente.
* **Diagnóstico de Respaldo Heurístico:** Si la aplicación móvil opera en un entorno de demostración sin servidor conectado, contiene un analizador de color en el cliente que ejecuta una lectura rápida sobre un canvas de $64 \times 64$ píxeles para estimar de forma local e instantánea el posible patógeno basándose en dominancia cromática de píxeles (blanco para Monilia, oscuro necrótico para Mazorca Negra, verde para saludable y marrón para Escoba de Bruja).
* **Acciones Preventivas Recomendadas:** Al recibir el diagnóstico (con su porcentaje de certeza y tipo de hongo), la app despliega una ficha con **instrucciones de manejo fitosanitario de 3 pasos**, alineadas con protocolos agrícolas reales (por ejemplo: podas fitosanitarias 30 cm por debajo de la base afectada, desinfección de machetes con alcohol al 70%, o ahogo fúngico mediante cobertura con hojarasca).

---

## 3. Arquitectura de Datos Offline-First (Cola de Sincronización)
La aplicación está programada bajo una filosofía de tolerancia a pérdidas de red, garantizando que el agricultor nunca pierda la información recolectada:
* **Caché Local de Respaldo:** Cuando la aplicación móvil realiza un escaneo y no detecta conexión con el servidor backend central, almacena la ficha diagnóstica en un almacenamiento local (que emula SQLite mediante LocalStorage) con la bandera `synced: false`.
* **Sincronización en Lote en Segundo Plano:** El módulo móvil evalúa el estado del socket de red de forma reactiva. Cuando la conexión con el servidor se restablece (`online`), la aplicación dispara un hilo de fondo que extrae todos los registros acumulados con la bandera de pendiente de envío, los empaqueta, mapea sus coordenadas GPS a las coordenadas cartesianas de visualización del mapa central, y los sube en lote al backend, actualizándolos localmente con la bandera `synced: true` una vez confirmada la persistencia en el servidor.

---

## 4. Geolocalización Fitosanitaria de Alta Precisión
* Utiliza el API nativo de geolocalización del dispositivo móvil mediante Capacitor para interrogar al hardware GPS del celular.
* Captura las coordenadas exactas de **Latitud y Longitud** con nivel de precisión en metros al momento del escaneo.
* Esto permite asociar espacialmente cada patología a un lote de cultivo específico, alimentando directamente el mapa epidemiológico regional del panel web para identificar geográficamente brotes antes de que se conviertan en epidemias generalizadas.

---

## 5. Conectividad Inteligente Adaptativa (Auto-Discovery)
Para evitar que los agricultores tengan que interactuar con configuraciones complejas de red (escribir direcciones IP o puertos al cambiar de red local):
* **Búsqueda Concurrente en Red:** Al iniciarse, la app móvil descarga un registro ligero desde un repositorio en la nube que lista las direcciones de red activas del servidor (interfaces Wi-Fi locales, direcciones virtuales de VPNs corporativas y túneles web temporales como `localtunnel`).
* **Pings de Alta Velocidad:** Lanza solicitudes concurrentes en paralelo a todas las direcciones identificadas con un tiempo de espera límite de 1.5 segundos. La primera interfaz en responder de forma exitosa es fijada automáticamente como la dirección del backend de comunicación.
* **Escáner QR Integrado:** Si el auto-descubrimiento en la nube falla, la app provee un módulo de escaneo visual mediante la librería `jsqr`. Al apuntar la cámara al código QR generado en el Dashboard del agrónomo, el celular se vincula al servidor de manera instantánea, decodificando la URL local o pública del ecosistema centralizado.
