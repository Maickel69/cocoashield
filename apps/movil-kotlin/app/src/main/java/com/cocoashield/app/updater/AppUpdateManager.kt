package com.cocoashield.app.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.cocoashield.app.BuildConfig
import com.cocoashield.app.data.remote.AppVersionInfo
import com.cocoashield.app.data.remote.UpdateApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class AppUpdateManager(
    private val context: Context,
    private val api: UpdateApi = UpdateApi.create()
) {
    private val _updateState = MutableStateFlow<AppVersionInfo?>(null)
    val updateState: StateFlow<AppVersionInfo?> = _updateState

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress

    suspend fun checkForUpdates(): AppVersionInfo? = withContext(Dispatchers.IO) {
        try {
            val remote = api.checkLatestVersion()
            val currentCode = BuildConfig.VERSION_CODE
            if (remote.versionCode > currentCode) {
                _updateState.value = remote
                remote
            } else {
                _updateState.value = null
                null
            }
        } catch (e: Exception) {
            _updateState.value = null
            null
        }
    }

    suspend fun downloadAndInstallApk(versionInfo: AppVersionInfo) = withContext(Dispatchers.IO) {
        try {
            _isDownloading.value = true
            _downloadProgress.value = 0.05f

            val client = OkHttpClient()
            val request = Request.Builder().url(versionInfo.apkUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                _isDownloading.value = false
                return@withContext
            }

            val body = response.body ?: run {
                _isDownloading.value = false
                return@withContext
            }

            val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val apkFile = File(updatesDir, "cocoashield-v${versionInfo.versionName}.apk")

            val totalBytes = body.contentLength()
            var bytesReadTotal = 0L

            body.byteStream().use { input ->
                FileOutputStream(apkFile).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        bytesReadTotal += bytesRead
                        if (totalBytes > 0) {
                            _downloadProgress.value = (bytesReadTotal.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                        }
                    }
                    output.flush()
                }
            }

            _isDownloading.value = false
            _downloadProgress.value = 1f

            // Lanzar instalador nativo de Android preservando base de datos Room
            withContext(Dispatchers.Main) {
                installApk(apkFile)
            }
        } catch (e: Exception) {
            _isDownloading.value = false
        }
    }

    private fun installApk(file: File) {
        if (!file.exists()) return

        // Verificar si se necesita permiso de instalación en Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(settingsIntent)
            }
        }

        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(installIntent)
    }

    fun dismissUpdate() {
        _updateState.value = null
    }
}
