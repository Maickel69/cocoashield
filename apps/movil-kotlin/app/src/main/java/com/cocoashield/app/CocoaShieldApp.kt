package com.cocoashield.app

import android.app.Application
import androidx.work.*
import com.cocoashield.app.data.local.AppDatabase
import com.cocoashield.app.workers.SyncWorker
import java.util.concurrent.TimeUnit

class CocoaShieldApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Inicializar base de datos Room SQLite
        AppDatabase.getDatabase(this)

        // Programar sincronización periódica en background cuando haya internet
        setupPeriodicSyncWorker()
    }

    private fun setupPeriodicSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CocoaShieldSyncWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
