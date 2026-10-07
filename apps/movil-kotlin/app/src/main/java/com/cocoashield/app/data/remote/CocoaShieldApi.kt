package com.cocoashield.app.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

data class CaseRemoteDto(
    @SerializedName("id") val id: String?,
    @SerializedName("diagnosis") val diagnosis: String?,
    @SerializedName("disease") val disease: String?,
    @SerializedName("confidence") val confidence: Int?,
    @SerializedName("certainty") val certainty: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("farmer") val farmer: String?,
    @SerializedName("severity") val severity: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lng") val lng: Double?,
    @SerializedName("prescription") val prescription: String?,
    @SerializedName("status") val status: String?
)

data class PredictRequest(
    @SerializedName("image") val image: String,
    @SerializedName("location") val location: String = "Finca Cacaotera",
    @SerializedName("region") val region: String = "Napo",
    @SerializedName("farmer") val farmer: String = "Maicol Alberto",
    @SerializedName("lat") val lat: Double = -1.0234,
    @SerializedName("lng") val lng: Double = -77.5432
)

data class PredictResponse(
    @SerializedName("model") val model: String?,
    @SerializedName("details") val details: String?,
    @SerializedName("caseData") val caseData: CaseDataPayload?
)

data class CaseDataPayload(
    @SerializedName("diagnosis") val diagnosis: String?,
    @SerializedName("confidence") val confidence: Int?,
    @SerializedName("severity") val severity: String?
)

data class SyncCasePayload(
    @SerializedName("id") val id: String,
    @SerializedName("disease") val disease: String,
    @SerializedName("diagnosis") val diagnosis: String,
    @SerializedName("certainty") val certainty: Int,
    @SerializedName("confidence") val confidence: Int,
    @SerializedName("severity") val severity: String,
    @SerializedName("location") val location: String,
    @SerializedName("farmer") val farmer: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("treatment") val treatment: String,
    @SerializedName("prescription") val prescription: String,
    @SerializedName("image") val image: String,
    @SerializedName("photo") val photo: String
)

interface CocoaShieldApi {
    @GET("api/cases")
    suspend fun getCases(): List<CaseRemoteDto>

    @POST("api/cases")
    suspend fun createCase(@Body payload: SyncCasePayload): Response<Any>

    @POST("api/predict")
    suspend fun predict(@Body request: PredictRequest): PredictResponse

    companion object {
        const val BASE_URL = "https://cocoashield-backend.onrender.com/"

        fun create(): CocoaShieldApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CocoaShieldApi::class.java)
        }
    }
}
