package io.devexpert.splitbill

import io.devexpert.splitbill.data.datasource.ticket.TicketDataSource
import io.devexpert.splitbill.domain.model.TicketData
import kotlinx.serialization.json.Json

class TicketDataSourceFake : TicketDataSource {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val MOCK_JSON = """
        {
           "items":[
              {
                 "count": 6,
                 "name": "Tacos al pastor",
                 "price_per_unit": 20.0
              },
              {
                 "count": 2,
                 "name": "Una cheve",
                 "price_per_unit": 28.0
              },
              {
                 "count": 2,
                 "name": "Gringa",
                 "price_per_unit": 52.0
              }
           ],
           "total": 280.00
        }
        """.trimIndent()
    }

    override suspend fun processTicket(image: ByteArray): TicketData {
        return json.decodeFromString<TicketData>(MOCK_JSON)
    }
}