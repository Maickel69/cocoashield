package com.cocoashield.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [DiagnosisEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun diagnosisDao(): DiagnosisDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cocoashield_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialData(database.diagnosisDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: DiagnosisDao) {
                val now = System.currentTimeMillis()
                val samples = listOf(
                    DiagnosisEntity(
                        id = "sample-1",
                        disease = "Monilia",
                        scientificName = "Moniliophthora roreri",
                        certainty = 94,
                        severity = "Crítica",
                        photoPath = "sample_monilia",
                        latitude = -1.0234,
                        longitude = -77.5432,
                        farmerName = "Maicol Alberto",
                        farmName = "Finca Cacaotera Lote 1",
                        treatment = "Retira la mazorca enferma antes de que esparza polvo blanco. Cúbrela con hojarasca seca en el suelo.",
                        isSynced = true,
                        createdAt = now - 3600000 * 2
                    ),
                    DiagnosisEntity(
                        id = "sample-2",
                        disease = "Mazorca Negra",
                        scientificName = "Phytophthora spp.",
                        certainty = 88,
                        severity = "Media-Alta",
                        photoPath = "sample_black_pod",
                        latitude = -1.0250,
                        longitude = -77.5410,
                        farmerName = "Maicol Alberto",
                        farmName = "Finca Cacaotera Lote 1",
                        treatment = "Poda ramas bajas para aumentar ventilación. Cosecha frutos con manchas necróticas de inmediato.",
                        isSynced = true,
                        createdAt = now - 3600000 * 24
                    ),
                    DiagnosisEntity(
                        id = "sample-3",
                        disease = "Sano",
                        scientificName = "Theobroma cacao (Saludable)",
                        certainty = 97,
                        severity = "Ninguna",
                        photoPath = "sample_healthy",
                        latitude = -1.0210,
                        longitude = -77.5450,
                        farmerName = "Maicol Alberto",
                        farmName = "Finca Cacaotera Lote 1",
                        treatment = "Mantener inspecciones fitosanitarias periódicas y poda de saneamiento regular.",
                        isSynced = true,
                        createdAt = now - 3600000 * 48
                    )
                )
                dao.insertAll(samples)
            }
        }
    }
}
