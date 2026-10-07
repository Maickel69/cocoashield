package com.cocoashield.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "diagnosticos")
data class DiagnosisEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val disease: String,           // Monilia, Mazorca Negra, Escoba de Bruja, Sano
    val scientificName: String,    // e.g. Moniliophthora roreri
    val certainty: Int,            // 0 a 100
    val severity: String,          // Crítica, Media-Alta, Alta, Ninguna
    val photoPath: String,         // Ruta local o URI interna de la foto
    val latitude: Double = -1.0234,
    val longitude: Double = -77.5432,
    val farmerName: String = "Maicol Alberto",
    val farmName: String = "Finca Cacaotera Lote 1",
    val treatment: String = "",    // Pasos de acción recomendados
    val isSynced: Boolean = false, // false = Modo Offline, true = sincronizado con nube
    val createdAt: Long = System.currentTimeMillis()
)
