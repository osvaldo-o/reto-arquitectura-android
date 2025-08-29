package io.devexpert.splitbill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.devexpert.splitbill.di.AppModule
import io.devexpert.splitbill.ui.screen.home.HomeScreen
import io.devexpert.splitbill.ui.screen.home.HomeViewModel
import io.devexpert.splitbill.ui.screen.receipt.ReceiptScreen
import io.devexpert.splitbill.ui.screen.receipt.ReceiptViewModel
import io.devexpert.splitbill.ui.theme.SplitBillTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppModule.initialize(this)
        setContent {
            SplitBillTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        val viewModel: HomeViewModel = viewModel {
                            HomeViewModel(
                                AppModule.initializeOrResetScanCounterUseCase,
                                AppModule.getScansReminingUseCase,
                                AppModule.decrementScanUseCase,
                                AppModule.processTicketUseCase
                            )
                        }
                        val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
                        HomeScreen(
                            homeUiState = homeUiState,
                            onProcessedTicket = { image, success ->
                                viewModel.processTicket(image, success)
                            },
                            onNavReceipt = {
                                navController.navigate("receipt")
                            }
                        )
                    }

                    composable("receipt") {
                        val viewModel: ReceiptViewModel = viewModel { ReceiptViewModel(AppModule.getTicketUseCase) }
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                        ReceiptScreen(
                            uiState = uiState,
                            onQuantityChange = { item, newQty ->
                                viewModel.onQuantityChange(item, newQty)
                            },
                            onMarkAsPaid = { viewModel.onMarkAsPaid() },
                            onBackPressed = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}