package com.cocoashield.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocoashield.app.data.local.DiagnosisEntity
import com.cocoashield.app.data.remote.AppVersionInfo
import com.cocoashield.app.ui.components.*
import com.cocoashield.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class AppTab {
    HOME, HISTORY, ACCOUNT
}

@Composable
fun MainScreen(
    farmerName: String,
    farmName: String,
    isOnline: Boolean,
    totalScans: Int,
    diseasedScans: Int,
    healthyScans: Int,
    recentDiagnoses: List<DiagnosisEntity>,
    allDiagnoses: List<DiagnosisEntity>,
    versionInfo: AppVersionInfo?,
    isDownloading: Boolean,
    downloadProgress: Float,
    onStartScan: () -> Unit,
    onSyncNow: () -> Unit,
    onUpdateClick: () -> Unit,
    onDismissUpdate: () -> Unit,
    onCheckUpdateManual: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var selectedDiagnosis by remember { mutableStateOf<DiagnosisEntity?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 75.dp) // Espacio para el BottomNav flotante
        ) {
            // Header Ejecutivo persistente
            ExecutiveHeader(
                farmerName = farmerName,
                farmName = farmName,
                isOnline = isOnline
            )

            // Contenido según Tab activo
            when (currentTab) {
                AppTab.HOME -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        item {
                            KpiStatsRow(
                                totalScans = totalScans,
                                diseasedScans = diseasedScans,
                                healthyScans = healthyScans
                            )
                        }

                        item {
                            ScannerCard(
                                onScanClick = onStartScan
                            )
                        }

                        item {
                            HistoryCarousel(
                                diagnoses = recentDiagnoses,
                                onItemClick = { selectedDiagnosis = it },
                                onSeeAllClick = { currentTab = AppTab.HISTORY }
                            )
                        }
                    }
                }

                AppTab.HISTORY -> {
                    HistoryTabScreen(
                        diagnoses = allDiagnoses,
                        isOnline = isOnline,
                        onSyncNow = onSyncNow,
                        onItemClick = { selectedDiagnosis = it }
                    )
                }

                AppTab.ACCOUNT -> {
                    AccountTabScreen(
                        farmerName = farmerName,
                        farmName = farmName,
                        totalLocalRecords = allDiagnoses.size,
                        onCheckUpdate = onCheckUpdateManual
                    )
                }
            }
        }

        // Banner flotante de Auto-Update
        AutoUpdateBanner(
            versionInfo = versionInfo,
            isDownloading = isDownloading,
            downloadProgress = downloadProgress,
            onUpdateClick = onUpdateClick,
            onDismissClick = onDismissUpdate,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )

        // Barra de navegación inferior flotante
        FloatingBottomNav(
            currentTab = currentTab,
            onTabSelected = { currentTab = it },
            onCenterScanClick = onStartScan,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )

        // Modal de detalle fitosanitario al presionar una tarjeta
        DiagnosisDetailDialog(
            diagnosis = selectedDiagnosis,
            onDismiss = { selectedDiagnosis = null }
        )
    }
}

@Composable
private fun HistoryTabScreen(
    diagnoses: List<DiagnosisEntity>,
    isOnline: Boolean,
    onSyncNow: () -> Unit,
    onItemClick: (DiagnosisEntity) -> Unit
) {
    val unsyncedCount = diagnoses.count { !it.isSynced }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Historial en SQLite Local",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "$unsyncedCount diagnósticos pendientes de sincronizar",
                    fontSize = 11.sp,
                    color = if (unsyncedCount > 0) AmberWarning else TextGray
                )
            }

            if (unsyncedCount > 0) {
                Button(
                    onClick = onSyncNow,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Sincronizar",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (diagnoses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay registros guardados en la base de datos local.",
                    color = TextGray,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(diagnoses, key = { it.id }) { item ->
                    val diseaseColor = when (item.disease) {
                        "Monilia" -> AmberWarning
                        "Mazorca Negra" -> DangerRed
                        "Escoba de Bruja" -> VioletPurple
                        else -> EmeraldPrimary
                    }
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(item) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardNavy),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderNavy))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = item.disease,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = item.scientificName,
                                    fontSize = 11.sp,
                                    color = TextGray
                                )
                                Text(
                                    text = "${dateFormat.format(Date(item.createdAt))} • ${item.farmName}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(diseaseColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${item.certainty}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = diseaseColor
                                    )
                                }
                                Text(
                                    text = if (item.isSynced) "☁️ Nube" else "💾 Local",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (item.isSynced) EmeraldPrimary else AmberWarning
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountTabScreen(
    farmerName: String,
    farmName: String,
    totalLocalRecords: Int,
    onCheckUpdate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Configuración y Dispositivo",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderNavy))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Técnico: $farmerName", color = TextWhite, fontWeight = FontWeight.SemiBold)
                Text(text = "Lote Asignado: $farmName", color = TextGray, fontSize = 12.sp)
                Text(text = "Base de Datos: Room SQLite ($totalLocalRecords registros persistidos)", color = EmeraldPrimary, fontSize = 12.sp)
                Text(text = "Motor de IA: Clasificador Botánico Local v2.4", color = TextGray, fontSize = 12.sp)
            }
        }

        Button(
            onClick = onCheckUpdate,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = TextWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Buscar Actualizaciones del Sistema", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FloatingBottomNav(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onCenterScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy.copy(alpha = 0.95f)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderNavy))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Inicio",
                isSelected = currentTab == AppTab.HOME,
                onClick = { onTabSelected(AppTab.HOME) }
            )

            // Botón central de escáner
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary)
                    .clickable { onCenterScanClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Escanear",
                    tint = DeepNavy,
                    modifier = Modifier.size(22.dp)
                )
            }

            BottomNavItem(
                icon = Icons.Default.History,
                label = "Historial",
                isSelected = currentTab == AppTab.HISTORY,
                onClick = { onTabSelected(AppTab.HISTORY) }
            )

            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Cuenta",
                isSelected = currentTab == AppTab.ACCOUNT,
                onClick = { onTabSelected(AppTab.ACCOUNT) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) EmeraldPrimary else TextMuted,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) EmeraldPrimary else TextMuted
        )
    }
}
