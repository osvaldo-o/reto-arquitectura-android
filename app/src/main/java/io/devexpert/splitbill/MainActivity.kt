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
import io.devexpert.splitbill.data.datasource.scan.ScanCounterDataSourceImpl
import io.devexpert.splitbill.data.datasource.ticket.MLKitTicketDataSource
import io.devexpert.splitbill.data.datasource.ticket.MockTicketDataSource
import io.devexpert.splitbill.data.repository.ScanCounterRepository
import io.devexpert.splitbill.data.repository.TicketRepository
import io.devexpert.splitbill.domain.usecase.GetScansReminingUseCase
import io.devexpert.splitbill.domain.usecase.GetTicketUseCase
import io.devexpert.splitbill.domain.usecase.InitializeOrResetScanCounterUseCase
import io.devexpert.splitbill.domain.usecase.ProcessTicketUseCase
import io.devexpert.splitbill.ui.screen.home.HomeScreen
import io.devexpert.splitbill.ui.screen.home.HomeViewModel
import io.devexpert.splitbill.ui.screen.receipt.ReceiptScreen
import io.devexpert.splitbill.ui.screen.receipt.ReceiptViewModel
import io.devexpert.splitbill.ui.theme.SplitBillTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitBillTheme {
                val navController = rememberNavController()
                val scanCounterRepository = ScanCounterRepository(ScanCounterDataSourceImpl(this))
                val ticketDataSource = if (BuildConfig.DEBUG) MockTicketDataSource() else MLKitTicketDataSource()
                val ticketRepository = TicketRepository(ticketDataSource)
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        val viewModel: HomeViewModel = viewModel {
                            HomeViewModel(
                                InitializeOrResetScanCounterUseCase(scanCounterRepository),
                                GetScansReminingUseCase(scanCounterRepository),
                                ProcessTicketUseCase(ticketRepository, scanCounterRepository),
                                this@MainActivity
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
                        val viewModel: ReceiptViewModel = viewModel {
                            ReceiptViewModel(GetTicketUseCase(ticketRepository))
                        }
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