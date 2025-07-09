package io.devexpert.splitbill

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TicketItem(
    val name: String,
    @SerialName("count") val quantity: Int,
    @SerialName("price_per_unit") val price: Double
)

@Serializable
data class TicketData(
    val items: List<TicketItem>,
    val total: Double
)

class TicketProcessor {
    suspend fun processTicketImage(bitmap: Bitmap): Result<TicketData> = withContext(Dispatchers.IO) {
        try {
            Log.d("TicketProcessor", "Iniciando procesamiento de imagen...")

            // Definir el schema para la respuesta JSON (sin requiredProperties)
            val jsonSchema = Schema.obj(
                mapOf(
                    "items" to Schema.array(
                        Schema.obj(
                            mapOf(
                                "name" to Schema.string(),
                                "count" to Schema.integer(),
                                "price_per_unit" to Schema.double()
                            )
                        )
                    ),
                    "total" to Schema.double()
                )
            )

            val prompt = """
                Analiza esta imagen de un ticket de restaurante y extrae:
                1. Lista de items con nombre, cantidad y precio individual
                2. Total de la cuenta
                Si no puedes leer algún precio, ponlo como 0.0
            """.trimIndent()

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val model = Firebase.ai.generativeModel(
                modelName = "gemini-2.5-pro",
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = jsonSchema
                }
            )

            val response = model.generateContent(inputContent)
            val responseText = response.text ?: throw Exception("No se recibió respuesta de la IA")
            Log.d("TicketProcessor", "Respuesta de IA: $responseText")

            // Parsear el JSON usando kotlinx.serialization
            val ticketData = Json { ignoreUnknownKeys = true }.decodeFromString<TicketData>(responseText)
            Result.success(ticketData)
        } catch (e: Exception) {
            Log.e("TicketProcessor", "Error procesando ticket: ${e.message}", e)
            Result.failure(e)
        }
    }
} 