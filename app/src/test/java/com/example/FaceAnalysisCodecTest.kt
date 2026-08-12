package com.example

import com.example.data.api.FaceRegionIssue
import com.example.data.api.ProfileAnalysisResult
import com.example.data.database.FaceAnalysisCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FaceAnalysisCodecTest {
    @Test
    fun analysisRoundTripPreservesFaceMap() {
        val original = ProfileAnalysisResult(
            skinType = "Yağlı",
            concerns = listOf("Akne & Sivilce", "Geniş Gözenekler"),
            goal = "Sivilce Kontrolü",
            explanation = "Fotoğrafta görülebilen bakım özeti",
            faceMapRegions = listOf(
                FaceRegionIssue("Alın", "Parlama", "Niasinamid", 0.5f, 0.2f)
            )
        )

        assertEquals(original, FaceAnalysisCodec.decode(FaceAnalysisCodec.encode(original)))
    }

    @Test
    fun invalidJsonDoesNotCrash() {
        assertNull(FaceAnalysisCodec.decode("{invalid"))
        assertNull(FaceAnalysisCodec.decode(null))
    }
}
