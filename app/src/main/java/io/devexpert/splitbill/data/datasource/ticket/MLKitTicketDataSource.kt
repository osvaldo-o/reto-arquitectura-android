package io.devexpert.splitbill.data.datasource.ticket

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import io.devexpert.splitbill.TicketData
import io.devexpert.splitbill.data.datasource.ticket.TicketDataSource
import kotlinx.serialization.json.Json

class MLKitTicketDataSource : TicketDataSource {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun processTicket(image: Bitmap): TicketData {
        val jsonSchema = Schema.Companion.obj(
            mapOf(
                "items" to Schema.Companion.array(
                    Schema.Companion.obj(
                        mapOf(
                            "name" to Schema.Companion.string(),
                            "count" to Schema.Companion.integer(),
                            "price_per_unit" to Schema.Companion.double()
                        )
                    )
                ),
                "total" to Schema.Companion.double()
            )
        )

        val prompt = """
            Analiza esta imagen de un ticket de restaurante y extrae:
            1. Lista de items con nombre, cantidad y precio individual
            2. Total de la cuenta
            Si no puedes leer algún precio, ponlo como 0.0
        """.trimIndent()

        val inputContent = content {
            image(image)
            text(prompt)
        }

        val model = Firebase.ai.generativeModel(
            modelName = "gemini-2.5-flash-lite-preview-06-17",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = jsonSchema
            }
        )

        val response = model.generateContent(inputContent)
        val responseText = response.text ?: throw Exception("No se recibió respuesta de la IA")
        val ticketData = json.decodeFromString<TicketData>(responseText)

        return ticketData
    }
}