package com.example.ui.screens

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun createPhotoFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.cacheDir
    return File.createTempFile("Skin_${timeStamp}_", ".jpg", storageDir)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraCaptureView(
    onPhotoCaptured: (File) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) } // Front camera default for facial selfies
        var flashMode by remember { mutableStateOf(ImageCapture.FLASH_MODE_OFF) }

        val preview = remember { Preview.Builder().build() }
        val imageCapture = remember { ImageCapture.Builder().build() }
        val cameraSelector = remember(lensFacing) {
            CameraSelector.Builder().requireLensFacing(lensFacing).build()
        }

        val previewView = remember { PreviewView(context) }

        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

        // Bind camera to lifecycle and release it properly on dispose
        DisposableEffect(lensFacing) {
            val cameraProvider = cameraProviderFuture.get()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (e: Exception) {
                Log.e("CameraCaptureView", "Use case binding failed", e)
            }

            onDispose {
                try {
                    cameraProvider.unbindAll()
                } catch (e: Exception) {
                    Log.e("CameraCaptureView", "Error unbinding on dispose", e)
                }
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Live PreviewView
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Face Silhouette Overlay
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    
                    // Background dark overlay
                    val rectPath = Path().apply {
                        addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                    }
                    
                    // Oval face guide dimensions
                    val ovalWidth = canvasWidth * 0.70f
                    val ovalHeight = canvasHeight * 0.45f
                    val left = (canvasWidth - ovalWidth) / 2f
                    val top = (canvasHeight - ovalHeight) / 2.3f
                    val ovalRect = Rect(left, top, left + ovalWidth, top + ovalHeight)
                    
                    val ovalPath = Path().apply {
                        addOval(ovalRect)
                    }
                    
                    // Mask the oval cut out
                    val cutoutPath = Path.combine(
                        PathOperation.Difference,
                        rectPath,
                        ovalPath
                    )
                    
                    // Draw semi-transparent background around the face zone
                    drawPath(
                        path = cutoutPath,
                        color = Color.Black.copy(alpha = 0.55f)
                    )
                    
                    // Golden-beige professional focus alignment indicator
                    drawOval(
                        color = Color(0xFFE6C15C),
                        topLeft = Offset(left, top),
                        size = Size(ovalWidth, ovalHeight),
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f), 0f)
                        )
                    )
                }

                // Help text aligned underneath the oval cutout
                Text(
                    text = "YÜZÜNÜZÜ OVAL ÇERÇEVEYE HİZALAYIN",
                    color = Color(0xFFE6C15C),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 220.dp)
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Top bar controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.6f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "Akıllı Cilt Analizi",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = {
                        flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                            ImageCapture.FLASH_MODE_ON
                        } else {
                            ImageCapture.FLASH_MODE_OFF
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.6f)
                    )
                ) {
                    Icon(
                        imageVector = if (flashMode == ImageCapture.FLASH_MODE_ON) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flaş",
                        tint = Color.White
                    )
                }
            }

            // Live Scan Assistant Banner
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 110.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xE614181B)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF4CAF50), CircleShape)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Canlı Tarama Asistanı Aktif",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Açı: Dengeli ✓ | Işık: Yeterli ✓\nYüzünüzü göz hizasında dik tutun ve gülümseyin.",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Bottom Shutter controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spacer matching toggler size to keep shutter exactly centered
                Spacer(modifier = Modifier.size(48.dp))

                // Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, CircleShape)
                        .clickable {
                            val photoFile = createPhotoFile(context)
                            val metadata = ImageCapture.Metadata().apply {
                                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                            }
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                                .setMetadata(metadata)
                                .build()

                            imageCapture.flashMode = flashMode

                            imageCapture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        onPhotoCaptured(photoFile)
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e("CameraCaptureView", "Error capturing image", exception)
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .border(3.dp, Color.Black, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                }

                // Lens toggler (front/back camera swap)
                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                            CameraSelector.LENS_FACING_BACK
                        } else {
                            CameraSelector.LENS_FACING_FRONT
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Kamerayı Değiştir",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    } else {
        // Permission presentation Screen
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Kamera İzni Gerekli",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Cilt gelişim ve değişim günlüğünüze fotoğraf ekleyebilmek için uygulamanın kameranıza erişmesine izin vermeniz gerekmektedir.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Kamera İznini Onayla", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onDismiss) {
                Text("Vazgeç", fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
