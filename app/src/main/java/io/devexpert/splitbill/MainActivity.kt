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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.devexpert.splitbill.ui.theme.SplitBillTheme

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

    // Launcher para capturar foto con la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            capturedPhoto = it
            // TODO: Aquí pasaremos la foto a la IA para procesar el ticket
            // Por ahora, solo decrementamos los escaneos
            scansLeft--
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
                    if (isButtonEnabled) {
                        // Lanzar la cámara
                        cameraLauncher.launch(null)
                    }
                },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .size(width = 320.dp, height = 64.dp),
                // Colores por defecto del tema
                shape = ButtonDefaults.shape
            ) {
                Text(
                    text = "Escanear Ticket",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Mensaje temporal para mostrar que la foto fue capturada
            capturedPhoto?.let {
                Text(
                    text = "¡Foto capturada! Procesando...",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}