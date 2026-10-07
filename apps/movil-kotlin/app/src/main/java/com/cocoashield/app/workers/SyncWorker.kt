package com.cocoashield.app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cocoashield.app.data.local.AppDatabase
import com.cocoashield.app.data.repository.DiagnosisRepository

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = DiagnosisRepository(database.diagnosisDao())
            val count = repository.syncPendingCases()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
