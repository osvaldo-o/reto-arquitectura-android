package io.devexpert.splitbill

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.devexpert.splitbill.ui.theme.SplitBillTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import java.io.File
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitBillTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// El Composable principal de la pantalla de inicio
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // Variable local para los escaneos restantes (ahora desde DataStore)
    val context = LocalContext.current
    val scanCounter = remember { ScanCounter(context) }
    val scansLeft by scanCounter.scansRemaining.collectAsState(initial = 5)
    val isButtonEnabled = scansLeft > 0
    
    // Inicializar o resetear si es necesario al cargar la pantalla
    LaunchedEffect(Unit) {
        scanCounter.initializeOrResetIfNeeded()
    }
    
    // Estado para almacenar la foto capturada (temporal, solo para pasarla a la IA)
    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }
    
    // Estado para mostrar el resultado del procesamiento
    var processingResult by remember { mutableStateOf<TicketData?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Coroutine scope para operaciones asíncronas
    val coroutineScope = rememberCoroutineScope()
    val ticketProcessor = remember { TicketProcessor(useMockData = true) }

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    fun resizeBitmapToMaxWidth(bitmap: Bitmap, maxWidth: Int): Bitmap {
        if (bitmap.width <= maxWidth) return bitmap
        val aspectRatio = bitmap.height.toFloat() / bitmap.width
        val newWidth = maxWidth
        val newHeight = (maxWidth * aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    // Launcher para capturar foto con la cámara (alta resolución)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && photoUri != null) {
            val inputStream = context.contentResolver.openInputStream(photoUri!!)
            val bitmap = inputStream?.use { BitmapFactory.decodeStream(it) }
            if (bitmap != null) {
                // Redimensionar antes de procesar
                val resizedBitmap = resizeBitmapToMaxWidth(bitmap, 1280)
                capturedPhoto = resizedBitmap
                isProcessing = true
                errorMessage = null
                // Procesar la imagen con IA
                coroutineScope.launch {
                    ticketProcessor.processTicketImage(resizedBitmap)
                        .onSuccess { ticketData ->
                            processingResult = ticketData
                            // Decrementar el contador solo si el procesamiento fue exitoso
                            scanCounter.decrementScan()
                            isProcessing = false
                        }
                        .onFailure { error ->
                            errorMessage = "Error procesando ticket: ${error.message}"
                            isProcessing = false
                        }
                }
            } else {
                errorMessage = "No se pudo leer la imagen"
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            , // Fondo por defecto del tema
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Contador de escaneos
            Text(
                text = if (scansLeft > 0) "Te quedan $scansLeft escaneos" else "No te quedan escaneos",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            // Botón principal
            Button(
                onClick = {
                    if (isButtonEnabled && !isProcessing) {
                        // Crear archivo temporal para la foto
                        val photoFile = File.createTempFile("ticket_", ".jpg", context.cacheDir)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "io.devexpert.splitbill.fileprovider",
                            photoFile
                        )
                        photoUri = uri
                        cameraLauncher.launch(uri)
                    }
                },
                enabled = isButtonEnabled && !isProcessing,
                modifier = Modifier
                    .size(width = 320.dp, height = 64.dp),
                // Colores por defecto del tema
                shape = ButtonDefaults.shape
            ) {
                Text(
                    text = if (isProcessing) "Procesando..." else "Escanear Ticket",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Mostrar resultado del procesamiento
            when {
                isProcessing -> {
                    Text(
                        text = "¡Foto capturada! Procesando con IA...",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage!!,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                processingResult != null -> {
                    Text(
                        text = "¡Ticket procesado! Total: €${processingResult!!.total}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = "${processingResult!!.items.size} items encontrados",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}