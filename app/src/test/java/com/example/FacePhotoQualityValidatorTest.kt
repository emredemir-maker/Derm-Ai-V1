package com.example

import com.example.util.evaluateFaceGeometry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FacePhotoQualityValidatorTest {
    @Test
    fun rejectsDistantFace() {
        val result = evaluateFaceGeometry(1000, 1600, 1, 250, 350, 0f, 0f)
        assertFalse(result.isAcceptable)
    }

    @Test
    fun rejectsMultipleFaces() {
        val result = evaluateFaceGeometry(1000, 1600, 2, 500, 700, 0f, 0f)
        assertFalse(result.isAcceptable)
    }

    @Test
    fun rejectsTurnedFace() {
        val result = evaluateFaceGeometry(1000, 1600, 1, 500, 700, 20f, 0f)
        assertFalse(result.isAcceptable)
    }

    @Test
    fun acceptsSingleCloseStraightFace() {
        val result = evaluateFaceGeometry(1000, 1600, 1, 500, 700, 5f, 4f)
        assertTrue(result.isAcceptable)
    }
}
