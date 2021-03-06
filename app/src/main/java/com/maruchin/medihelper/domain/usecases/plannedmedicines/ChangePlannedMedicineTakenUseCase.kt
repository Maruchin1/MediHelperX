package com.maruchin.medihelper.domain.usecases.plannedmedicines

import com.maruchin.medihelper.domain.usecases.MedicineNotFoundException
import com.maruchin.medihelper.domain.usecases.PlannedMedicineNotFoundException


interface ChangePlannedMedicineTakenUseCase {

    @Throws(
        PlannedMedicineNotFoundException::class,
        MedicineNotFoundException::class
    )
    suspend fun execute(plannedMedicineId: String)
}