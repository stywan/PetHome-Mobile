package com.example.pethome.data.remote

import com.example.pethome.data.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Para emulador Android, usar 10.0.2.2 que apunta a localhost de tu PC
    // Para dispositivo físico, cambiar a la IP de tu máquina en la misma red WiFi
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var sessionManager: SessionManager? = null

    /**
     * Inicializar el cliente con el SessionManager
     * Debe llamarse desde MainActivity antes de usar la API
     */
    fun initialize(sessionManager: SessionManager) {
        this.sessionManager = sessionManager
    }

    /**
     * Interceptor para añadir el token JWT a todas las peticiones
     */
    private val authInterceptor = Interceptor { chain ->
        val token = sessionManager?.let { manager ->
            runBlocking {
                try {
                    // Obtener token del SessionManager
                    manager.authToken.first()
                } catch (e: Exception) {
                    android.util.Log.e("RetrofitClient", "Error obteniendo token", e)
                    null
                }
            }
        }

        val requestBuilder = chain.request().newBuilder()

        // Si hay token, añadirlo al header Authorization
        if (!token.isNullOrEmpty()) {
            android.util.Log.d("RetrofitClient", " Añadiendo token JWT al header: Bearer ${token.take(20)}...")
            requestBuilder.addHeader("Authorization", "Bearer $token")
        } else {
            android.util.Log.w("RetrofitClient", " No hay token disponible para la petición: ${chain.request().url}")
        }

        chain.proceed(requestBuilder.build())
    }

    /**
     * Interceptor para logging de peticiones y respuestas (útil para debug)
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Cliente OkHttp con interceptores configurados
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Instancia de Retrofit
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Servicio de autenticación
     */
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    /**
     * Servicio de mascotas
     */
    val petApi: PetApiService by lazy {
        retrofit.create(PetApiService::class.java)
    }
}
