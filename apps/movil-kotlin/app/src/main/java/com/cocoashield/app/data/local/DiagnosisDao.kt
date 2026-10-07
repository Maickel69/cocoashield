package com.cocoashield.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {

    @Query("SELECT * FROM diagnosticos ORDER BY createdAt DESC")
    fun getAll(): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnosticos ORDER BY createdAt DESC LIMIT :limit")
    fun getRecent(limit: Int = 10): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnosticos WHERE isSynced = 0 ORDER BY createdAt ASC")
    suspend fun getUnsynced(): List<DiagnosisEntity>

    @Query("SELECT COUNT(*) FROM diagnosticos")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM diagnosticos WHERE disease != 'Sano'")
    fun getDiseasedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM diagnosticos WHERE disease = 'Sano'")
    fun getHealthyCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diagnosis: DiagnosisEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diagnoses: List<DiagnosisEntity>)

    @Query("UPDATE diagnosticos SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Delete
    suspend fun delete(diagnosis: DiagnosisEntity)

    @Query("DELETE FROM diagnosticos")
    suspend fun deleteAll()
}
