package io.devexpert.splitbill.ui.screen.receipt

import androidx.lifecycle.ViewModel
import io.devexpert.splitbill.domain.model.TicketItem
import io.devexpert.splitbill.domain.usecase.GetTicketUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ReceiptViewModel(
    private val getTicketUseCase: GetTicketUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    init {
        loadTicketData()
    }

    private fun loadTicketData() {
        val ticketData = getTicketUseCase()
        if (ticketData != null) {
            _uiState.update {
                it.copy(
                    ticketData = ticketData,
                    selectedQuantities = ticketData.items.associateWith { 0 },
                    paidQuantities = ticketData.items.associateWith { 0 }
                )
            }
            updateUi()
        }
    }

    fun onQuantityChange(item: TicketItem, newQty: Int) {
        _uiState.update { currentState ->
            val newSelectedQuantities = currentState.selectedQuantities.toMutableMap()
            newSelectedQuantities[item] = newQty
            currentState.copy(selectedQuantities = newSelectedQuantities)
        }
        updateUi()
    }

    fun onMarkAsPaid() {
        _uiState.update { currentState ->
            val newPaidQuantities = currentState.paidQuantities.toMutableMap()
            currentState.selectedQuantities.forEach { (item, selectedQty) ->
                if (selectedQty > 0) {
                    newPaidQuantities[item] = (newPaidQuantities[item] ?: 0) + selectedQty
                }
            }
            // Reset selected quantities
            val newSelectedQuantities = currentState.selectedQuantities.mapValues { 0 }

            currentState.copy(
                paidQuantities = newPaidQuantities,
                selectedQuantities = newSelectedQuantities
            )
        }
        updateUi()
    }

    private fun updateUi() {
        _uiState.update { currentState ->
            val ticketData = currentState.ticketData ?: return@update currentState

            val availableItems = ticketData.items.map { item ->
                val paidQty = currentState.paidQuantities[item] ?: 0
                val availableQty = item.quantity - paidQty
                item to availableQty
            }.filter { it.second > 0 }

            val paidItems = ticketData.items.map { item ->
                val paidQty = currentState.paidQuantities[item] ?: 0
                item to paidQty
            }.filter { it.second > 0 }

            val selectedTotal = currentState.selectedQuantities.entries.sumOf { (item, quantity) ->
                item.price * quantity
            }

            currentState.copy(
                availableItems = availableItems,
                paidItems = paidItems,
                selectedTotal = selectedTotal
            )
        }
    }
}