package io.localizable.uploader.networking

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.fastjson.FastJsonConverterFactory

class Network {
  companion object {
    private val defaultApiURL = "https://localizable-api.herokuapp.com/"

    private val interceptor: HttpLoggingInterceptor by lazy {
      val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { println(" -> $it") })
      interceptor.level = HttpLoggingInterceptor.Level.BODY
      interceptor
    }

    private val client: OkHttpClient by lazy {
      OkHttpClient.Builder()
          .addInterceptor(interceptor)
          .build()
    }

    private val retrofit: Retrofit by lazy {
      Retrofit.Builder()
          .baseUrl(defaultApiURL)
          .client(client)
          .addConverterFactory(FastJsonConverterFactory.create())
          .build()
    }

    val service: LocalizableAPI by lazy {
      retrofit.create(LocalizableAPI::class.java)
    }
  }
}
