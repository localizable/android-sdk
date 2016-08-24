package io.localizable.uploader.networking

import io.localizable.uploader.model.LanguageDelta
import io.localizable.uploader.model.UploadStringsRequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST


interface LocalizableAPI {

  /**
   * Sends the new strings to the server.
   * @param languageDeltas The differences to resources since last update
   */
  @POST("/api/v1/languages")
  fun UploadLanguages(@Header("X-LOCALIZABLE-TOKEN") token: String?, @Body languageDeltas: UploadStringsRequestBody): Call<Void>
}
