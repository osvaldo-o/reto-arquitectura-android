package io.devexpert.splitbill.ui.screen.receipt

import io.devexpert.splitbill.domain.model.TicketData
import io.devexpert.splitbill.domain.model.TicketItem

data class ReceiptUiState(
    val ticketData: TicketData? = null,
    val selectedQuantities: Map<TicketItem, Int> = emptyMap(),
    val paidQuantities: Map<TicketItem, Int> = emptyMap(),
    val availableItems: List<Pair<TicketItem, Int>> = emptyList(),
    val paidItems: List<Pair<TicketItem, Int>> = emptyList(),
    val selectedTotal: Double = 0.0
)
