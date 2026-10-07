package com.cocoashield.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocoashield.app.R
import com.cocoashield.app.ui.theme.*

@Composable
fun ExecutiveHeader(
    farmerName: String,
    farmName: String,
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(CardNavy)
                    .border(1.5.dp, EmeraldPrimary.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_cocoashield),
                    contentDescription = "CocoaShield Logo",
                    modifier = Modifier.size(34.dp)
                )
            }

            Column {
                Text(
                    text = "Hola, $farmerName",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = farmName,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }

        // Chip de estado de red (En línea vs Modo Local)
        val statusBg = if (isOnline) EmeraldDark.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f)
        val statusBorder = if (isOnline) EmeraldPrimary.copy(alpha = 0.6f) else AmberWarning.copy(alpha = 0.6f)
        val statusText = if (isOnline) "EN LÍNEA 🟢" else "MODO LOCAL 🟡"
        val statusTextColor = if (isOnline) EmeraldPrimary else AmberWarning

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(statusBg)
                .border(1.dp, statusBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusTextColor
            )
        }
    }
}
