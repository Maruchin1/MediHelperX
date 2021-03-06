package com.maruchin.medihelper.domain.model

import com.maruchin.medihelper.domain.entities.AppExpireDate
import com.maruchin.medihelper.domain.entities.Medicine
import com.maruchin.medihelper.domain.entities.MedicineState

data class MedicineItem(
    val medicineId: String,
    val name: String,
    val type: String,
    val expireDate: AppExpireDate?,
    val state: MedicineState,
    val pictureName: String?
) {
    constructor(medicine: Medicine) : this(
        medicineId = medicine.entityId,
        name = medicine.name,
        type = medicine.type,
        expireDate = medicine.expireDate,
        state = medicine.state,
        pictureName = medicine.pictureName
    )
}