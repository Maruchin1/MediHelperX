package com.maruchin.medihelper.domain.usecasesimpl.mediplans

import com.google.common.truth.Truth
import com.maruchin.medihelper.domain.entities.*
import com.maruchin.medihelper.domain.model.MedicinePlanDetails
import com.maruchin.medihelper.domain.repositories.MedicinePlanRepo
import com.maruchin.medihelper.domain.repositories.MedicineRepo
import com.maruchin.medihelper.domain.repositories.ProfileRepo
import com.maruchin.medihelper.domain.usecases.MedicineNotFoundException
import com.maruchin.medihelper.domain.usecases.MedicinePlanNotFoundException
import com.maruchin.medihelper.domain.usecases.ProfileNotFoundException
import com.maruchin.medihelper.domain.usecases.mediplans.GetMedicinePlanDetailsUseCase
import com.maruchin.medihelper.testingframework.mock
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetMedicinePlanDetailsUseCaseImplTest {

    private val medicinePlanRepo: MedicinePlanRepo = mock()
    private val medicineRepo: MedicineRepo = mock()
    private val profileRepo: ProfileRepo = mock()

    private lateinit var useCase: GetMedicinePlanDetailsUseCase

    @Before
    fun before() {
        useCase = GetMedicinePlanDetailsUseCaseImpl(medicinePlanRepo, medicineRepo, profileRepo)
    }

    @Test
    fun execute_Correct() {
        val medicinePlanId = "aaa"
        val medicineId = "bbb"
        val profileId = "ccc"
        val medicinePlanMock = MedicinePlan(
            entityId = medicinePlanId,
            medicineId = medicineId,
            profileId = profileId,
            planType = MedicinePlan.Type.ONCE,
            startDate = AppDate(2019, 11, 13),
            endDate = null,
            intakeDays = null,
            timeDoseList = listOf(
                TimeDose(
                    time = AppTime(9, 30),
                    doseSize = 2f
                )
            )
        )
        val medicineMock = Medicine(
            entityId = medicineId,
            name = "Hitaxa",
            unit = "tabletki",
            expireDate = AppExpireDate(2020, 3),
            state = MedicineState(
                packageSize = 0f,
                currState = 0f
            ),
            pictureName = "test.jpg"
        )
        val profileMock = Profile(
            entityId = profileId,
            name = "Wojtek",
            color = "#000000",
            mainPerson = false
        )

        val expectedResult = MedicinePlanDetails(
            medicinePlanId = medicinePlanId,
            profileColor = profileMock.color,
            medicineId = medicineId,
            medicineName = medicineMock.name,
            medicineUnit = medicineMock.unit,
            planType = medicinePlanMock.planType,
            startDate = medicinePlanMock.startDate,
            endDate = medicinePlanMock.endDate,
            intakeDays = medicinePlanMock.intakeDays,
            timeDoseList = medicinePlanMock.timeDoseList
        )

        runBlocking {
            Mockito.`when`(medicinePlanRepo.getById(medicinePlanId)).thenReturn(medicinePlanMock)
            Mockito.`when`(medicineRepo.getById(medicineId)).thenReturn(medicineMock)
            Mockito.`when`(profileRepo.getById(profileId)).thenReturn(profileMock)

            val result = useCase.execute(medicinePlanId)

            Truth.assertThat(result).isEqualTo(expectedResult)
        }
    }

    @Test(expected = MedicinePlanNotFoundException::class)
    fun execute_MedicinePlanNotFound() {
        val medicinePlanId = "aaa"
        val medicineId = "bbb"
        val profileId = "ccc"

        val medicineMock = Medicine(
            entityId = medicineId,
            name = "Hitaxa",
            unit = "tabletki",
            expireDate = AppExpireDate(2020, 3),
            state = MedicineState(
                packageSize = 0f,
                currState = 0f
            ),
            pictureName = "test.jpg"
        )
        val profileMock = Profile(
            entityId = profileId,
            name = "Wojtek",
            color = "#000000",
            mainPerson = false
        )

        runBlocking {
            Mockito.`when`(medicinePlanRepo.getById(medicinePlanId)).thenReturn(null)
            Mockito.`when`(medicineRepo.getById(medicineId)).thenReturn(medicineMock)
            Mockito.`when`(profileRepo.getById(profileId)).thenReturn(profileMock)

            useCase.execute(medicinePlanId)
        }
    }

    @Test(expected = MedicineNotFoundException::class)
    fun execute_MedicineNotFound() {
        val medicinePlanId = "aaa"
        val medicineId = "bbb"
        val profileId = "ccc"
        val medicinePlanMock = MedicinePlan(
            entityId = medicinePlanId,
            medicineId = medicineId,
            profileId = profileId,
            planType = MedicinePlan.Type.ONCE,
            startDate = AppDate(2019, 11, 13),
            endDate = null,
            intakeDays = null,
            timeDoseList = listOf(
                TimeDose(
                    time = AppTime(9, 30),
                    doseSize = 2f
                )
            )
        )
        val profileMock = Profile(
            entityId = profileId,
            name = "Wojtek",
            color = "#000000",
            mainPerson = false
        )

        runBlocking {
            Mockito.`when`(medicinePlanRepo.getById(medicinePlanId)).thenReturn(medicinePlanMock)
            Mockito.`when`(medicineRepo.getById(medicineId)).thenReturn(null)
            Mockito.`when`(profileRepo.getById(profileId)).thenReturn(profileMock)

            useCase.execute(medicinePlanId)
        }
    }

    @Test(expected = ProfileNotFoundException::class)
    fun execute_ProfileNotFound() {
        val medicinePlanId = "aaa"
        val medicineId = "bbb"
        val profileId = "ccc"
        val medicinePlanMock = MedicinePlan(
            entityId = medicinePlanId,
            medicineId = medicineId,
            profileId = profileId,
            planType = MedicinePlan.Type.ONCE,
            startDate = AppDate(2019, 11, 13),
            endDate = null,
            intakeDays = null,
            timeDoseList = listOf(
                TimeDose(
                    time = AppTime(9, 30),
                    doseSize = 2f
                )
            )
        )
        val medicineMock = Medicine(
            entityId = medicineId,
            name = "Hitaxa",
            unit = "tabletki",
            expireDate = AppExpireDate(2020, 3),
            state = MedicineState(
                packageSize = 0f,
                currState = 0f
            ),
            pictureName = "test.jpg"
        )

        runBlocking {
            Mockito.`when`(medicinePlanRepo.getById(medicinePlanId)).thenReturn(medicinePlanMock)
            Mockito.`when`(medicineRepo.getById(medicineId)).thenReturn(medicineMock)
            Mockito.`when`(profileRepo.getById(profileId)).thenReturn(null)

            useCase.execute(medicinePlanId)
        }
    }
}