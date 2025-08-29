package io.devexpert.splitbill

import io.devexpert.splitbill.data.repository.TicketRepository
import io.devexpert.splitbill.domain.model.TicketData
import io.devexpert.splitbill.domain.usecase.ProcessTicketUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProcessDataUseCaseTest {

    private lateinit var processTicketUseCase: ProcessTicketUseCase

    @Before
    fun setup() {
        val ticketRepository = TicketRepository(TicketDataSourceFake())
        processTicketUseCase = ProcessTicketUseCase(ticketRepository)
    }

    @Test
    fun `comprobar cantidad de productos diferentes en el ticket`() = runTest {
        // Given
        val imagenFake = ByteArray(0)

        // When
        val result: TicketData = processTicketUseCase(imagenFake)

        print(result.items.size)
        // Then
        assertEquals(3, result.items.size)
    }

    @Test
    fun `comprobar suma total del ticket`() = runTest {
        // Given
        val data = ByteArray(0)

        // When
        val result = processTicketUseCase(data)

        // Then
        assertEquals(280.0, result.total, 0.01)
    }

}