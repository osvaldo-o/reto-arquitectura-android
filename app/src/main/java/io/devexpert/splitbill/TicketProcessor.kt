package io.devexpert.splitbill

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class TicketItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

data class TicketData(
    val items: List<TicketItem>,
    val total: Double
)

class TicketProcessor {
    
    suspend fun processTicketImage(bitmap: Bitmap): Result<TicketData> = withContext(Dispatchers.IO) {
        try {
            Log.d("TicketProcessor", "Iniciando procesamiento de imagen...")
            
            val model = Firebase.ai.generativeModel(
                modelName = "gemini-2.5-flash-lite-preview-06-17"
            )

            val prompt = """
                Analiza esta imagen de un ticket de restaurante y extrae:
                1. Lista de items con nombre, cantidad y precio individual
                2. Total de la cuenta
                
                Responde SOLO en formato JSON así:
                {
                  "items": [
                    {"name": "Nombre del producto", "quantity": 1, "price": 12.50}
                  ],
                  "total": 45.80
                }
                
                Si no puedes leer algún precio, ponlo como 0.0
            """.trimIndent()

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = model.generateContent(inputContent)
            val responseText = response.text ?: throw Exception("No se recibió respuesta de la IA")
            
            Log.d("TicketProcessor", "Respuesta de IA: $responseText")
            
            // Por ahora, devolvemos datos de prueba mientras configuramos Firebase
            val mockData = TicketData(
                items = listOf(
                    TicketItem("Refresco x2", 2, 6.50),
                    TicketItem("Hamburguesa", 1, 12.80),
                    TicketItem("Patatas fritas", 1, 4.20)
                ),
                total = 23.50
            )
            
            Result.success(mockData)
            
        } catch (e: Exception) {
            Log.e("TicketProcessor", "Error procesando ticket: ${e.message}", e)
            Result.failure(e)
        }
    }
} 