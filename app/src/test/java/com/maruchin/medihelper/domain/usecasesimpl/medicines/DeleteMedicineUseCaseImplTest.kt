package com.maruchin.medihelper.domain.usecasesimpl.medicines

import com.maruchin.medihelper.domain.entities.*
import com.maruchin.medihelper.domain.repositories.MedicinePlanRepo
import com.maruchin.medihelper.domain.repositories.MedicineRepo
import com.maruchin.medihelper.domain.usecases.medicines.DeleteMedicineUseCase
import com.maruchin.medihelper.domain.usecases.mediplans.DeleteMedicinesPlansUseCase
import com.maruchin.medihelper.testingframework.mock
import com.maruchin.medihelper.testingframework.verifyInvocations
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Before
import org.mockito.Mockito

class DeleteMedicineUseCaseImplTest {

    private val medicineRepo: MedicineRepo = mock()
    private val medicinePlanRepo: MedicinePlanRepo = mock()
    private val deleteMedicinesPlansUseCase: DeleteMedicinesPlansUseCase = mock()

    private lateinit var useCase: DeleteMedicineUseCase

    @Before
    fun before() {
        useCase = DeleteMedicineUseCaseImpl(
            medicineRepo,
            medicinePlanRepo,
            deleteMedicinesPlansUseCase
        )
    }

    @Test
    fun execute_medicinePictureExists() {
        val medicineId = "ABCD"

        runBlocking {
            val mockMedicine = Medicine(
                entityId = "ABCD",
                name = "Lek",
                unit = "tabletki",
                expireDate = AppExpireDate(2020, 6),
                state = MedicineState(
                    packageSize = 0f,
                    currState = 0f
                ),
                pictureName = "picture.jpg"
            )
            val medicinesPlansMock = listOf(
                MedicinePlan(
                    entityId = "mmm",
                    profileId = "ppp",
                    medicineId = "ABCD",
                    planType = MedicinePlan.Type.ONCE,
                    startDate = AppDate(2019, 11, 3),
                    endDate = null,
                    intakeDays = null,
                    timeDoseList = listOf(
                        TimeDose(
                            time = AppTime(9, 30),
                            doseSize = 2f
                        )
                    )
                )
            )
            Mockito.`when`(medicineRepo.getById(medicineId)).thenReturn(mockMedicine)
            Mockito.`when`(medicinePlanRepo.getListByMedicine(medicineId)).thenReturn(medicinesPlansMock)

            useCase.execute(medicineId)

            verifyInvocations(medicineRepo, invocations = 1).deleteMedicinePicture("picture.jpg")
            verifyInvocations(medicineRepo, invocations = 1).deleteById("ABCD")
            verifyInvocations(
                deleteMedicinesPlansUseCase,
                invocations = 1
            ).execute(medicinesPlansMock.map { it.entityId })
        }
    }

    @Test
    fun execute_medicinePictureNotExists() {
        val medicineId = "ABCD"

        runBlocking {
            val mockMedicine = Medicine(
                entityId = "ABCD",
                name = "Lek",
                unit = "tabletki",
                expireDate = AppExpireDate(2020, 6),
                state = MedicineState(
                    packageSize = 0f,
                    currState = 0f
                ),
                pictureName = null
            )
            Mockito.`when`(medicineRepo.getById(medicineId)).thenReturn(mockMedicine)
            Mockito.`when`(medicinePlanRepo.getListByMedicine(medicineId)).thenReturn(emptyList())

            useCase.execute(medicineId)

            Mockito.verify(medicineRepo, Mockito.times(0)).deleteMedicinePicture(Mockito.anyString())
            Mockito.verify(medicineRepo, Mockito.times(1)).deleteById("ABCD")
            verifyInvocations(deleteMedicinesPlansUseCase, invocations = 1).execute(Mockito.anyList())
        }
    }
}