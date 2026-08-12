package com.example.data.database

import com.example.data.api.ProfileAnalysisResult
import com.squareup.moshi.Moshi

object FaceAnalysisCodec {
    private val adapter = Moshi.Builder()
        .build()
        .adapter(ProfileAnalysisResult::class.java)

    fun encode(result: ProfileAnalysisResult): String = adapter.toJson(result)

    fun decode(json: String?): ProfileAnalysisResult? {
        if (json.isNullOrBlank()) return null
        return runCatching { adapter.fromJson(json) }.getOrNull()
    }
}
