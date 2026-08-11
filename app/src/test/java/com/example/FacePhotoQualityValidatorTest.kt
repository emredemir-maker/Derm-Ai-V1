package com.example

import com.example.util.evaluateFaceGeometry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FacePhotoQualityValidatorTest {
    @Test
    fun rejectsDistantFace() {
        val result = evaluateFaceGeometry(1000, 1600, 1, 375, 400, 250, 350, 0f, 0f, 0f)
        assertFalse(result.isAcceptable)
    }

    @Test
    fun rejectsMultipleFaces() {
        val result = evaluateFaceGeometry(1000, 1600, 2, 250, 350, 500, 700, 0f, 0f, 0f)
        assertFalse(result.isAcceptable)
    }

    @Test
    fun rejectsTurnedFace() {
        val result = evaluateFaceGeometry(1000, 1600, 1, 250, 350, 500, 700, 0f, 20f, 0f)
        assertFalse(result.isAcceptable)
    }

    @Test
    fun rejectsFaceLookingUp() {
        val result = evaluateFaceGeometry(1000, 1600, 1, 250, 350, 500, 700, 14f, 0f, 0f)
        assertFalse(result.isAcceptable)
    }

    @Test
    fun rejectsOffCenterFace() {
        val result = evaluateFaceGeometry(1000, 1600, 1, 20, 350, 500, 700, 0f, 0f, 0f)
        assertFalse(result.isAcceptable)
    }

    @Test
    fun acceptsSingleCloseStraightFace() {
        val result = evaluateFaceGeometry(1000, 1600, 1, 250, 350, 500, 700, 5f, 4f, 3f)
        assertTrue(result.isAcceptable)
    }
}
