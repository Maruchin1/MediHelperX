package com.maruchin.medihelper.domain.usecases.medicines

import com.maruchin.medihelper.domain.entities.AppExpireDate
import com.maruchin.medihelper.domain.entities.Medicine
import com.maruchin.medihelper.domain.model.MedicineValidator
import com.maruchin.medihelper.domain.repositories.MedicineRepo

class SaveMedicineUseCase(
    private val medicineRepo: MedicineRepo
) {

    suspend fun execute(params: Params): MedicineValidator {
        val validator = MedicineValidator(
            name = params.name,
            unit = params.unit,
            expireDate = params.expireDate,
            packageSize = params.packageSize,
            currState = params.currState
        )
        if (validator.noErrors) {
            saveMedicineToRepo(params)
        }
        return validator
    }

    private suspend fun saveMedicineToRepo(params: Params) {
        val medicine = Medicine(
            medicineId = params.medicineId ?: "",
            name = params.name!!,
            unit = params.unit!!,
            expireDate = params.expireDate!!,
            packageSize = params.packageSize,
            currState = params.currState,
            additionalInfo = params.additionalInfo
        )
        if (params.medicineId == null) {
            medicineRepo.addNew(medicine)
        } else {
            medicineRepo.update(medicine)
        }
    }


    data class Params(
        val medicineId: String?,
        val name: String?,
        val unit: String?,
        val expireDate: AppExpireDate?,
        val packageSize: Float?,
        val currState: Float?,
        val additionalInfo: String?
    )
}