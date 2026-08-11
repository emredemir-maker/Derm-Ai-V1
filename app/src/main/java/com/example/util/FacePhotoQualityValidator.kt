package com.example.util

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File
import kotlin.math.abs

data class FacePhotoQualityResult(
    val isAcceptable: Boolean,
    val message: String
)

fun evaluateFaceGeometry(
    imageWidth: Int,
    imageHeight: Int,
    faceCount: Int,
    faceLeft: Int,
    faceTop: Int,
    faceWidth: Int,
    faceHeight: Int,
    headEulerX: Float,
    headEulerY: Float,
    headEulerZ: Float
): FacePhotoQualityResult {
    if (faceCount == 0) return FacePhotoQualityResult(false, "Yüz algılanamadı. Yüzünüzü çerçevenin ortasına alın.")
    if (faceCount > 1) return FacePhotoQualityResult(false, "Fotoğrafta yalnızca bir kişi bulunmalı.")
    if (imageWidth <= 0 || imageHeight <= 0) return FacePhotoQualityResult(false, "Fotoğraf boyutu okunamadı.")

    val widthRatio = faceWidth.toFloat() / imageWidth
    val heightRatio = faceHeight.toFloat() / imageHeight
    if (widthRatio < 0.38f || heightRatio < 0.32f) {
        return FacePhotoQualityResult(false, "Yüzünüz çok uzakta. Gözlüklerinizi çıkarıp kameraya yaklaşın.")
    }

    val centerXRatio = (faceLeft + faceWidth / 2f) / imageWidth
    val centerYRatio = (faceTop + faceHeight / 2f) / imageHeight
    if (centerXRatio !in 0.35f..0.65f || centerYRatio !in 0.25f..0.60f) {
        return FacePhotoQualityResult(false, "Yüzünüzü oval çerçevenin tam ortasına alın.")
    }
    if (abs(headEulerX) > 10f || abs(headEulerY) > 10f || abs(headEulerZ) > 8f) {
        return FacePhotoQualityResult(false, "Başınızı kameraya düz ve göz hizasında çevirin.")
    }
    return FacePhotoQualityResult(true, "Fotoğraf kadraj kontrolünden geçti.")
}

fun validateFacePhoto(
    context: Context,
    photoFile: File,
    onResult: (FacePhotoQualityResult) -> Unit
) {
    val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .build()
    )

    val image = try {
        InputImage.fromFilePath(context, Uri.fromFile(photoFile))
    } catch (_: Exception) {
        detector.close()
        onResult(FacePhotoQualityResult(false, "Fotoğraf okunamadı. Lütfen yeniden çekin."))
        return
    }

    detector.process(image)
        .addOnSuccessListener { faces ->
            val face = faces.firstOrNull()
            onResult(
                evaluateFaceGeometry(
                    imageWidth = image.width,
                    imageHeight = image.height,
                    faceCount = faces.size,
                    faceLeft = face?.boundingBox?.left ?: 0,
                    faceTop = face?.boundingBox?.top ?: 0,
                    faceWidth = face?.boundingBox?.width() ?: 0,
                    faceHeight = face?.boundingBox?.height() ?: 0,
                    headEulerX = face?.headEulerAngleX ?: 0f,
                    headEulerY = face?.headEulerAngleY ?: 0f,
                    headEulerZ = face?.headEulerAngleZ ?: 0f
                )
            )
        }
        .addOnFailureListener {
            onResult(FacePhotoQualityResult(false, "Fotoğraf kalitesi doğrulanamadı. Lütfen yeniden çekin."))
        }
        .addOnCompleteListener { detector.close() }
}
