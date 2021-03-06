package com.maruchin.medihelper.device.reminder.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.maruchin.medihelper.domain.usecases.plannedmedicines.CheckIncomingPlannedMedicinesUseCase
import com.maruchin.medihelper.domain.usecases.plannedmedicines.CheckNotTakenMedicinesUseCase
import org.koin.core.KoinComponent
import org.koin.core.inject

class CheckPlannedMedicinesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
    private val TAG = "CheckPlannedMedicines"

    private val checkNotTakenUseCase: CheckNotTakenMedicinesUseCase by inject()
    private val checkIncomingUseCase: CheckIncomingPlannedMedicinesUseCase by inject()

    override suspend fun doWork(): Result {
        Log.i(TAG, "doWork")
        checkNotTakenUseCase.execute()
        checkIncomingUseCase.execute()
        return Result.success()
    }
}