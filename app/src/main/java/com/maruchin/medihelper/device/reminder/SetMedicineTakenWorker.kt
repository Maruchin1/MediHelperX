package com.maruchin.medihelper.device.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.maruchin.medihelper.domain.usecases.plannedmedicines.SetPlannedMedicineTakenUseCase
import org.koin.core.KoinComponent
import org.koin.core.inject

class SetMedicineTakenWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    companion object {
        const val INPUT_PLANNED_MEDICINE_ID = "input-planned-medicine-id"
    }

    private val setPlannedMedicineTakenUseCase: SetPlannedMedicineTakenUseCase by inject()

    override suspend fun doWork(): Result {
        inputData.getString(INPUT_PLANNED_MEDICINE_ID)?.let { plannedMedicineId ->

            PlannedMedicineNotification.cancel(plannedMedicineId, context)
            setPlannedMedicineTakenUseCase.execute(plannedMedicineId)
        }

        return Result.success()
    }
}