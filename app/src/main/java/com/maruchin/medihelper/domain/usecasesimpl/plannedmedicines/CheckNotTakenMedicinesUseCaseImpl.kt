package com.maruchin.medihelper.domain.usecasesimpl.plannedmedicines

import com.maruchin.medihelper.domain.device.DeviceNotifications
import com.maruchin.medihelper.domain.entities.Medicine
import com.maruchin.medihelper.domain.entities.PlannedMedicine
import com.maruchin.medihelper.domain.entities.Profile
import com.maruchin.medihelper.domain.model.PlannedMedicineNotifData
import com.maruchin.medihelper.domain.repositories.MedicineRepo
import com.maruchin.medihelper.domain.repositories.PlannedMedicineRepo
import com.maruchin.medihelper.domain.repositories.ProfileRepo
import com.maruchin.medihelper.domain.usecases.MedicineNotFoundException
import com.maruchin.medihelper.domain.usecases.ProfileNotFoundException
import com.maruchin.medihelper.domain.usecases.plannedmedicines.CheckNotTakenMedicinesUseCase

class CheckNotTakenMedicinesUseCaseImpl(
    private val plannedMedicineRepo: PlannedMedicineRepo,
    private val medicineRepo: MedicineRepo,
    private val profileRepo: ProfileRepo,
    private val notifications: DeviceNotifications
) : CheckNotTakenMedicinesUseCase {

    companion object {
        private const val CHECK_MINUTES_RANGE = 90
    }

    override suspend fun execute() {
        val notTakenPlannedMedicines = plannedMedicineRepo.getListNotTakenForLastMinutes(CHECK_MINUTES_RANGE)
        notTakenPlannedMedicines.forEach { plannedMedicine ->
            notifyAboutNotTakenPlannedMedicine(plannedMedicine)
        }
    }

    private suspend fun notifyAboutNotTakenPlannedMedicine(plannedMedicine: PlannedMedicine) {
        val medicine = getMedicine(plannedMedicine.medicineId)
        val profile = getProfile(plannedMedicine.profileId)
        val notifData = PlannedMedicineNotifData(plannedMedicine, profile, medicine)
        notifications.launchPlannedMedicineNotification(notifData)
    }

    private suspend fun getMedicine(medicineId: String): Medicine {
        return medicineRepo.getById(medicineId) ?: throw MedicineNotFoundException()
    }

    private suspend fun getProfile(profileId: String): Profile {
        return profileRepo.getById(profileId) ?: throw ProfileNotFoundException()
    }
}