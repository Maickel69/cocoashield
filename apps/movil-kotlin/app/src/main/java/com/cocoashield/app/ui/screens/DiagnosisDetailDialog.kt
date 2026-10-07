package com.cocoashield.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cocoashield.app.data.local.DiagnosisEntity
import com.cocoashield.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DiagnosisDetailDialog(
    diagnosis: DiagnosisEntity?,
    onDismiss: () -> Unit
) {
    if (diagnosis == null) return

    val diseaseColor = when (diagnosis.disease) {
        "Monilia" -> AmberWarning
        "Mazorca Negra" -> DangerRed
        "Escoba de Bruja" -> VioletPurple
        else -> EmeraldPrimary
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val dateString = dateFormat.format(Date(diagnosis.createdAt))

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderNavy))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con botón cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ficha Fitosanitaria",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Estado de Sincronización
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (diagnosis.isSynced) EmeraldDark.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (diagnosis.isSynced) "✅ Sincronizado con la Nube y Dashboard" else "💾 Guardado en dispositivo (Modo Offline)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (diagnosis.isSynced) EmeraldPrimary else AmberWarning
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Título de la Enfermedad y Certeza
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = diagnosis.disease,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = diseaseColor
                        )
                        Text(
                            text = diagnosis.scientificName,
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(diseaseColor.copy(alpha = 0.15f))
                            .border(1.dp, diseaseColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${diagnosis.certainty}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = diseaseColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Protocolo de Manejo Fitosanitario
                Text(
                    text = "PROTOCOLO RECOMENDADO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DeepNavy)
                        .padding(12.dp)
                ) {
                    Text(
                        text = diagnosis.treatment.ifBlank { "Mantener monitoreo constante en el lote." },
                        fontSize = 12.sp,
                        color = TextLight,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Metadatos de Campo
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "📅 Fecha: $dateString",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                    Text(
                        text = "📍 Ubicación: ${diagnosis.farmName} (${String.format(Locale.US, "%.4f", diagnosis.latitude)}, ${String.format(Locale.US, "%.4f", diagnosis.longitude)})",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                    Text(
                        text = "👤 Evaluador: ${diagnosis.farmerName}",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Entendido",
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy
                    )
                }
            }
        }
    }
}
