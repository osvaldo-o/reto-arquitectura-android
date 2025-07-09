package io.devexpert.splitbill

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
    // Variable local para los escaneos restantes
    var scansLeft by remember { mutableStateOf(3) } // Cambia a 0 para probar el estado deshabilitado
    val maxScans = 5
    val isButtonEnabled = scansLeft > 0
    
    // Estado para almacenar la foto capturada (temporal, solo para pasarla a la IA)
    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }
    
    // Estado para mostrar el resultado del procesamiento
    var processingResult by remember { mutableStateOf<TicketData?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Coroutine scope para operaciones asíncronas
    val coroutineScope = rememberCoroutineScope()
    val ticketProcessor = remember { TicketProcessor() }

    // Launcher para capturar foto con la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            capturedPhoto = it
            isProcessing = true
            errorMessage = null
            
            // Procesar la imagen con IA
            coroutineScope.launch {
                ticketProcessor.processTicketImage(it)
                    .onSuccess { ticketData ->
                        processingResult = ticketData
                        scansLeft--
                        isProcessing = false
                    }
                    .onFailure { error ->
                        errorMessage = "Error procesando ticket: ${error.message}"
                        isProcessing = false
                    }
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
                        // Lanzar la cámara
                        cameraLauncher.launch(null)
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