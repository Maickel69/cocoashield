# REPORTE TÉCNICO DE INVESTIGACIÓN: COCOASHIELD AI
**Para:** Docente de Redacción y Asesoría Científica
**De:** Maicol Alberto Amezquita Camargo (Estudiante de Ingeniería de Sistemas - UNAMAD)
**Proyecto:** CocoaShield AI
**Fecha:** Junio 2025

---

## 1. CONTEXTO GENERAL DEL PROYECTO
El proyecto **CocoaShield AI** es un ecosistema tecnológico diseñado para la detección temprana y clasificación de enfermedades en el fruto del cacao (*Theobroma cacao*) en zonas agrícolas de la Amazonía peruana (Madre de Dios). La principal característica de la zona es la **nula o muy baja conectividad a internet**, lo que impide usar herramientas tradicionales en la nube.

---

## 2. ARQUITECTURA TECNOLÓGICA (CÓMO FUNCIONA EL SOFTWARE)
El sistema no es una aplicación web tradicional, sino un **ecosistema distribuido** con tres componentes:

1. **Componente Móvil (App):** Desarrollada con React y Capacitor. Funciona de manera local en el teléfono del agricultor. Captura la imagen de la mazorca de cacao y realiza la inferencia (predicción) de forma local. Almacena el historial en una base de datos local SQLite.
2. **Servidor Local / Sincronización Diferida:** Cuando el agricultor tiene acceso a un servidor local o a una red Wi-Fi autorizada (no necesariamente internet), los datos guardados en el celular se sincronizan con la base de datos central de forma asíncrona.
3. **Módulo de Inteligencia Artificial (Red Neuronal):** Una Red Neuronal Convolucional (CNN) integrada para procesar las imágenes de campo y clasificar el estado fitosanitario.

---

## 3. ALCANCE FITOSANITARIO (LAS 3 ENFERMEDADES PREDICHERAS)
El modelo clasifica las mazorcas en **4 clases** (3 enfermedades fúngicas que causan el 80% de pérdidas en la región, más la clase de control sano):

*   **Clase 1: Moniliasis** (causada por el hongo *Moniliophthora roreri*).
*   **Clase 2: Mazorca Negra** (causada por el pseudohongo *Phytophthora palmivora*).
*   **Clase 3: Escoba de Bruja** (causada por el hongo *Moniliophthora perniciosa*).
*   **Clase 4: Sano** (Mazorcas libres de patógenos).

---

## 4. ESTADÍSTICAS DEL DATASET REAL DE CAMPO
El entrenamiento y validación de la red neuronal se basa en un dataset con imágenes tomadas en condiciones reales de iluminación, sombra y ruido de fondo:

*   **Total de imágenes:** 13,304
*   **Distribución por clase:**
    *   Sano: 8,180 imágenes
    *   Monilia: 2,818 imágenes
    *   Mazorca Negra: 2,100 imágenes
    *   Escoba de Bruja: 206 imágenes (clase minoritaria con desbalance)

---

## 5. BORRADORES DE TÍTULO PARA EVALUACIÓN DEL DOCENTE
*Buscamos un título con la estructura: `[Variable principal] + en + [Población/Muestra] + [Contexto/Metodología]`, limitando o evitando cifras numéricas directas para mantenerlo académico.*

*   **Opción A (Fórmula Directa con Enfermedades Específicas):**
    > *"Detección de Moniliasis, Mazorca Negra y Escoba de Bruja en Theobroma cacao mediante aprendizaje profundo: Un ecosistema móvil-servidor con sincronización diferida"*
*   **Opción B (Enfoque en Ecosistema de Software y las 3 Patologías):**
    > *"CocoaShield AI: Ecosistema híbrido con sincronización diferida para la clasificación de las tres principales enfermedades del cacao mediante redes neuronales"*
*   **Opción C (Enfoque clásico de Ingeniería/Visión Computacional):**
    > *"Identificación automática de Moniliasis, Mazorca Negra y Escoba de Bruja en cacao: Integración de visión computacional en una arquitectura móvil-servidor"*

---

## 6. BIBLIOGRAFÍA CLAVE UTILIZADA (10 FUENTES Q1/Q2)
El marco teórico se fundamenta en las siguientes publicaciones científicas indexadas (organizadas en el archivo de citas `CocoaShield_referencias.bib`):
1. **Alvarado et al. (2025)** - *Agriculture* (Q1): Revisión sistemática de visión computacional en cacao.
2. **Sykes et al. (2023)** - *Applications in Plant Sciences* (Q2): Visión computacional adaptada a la agricultura del cacao.
3. **Ashurov et al. (2025)** - *Frontiers in Plant Science* (Q1): Redes neuronales convolucionales con bloques SE para fitopatología.
4. **Khubisa & Olugbara (2025)** - *Egyptian Informatics Journal* (Q2): Análisis bibliométrico de aprendizaje profundo en fitopatología.
5. **Mohanty et al. (2016)** - *Frontiers in Plant Science* (Q1): El paper pionero de smartphone y deep learning en plantas.
6. **Jayapal et al. (2022)** - *Applied Sciences* (Q2): Análisis de imágenes RGB y segmentación profunda de fitopatógenos.
7. **Zhang et al. (2023)** - *International Journal of Applied Earth Observation* (Q1): Monitoreo de cultivos mediante sensores y machine learning.
8. **Abdollahi et al. (2021)** - *Sustainability* (Q2): Redes de sensores inalámbricos e IoT para agricultura de precisión.
9. **Quan et al. (2021)** - *IEEE JSTARS* (Q1): Modelado y machine learning para variables de follaje forestal.
10. **Wang et al. (2025)** - *Remote Sensing* (Q1): Estado del arte y datasets para detección de plagas y enfermedades mediante deep learning (2018-2024).
