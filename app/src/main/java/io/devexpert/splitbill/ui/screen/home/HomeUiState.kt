package io.devexpert.splitbill.ui.screen.home

data class HomeUiState(
    val scansLeft: Int = 0,
    val isProcessing: Boolean = false,
    val errorMessage: String = "",
)