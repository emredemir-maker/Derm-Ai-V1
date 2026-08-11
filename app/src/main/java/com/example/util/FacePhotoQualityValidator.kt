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
    faceWidth: Int,
    faceHeight: Int,
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
    if (abs(headEulerY) > 15f || abs(headEulerZ) > 12f) {
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
                    faceWidth = face?.boundingBox?.width() ?: 0,
                    faceHeight = face?.boundingBox?.height() ?: 0,
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
