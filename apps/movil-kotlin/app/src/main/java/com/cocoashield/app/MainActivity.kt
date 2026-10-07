package com.cocoashield.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import com.cocoashield.app.classifier.LocalBotanicalClassifier
import com.cocoashield.app.data.local.AppDatabase
import com.cocoashield.app.data.local.DiagnosisEntity
import com.cocoashield.app.data.remote.AppVersionInfo
import com.cocoashield.app.data.repository.DiagnosisRepository
import com.cocoashield.app.ui.screens.MainScreen
import com.cocoashield.app.ui.theme.*
import com.cocoashield.app.updater.AppUpdateManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: DiagnosisRepository
    private lateinit var updateManager: AppUpdateManager

    private val isOnlineState = mutableStateOf(false)
    private val totalScansState = mutableIntStateOf(0)
    private val diseasedScansState = mutableIntStateOf(0)
    private val healthyScansState = mutableIntStateOf(0)
    private val recentDiagnosesState = mutableStateOf<List<DiagnosisEntity>>(emptyList())
    private val allDiagnosesState = mutableStateOf<List<DiagnosisEntity>>(emptyList())
    private val versionInfoState = mutableStateOf<AppVersionInfo?>(null)
    private val isDownloadingState = mutableStateOf(false)
    private val downloadProgressState = mutableFloatStateOf(0f)
    private val showScanSourceDialog = mutableStateOf(false)

    // Lanzador para galería de imágenes
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            contentResolver.openInputStream(it)?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                bitmap?.let { bmp -> processScannedBitmap(bmp) }
            }
        }
    }

    // Lanzador para cámara nativa
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { processScannedBitmap(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(this)
        repository = DiagnosisRepository(database.diagnosisDao())
        updateManager = AppUpdateManager(this)

        setupNetworkMonitoring()
        observeDatabaseFlows()
        checkForAppUpdates()

        setContent {
            CocoaShieldTheme {
                MainScreen(
                    farmerName = "Maicol Alberto",
                    farmName = "Finca Cacaotera Lote 1",
                    isOnline = isOnlineState.value,
                    totalScans = totalScansState.intValue,
                    diseasedScans = diseasedScansState.intValue,
                    healthyScans = healthyScansState.intValue,
                    recentDiagnoses = recentDiagnosesState.value,
                    allDiagnoses = allDiagnosesState.value,
                    versionInfo = versionInfoState.value,
                    isDownloading = isDownloadingState.value,
                    downloadProgress = downloadProgressState.floatValue,
                    onStartScan = { showScanSourceDialog.value = true },
                    onSyncNow = { triggerManualSync() },
                    onUpdateClick = { triggerAppUpdate() },
                    onDismissUpdate = { updateManager.dismissUpdate(); versionInfoState.value = null },
                    onCheckUpdateManual = { checkForAppUpdates(isManual = true) }
                )

                // Diálogo para seleccionar origen de imagen (Cámara, Galería o Muestras de Campo)
                if (showScanSourceDialog.value) {
                    ScanSourceDialog(
                        onDismiss = { showScanSourceDialog.value = false },
                        onCamera = {
                            showScanSourceDialog.value = false
                            takePictureLauncher.launch(null)
                        },
                        onGallery = {
                            showScanSourceDialog.value = false
                            pickImageLauncher.launch("image/*")
                        },
                        onSimulate = { diseaseType ->
                            showScanSourceDialog.value = false
                            simulateFieldDiagnosis(diseaseType)
                        }
                    )
                }
            }
        }
    }

    private fun setupNetworkMonitoring() {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread { isOnlineState.value = true }
                // Sincronizar automáticamente en background al recuperar internet
                lifecycleScope.launch {
                    repository.syncPendingCases()
                }
            }

            override fun onLost(network: Network) {
                runOnUiThread { isOnlineState.value = false }
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager?.registerNetworkCallback(request, networkCallback)

        // Estado inicial
        val active = connectivityManager?.activeNetwork
        val caps = connectivityManager?.getNetworkCapabilities(active)
        isOnlineState.value = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun observeDatabaseFlows() {
        lifecycleScope.launch {
            repository.totalCount.collectLatest { totalScansState.intValue = it }
        }
        lifecycleScope.launch {
            repository.diseasedCount.collectLatest { diseasedScansState.intValue = it }
        }
        lifecycleScope.launch {
            repository.healthyCount.collectLatest { healthyScansState.intValue = it }
        }
        lifecycleScope.launch {
            repository.recentDiagnoses.collectLatest { recentDiagnosesState.value = it }
        }
        lifecycleScope.launch {
            repository.allDiagnoses.collectLatest { allDiagnosesState.value = it }
        }
        lifecycleScope.launch {
            updateManager.updateState.collectLatest { versionInfoState.value = it }
        }
        lifecycleScope.launch {
            updateManager.isDownloading.collectLatest { isDownloadingState.value = it }
        }
        lifecycleScope.launch {
            updateManager.downloadProgress.collectLatest { downloadProgressState.floatValue = it }
        }
    }

    private fun checkForAppUpdates(isManual: Boolean = false) {
        lifecycleScope.launch {
            val update = updateManager.checkForUpdates()
            if (isManual && update == null) {
                Toast.makeText(this@MainActivity, "CocoaShield AI está actualizado a la última versión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun triggerAppUpdate() {
        val info = versionInfoState.value ?: return
        lifecycleScope.launch {
            updateManager.downloadAndInstallApk(info)
        }
    }

    private fun triggerManualSync() {
        lifecycleScope.launch {
            val count = repository.syncPendingCases()
            Toast.makeText(this@MainActivity, "Sincronizados $count diagnósticos con la nube", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processScannedBitmap(bitmap: Bitmap) {
        lifecycleScope.launch {
            // 1. Análisis botánico local instantáneo (<100ms) sin conexión
            val result = LocalBotanicalClassifier.analyze(bitmap)

            // 2. Guardar imagen comprimida en almacenamiento interno de la app
            val filename = "diag_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
            }

            // 3. Crear registro en Room SQLite
            val entity = DiagnosisEntity(
                id = UUID.randomUUID().toString(),
                disease = result.disease,
                scientificName = result.scientificName,
                certainty = result.certainty,
                severity = result.severity,
                photoPath = file.absolutePath,
                latitude = -1.0234,
                longitude = -77.5432,
                farmerName = "Maicol Alberto",
                farmName = "Finca Cacaotera Lote 1",
                treatment = result.treatment,
                isSynced = false, // Guardado localmente en Room primero (Offline-First)
                createdAt = System.currentTimeMillis()
            )

            repository.insertDiagnosis(entity)

            // 4. Si hay internet en este momento, intentar subir en background
            if (isOnlineState.value) {
                repository.syncPendingCases()
            }

            Toast.makeText(
                this@MainActivity,
                "Diagnóstico: ${result.disease} (${result.certainty}%) guardado en SQLite",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun simulateFieldDiagnosis(diseaseType: String) {
        lifecycleScope.launch {
            val entity = when (diseaseType) {
                "Monilia" -> DiagnosisEntity(
                    disease = "Monilia",
                    scientificName = "Moniliophthora roreri",
                    certainty = 93,
                    severity = "Crítica",
                    photoPath = "sample_monilia",
                    treatment = "Retira la mazorca enferma de inmediato. Cúbrela con hojarasca en el suelo para evitar dispersión.",
                    isSynced = false
                )
                "Mazorca Negra" -> DiagnosisEntity(
                    disease = "Mazorca Negra",
                    scientificName = "Phytophthora spp.",
                    certainty = 89,
                    severity = "Media-Alta",
                    photoPath = "sample_black_pod",
                    treatment = "Poda ramas bajas para drenar humedad. Retira y entierra las mazorcas con podredumbre.",
                    isSynced = false
                )
                "Escoba de Bruja" -> DiagnosisEntity(
                    disease = "Escoba de Bruja",
                    scientificName = "Moniliophthora perniciosa",
                    certainty = 87,
                    severity = "Alta",
                    photoPath = "sample_witches_broom",
                    treatment = "Corta la rama afectada 30 cm por debajo de la zona enferma y desinfecta herramientas con alcohol al 70%.",
                    isSynced = false
                )
                else -> DiagnosisEntity(
                    disease = "Sano",
                    scientificName = "Theobroma cacao (Saludable)",
                    certainty = 98,
                    severity = "Ninguna",
                    photoPath = "sample_healthy",
                    treatment = "Lote vigoroso. Mantener labores culturales y deshierbe preventivo.",
                    isSynced = false
                )
            }
            repository.insertDiagnosis(entity)
            if (isOnlineState.value) {
                repository.syncPendingCases()
            }
            Toast.makeText(this@MainActivity, "Simulación registrada: ${entity.disease} guardada en Room", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ScanSourceDialog(
    onDismiss: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onSimulate: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderNavy)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Seleccionar Origen de Escaneo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Button(
                    onClick = onCamera,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "📸 Usar Cámara de Campo", color = DeepNavy, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onGallery,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "🖼️ Seleccionar de Galería", color = TextWhite)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "O simular muestra botánica:",
                    fontSize = 12.sp,
                    color = TextGray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSimulate("Monilia") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWarning),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Monilia", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                    }

                    Button(
                        onClick = { onSimulate("Mazorca Negra") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text(text = "M. Negra", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }

                    Button(
                        onClick = { onSimulate("Sano") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = HealthyGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Sano", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                    }
                }
            }
        }
    }
}
