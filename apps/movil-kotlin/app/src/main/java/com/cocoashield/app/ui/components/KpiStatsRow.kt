package com.cocoashield.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocoashield.app.ui.theme.*

@Composable
fun KpiStatsRow(
    totalScans: Int,
    diseasedScans: Int,
    healthyScans: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        KpiCard(
            label = "Escaneos",
            value = totalScans.toString(),
            valueColor = TextWhite,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            label = "Con Anomalía",
            value = diseasedScans.toString(),
            valueColor = DangerRed,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            label = "Sanos",
            value = healthyScans.toString(),
            valueColor = EmeraldPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun KpiCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardNavy.copy(alpha = 0.85f))
            .border(1.dp, BorderNavy, RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextGray
            )
        }
    }
}
