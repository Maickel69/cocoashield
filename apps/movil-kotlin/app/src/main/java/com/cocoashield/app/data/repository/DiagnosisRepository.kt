package com.cocoashield.app.data.repository

import com.cocoashield.app.data.local.DiagnosisDao
import com.cocoashield.app.data.local.DiagnosisEntity
import com.cocoashield.app.data.remote.CocoaShieldApi
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class DiagnosisRepository(
    private val dao: DiagnosisDao,
    private val api: CocoaShieldApi = CocoaShieldApi.create()
) {
    val allDiagnoses: Flow<List<DiagnosisEntity>> = dao.getAll()
    val recentDiagnoses: Flow<List<DiagnosisEntity>> = dao.getRecent(10)
    val totalCount: Flow<Int> = dao.getTotalCount()
    val diseasedCount: Flow<Int> = dao.getDiseasedCount()
    val healthyCount: Flow<Int> = dao.getHealthyCount()

    suspend fun insertDiagnosis(diagnosis: DiagnosisEntity) {
        dao.insert(diagnosis)
    }

    suspend fun getUnsyncedCases(): List<DiagnosisEntity> {
        return dao.getUnsynced()
    }

    suspend fun markAsSynced(id: String) {
        dao.markSynced(id)
    }

    suspend fun syncPendingCases(): Int {
        val pending = dao.getUnsynced()
        var syncedCount = 0

        for (item in pending) {
            try {
                val payload = com.cocoashield.app.data.remote.SyncCasePayload(
                    id = item.id,
                    disease = item.disease,
                    diagnosis = item.disease,
                    certainty = item.certainty,
                    confidence = item.certainty,
                    severity = item.severity,
                    location = item.farmName,
                    farmer = item.farmerName,
                    lat = item.latitude,
                    lng = item.longitude,
                    treatment = item.treatment,
                    prescription = item.treatment,
                    image = item.photoPath,
                    photo = item.photoPath
                )
                android.util.Log.d("DiagnosisRepo", "Sending payload to server: $payload")
                val response = api.createCase(payload)
                android.util.Log.d("DiagnosisRepo", "Server response code: ${response.code()}")
                if (response.isSuccessful || response.code() == 409) {
                    dao.markSynced(item.id)
                    syncedCount++
                } else {
                    android.util.Log.e("DiagnosisRepo", "Server error body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("DiagnosisRepo", "Sync failed with exception: ${e.message}", e)
            }
        }
        return syncedCount
    }

    suspend fun fetchRemoteCases(): Boolean {
        return try {
            val remoteCases = api.getCases()
            if (remoteCases.isNotEmpty()) {
                val entities = remoteCases.map { r ->
                    val diseaseName = r.diagnosis ?: r.disease ?: "Monilia"
                    val certaintyVal = r.confidence ?: r.certainty ?: 85
                    val sev = r.severity ?: if (diseaseName == "Sano") "Ninguna" else "Media"

                    DiagnosisEntity(
                        id = r.id ?: UUID.randomUUID().toString(),
                        disease = diseaseName,
                        scientificName = when (diseaseName) {
                            "Monilia" -> "Moniliophthora roreri"
                            "Mazorca Negra" -> "Phytophthora spp."
                            "Escoba de Bruja" -> "Moniliophthora perniciosa"
                            else -> "Theobroma cacao"
                        },
                        certainty = certaintyVal,
                        severity = sev,
                        photoPath = r.image ?: "",
                        latitude = r.lat ?: -1.0234,
                        longitude = r.lng ?: -77.5432,
                        farmerName = r.farmer ?: "Técnico Agrónomo",
                        farmName = r.location ?: "Finca Cacaotera",
                        treatment = r.prescription ?: "",
                        isSynced = true,
                        createdAt = System.currentTimeMillis()
                    )
                }
                dao.insertAll(entities)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }
}
