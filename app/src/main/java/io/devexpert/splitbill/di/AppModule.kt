package io.devexpert.splitbill.di

import android.content.Context
import io.devexpert.splitbill.BuildConfig
import io.devexpert.splitbill.data.datasource.scan.ScanCounterDataSource
import io.devexpert.splitbill.data.datasource.scan.ScanCounterDataSourceImpl
import io.devexpert.splitbill.data.datasource.ticket.MLKitTicketDataSource
import io.devexpert.splitbill.data.datasource.ticket.MockTicketDataSource
import io.devexpert.splitbill.data.datasource.ticket.TicketDataSource
import io.devexpert.splitbill.data.repository.ScanCounterRepository
import io.devexpert.splitbill.data.repository.TicketRepository
import io.devexpert.splitbill.domain.usecase.DecrementScanUseCase
import io.devexpert.splitbill.domain.usecase.GetScansReminingUseCase
import io.devexpert.splitbill.domain.usecase.GetTicketUseCase
import io.devexpert.splitbill.domain.usecase.InitializeOrResetScanCounterUseCase
import io.devexpert.splitbill.domain.usecase.ProcessTicketUseCase


object AppModule {
    private lateinit var applicationContext: Context

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    //Datasource
    private val mlKitTicketDataSource: TicketDataSource by lazy { MLKitTicketDataSource() }
    private val mockTicketDataSource: TicketDataSource by lazy { MockTicketDataSource() }
    val ticketDataSource: TicketDataSource by lazy { if (BuildConfig.DEBUG) mockTicketDataSource else mlKitTicketDataSource }
    val scanCounterDataSource: ScanCounterDataSource by lazy { ScanCounterDataSourceImpl(applicationContext) }

    //Repository
    val ticketRepository: TicketRepository by lazy { TicketRepository(ticketDataSource) }
    val scanCounterRepository: ScanCounterRepository by lazy { ScanCounterRepository(scanCounterDataSource) }

    //Use cases
    val getScansReminingUseCase by lazy { GetScansReminingUseCase(scanCounterRepository) }
    val getTicketUseCase by lazy { GetTicketUseCase(ticketRepository) }
    val initializeOrResetScanCounterUseCase by lazy { InitializeOrResetScanCounterUseCase(scanCounterRepository) }
    val processTicketUseCase by lazy { ProcessTicketUseCase(ticketRepository) }
    val decrementScanUseCase by lazy { DecrementScanUseCase(scanCounterRepository) }

}