package com.maruchin.medihelper.domain.model

import com.maruchin.medihelper.domain.entities.AppDate
import com.maruchin.medihelper.domain.entities.AppTime
import com.maruchin.medihelper.domain.entities.PlannedMedicine

data class HistoryItem(
    val date: AppDate,
    val checkboxesList: List<CheckBox>
) {
    data class CheckBox(
        val plannedMedicineId: String,
        val plannedTime: AppTime,
        val status: PlannedMedicine.Status
    )
}