package com.cocoashield.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocoashield.app.data.local.DiagnosisEntity
import com.cocoashield.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryCarousel(
    diagnoses: List<DiagnosisEntity>,
    onItemClick: (DiagnosisEntity) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Últimos Diagnósticos en Lote",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Row(
                modifier = Modifier.clickable { onSeeAllClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver todo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldPrimary
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (diagnoses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardNavy.copy(alpha = 0.5f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay diagnósticos registrados aún",
                    color = TextGray,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(diagnoses, key = { it.id }) { item ->
                    DiagnosisCompactCard(
                        diagnosis = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiagnosisCompactCard(
    diagnosis: DiagnosisEntity,
    onClick: () -> Unit
) {
    val diseaseColor = when (diagnosis.disease) {
        "Monilia" -> AmberWarning
        "Mazorca Negra" -> DangerRed
        "Escoba de Bruja" -> VioletPurple
        else -> EmeraldPrimary
    }

    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(diagnosis.createdAt))

    Box(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardNavy)
            .border(1.dp, BorderNavy, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header de la tarjeta: Badge de severidad y de sincronización
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(diseaseColor.copy(alpha = 0.2f))
                        .border(0.5.dp, diseaseColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${diagnosis.certainty}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = diseaseColor
                    )
                }

                // Indicador de modo offline o sincronizado
                Text(
                    text = if (diagnosis.isSynced) "☁️ Nube" else "💾 Local",
                    fontSize = 10.sp,
                    color = if (diagnosis.isSynced) EmeraldPrimary else AmberWarning,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = diagnosis.disease,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                maxLines = 1
            )

            Text(
                text = diagnosis.scientificName,
                fontSize = 11.sp,
                color = TextGray,
                maxLines = 1
            )

            Text(
                text = dateString,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}
