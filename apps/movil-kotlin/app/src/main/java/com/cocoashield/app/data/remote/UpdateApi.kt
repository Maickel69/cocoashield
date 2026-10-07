package com.cocoashield.app.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

data class AppVersionInfo(
    @SerializedName("versionCode") val versionCode: Int = 1,
    @SerializedName("versionName") val versionName: String = "1.0.0",
    @SerializedName("apkUrl") val apkUrl: String = "",
    @SerializedName("releaseNotes") val releaseNotes: String = "",
    @SerializedName("mandatory") val mandatory: Boolean = false,
    @SerializedName("publishedAt") val publishedAt: String? = null
)

interface UpdateApi {
    @GET("Maickel69/cocoashield/master/apps/movil-kotlin/version.json")
    suspend fun checkLatestVersion(): AppVersionInfo

    companion object {
        private const val RAW_GITHUB_BASE = "https://raw.githubusercontent.com/"

        fun create(): UpdateApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(RAW_GITHUB_BASE)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UpdateApi::class.java)
        }
    }
}
