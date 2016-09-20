package io.localizable.sync.networking

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.fastjson.FastJsonConverterFactory

class Network {
  companion object {
    private val defaultApiURL = "https://localizable-api.herokuapp.com/"

    private val client: OkHttpClient by lazy {
      OkHttpClient.Builder()
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
