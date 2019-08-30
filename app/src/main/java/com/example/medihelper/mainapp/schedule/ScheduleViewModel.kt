package com.example.medihelper.mainapp.schedule

import androidx.lifecycle.*
import com.example.medihelper.AppDateTime
import com.example.medihelper.AppRepository
import com.example.medihelper.R
import com.example.medihelper.localdatabase.entities.MedicinePlanEntity
import com.example.medihelper.localdatabase.entities.PlannedMedicineEntity
import com.example.medihelper.localdatabase.pojos.MedicinePlanItem
import com.example.medihelper.localdatabase.pojos.PlannedMedicineCheckbox
import com.example.medihelper.localdatabase.pojos.PlannedMedicineItem
import java.util.*
import kotlin.collections.ArrayList

class ScheduleViewModel : ViewModel() {

    val timelineDaysCount = 10000
    val initialDatePosition = timelineDaysCount / 2

    val selectedPersonItemLive = AppRepository.getSelectedPersonItemLive()
    val colorPrimaryLive: LiveData<Int>
    val calendarLayoutVisibleLive = MutableLiveData(false)
    val selectedDateLive = MutableLiveData<Date>()

    private val medicinePlanItemListLive: LiveData<List<MedicinePlanItem>>
    private val medicinePlanItemOngoingListLive: LiveData<List<MedicinePlanItem>>
    private val medicinePlanItemEndedListLive: LiveData<List<MedicinePlanItem>>

    init {
        colorPrimaryLive = Transformations.map(selectedPersonItemLive) { personItem ->
            personItem.personColorResID
        }
        medicinePlanItemListLive = Transformations.switchMap(selectedPersonItemLive) { personItem ->
            AppRepository.getMedicinePlanItemListLive(personItem.personID)
        }
        medicinePlanItemOngoingListLive = Transformations.map(medicinePlanItemListLive) { medicinePlanItemList ->
            medicinePlanItemList.filter { medicinePlanItem ->
                getMedicinePlanType(medicinePlanItem) == MedicinePlanType.ONGOING
            }
        }
        medicinePlanItemEndedListLive = Transformations.map(medicinePlanItemListLive) { medicinePlanItemList ->
            medicinePlanItemList.filter { medicinePlanItem ->
                getMedicinePlanType(medicinePlanItem) == MedicinePlanType.ENDED
            }
        }
    }

    fun selectDate(position: Int) = selectDate(getDateForPosition(position))

    fun selectDate(date: Date) {
        val currDate = selectedDateLive.value
        if (currDate != null) {
            if (AppDateTime.compareDates(date, currDate) != 0) {
                selectedDateLive.value = date
            }
        } else {
            selectedDateLive.value = date
        }
    }

    fun getDateForPosition(position: Int): Date {
        val calendar = AppDateTime.getCurrCalendar()
        calendar.add(Calendar.DAY_OF_YEAR, position - (timelineDaysCount / 2))
        return calendar.time
    }

    fun getPositionForDate(date: Date): Int {
        val currCalendar = AppDateTime.getCurrCalendar()
        val daysDiff = AppDateTime.daysBetween(currCalendar.time, date)
        return (timelineDaysCount / 2) + daysDiff.toInt()
    }

    fun getMedicinePlanItemListLive(medicinePlanType: MedicinePlanType): LiveData<List<MedicinePlanItem>> {
        return when (medicinePlanType) {
            MedicinePlanType.ONGOING -> medicinePlanItemOngoingListLive
            MedicinePlanType.ENDED -> medicinePlanItemEndedListLive
        }
    }

    fun getPlannedMedicineItemListByDateLive(date: Date): LiveData<List<PlannedMedicineItem>> {
        return Transformations.switchMap(selectedPersonItemLive) { personItem ->
            AppRepository.getPlannedMedicineItemListLiveByDate(date, personItem.personID)
        }
    }

    fun getMedicinePlanDisplayData(medicinePlanItem: MedicinePlanItem): MedicinePlanDisplayData {
        return MedicinePlanDisplayData(
            medicinePlanID = medicinePlanItem.medicinePlanID,
            medicineName = medicinePlanItem.medicineName,
            durationType = when (medicinePlanItem.durationType) {
                MedicinePlanEntity.DurationType.ONCE -> "Jednorazowo"
                MedicinePlanEntity.DurationType.PERIOD -> "Przez ${AppDateTime.daysBetween(
                    medicinePlanItem.startDate,
                    medicinePlanItem.endDate!!
                )} dni"
                MedicinePlanEntity.DurationType.CONTINUOUS -> "Leczenie ciągłe"
            },
            startDate = when (medicinePlanItem.durationType) {
                MedicinePlanEntity.DurationType.ONCE -> AppDateTime.dateToString(medicinePlanItem.startDate)
                else -> "Od ${AppDateTime.dateToString(medicinePlanItem.startDate)}"
            },
            endDate = when (medicinePlanItem.durationType) {
                MedicinePlanEntity.DurationType.PERIOD -> "Do ${AppDateTime.dateToString(medicinePlanItem.endDate!!)}"
                else -> ""
            },
            daysType = when (medicinePlanItem.daysType) {
                MedicinePlanEntity.DaysType.NONE -> ""
                MedicinePlanEntity.DaysType.EVERYDAY -> "Codziennie"
                MedicinePlanEntity.DaysType.DAYS_OF_WEEK -> medicinePlanItem.daysOfWeek?.getSelectedDaysString() ?: "--"
                MedicinePlanEntity.DaysType.INTERVAL_OF_DAYS -> "Co ${medicinePlanItem.intervalOfDays ?: "--"} dni"
            },
            timeOfTaking = StringBuilder().run {
                medicinePlanItem.timeOfTakingList.forEach { timeOfTaking ->
                    append(AppDateTime.timeToString(timeOfTaking.time))
                    append(" - ")
                    append(timeOfTaking.doseSize)
                    append(" ")
                    append(medicinePlanItem.medicineUnit)
                    append("\n")
                }
                toString()
            }
        )
    }

    fun deleteMedicinePlan(medicinePlanID: Int) = AppRepository.deleteMedicinePlan(medicinePlanID)

    fun getPlannedMedicinesGroupedByDateList(plannedMedicineList: List<PlannedMedicineCheckbox>): List<PlannedMedicineCheckboxGroupedByDate> {
        val distinctDatesArrayList = ArrayList<Date>()
        plannedMedicineList.forEach { plannedMedicineForPlanItem ->
            val plannedDate = plannedMedicineForPlanItem.plannedDate
            if (!distinctDatesArrayList.contains(plannedDate)) {
                distinctDatesArrayList.add(plannedDate)
            }
        }
        val plannedMedicinesGroupedByDateArrayList = ArrayList<PlannedMedicineCheckboxGroupedByDate>()
        distinctDatesArrayList.forEach { date ->
            val plannedMedicinesByDate = plannedMedicineList.filter { plannedMedicine ->
                AppDateTime.compareDates(plannedMedicine.plannedDate, date) == 0
            }
            plannedMedicinesGroupedByDateArrayList.add(
                PlannedMedicineCheckboxGroupedByDate(
                    date = date,
                    plannedMedicineCheckboxList = plannedMedicinesByDate
                )
            )
        }
        return plannedMedicinesGroupedByDateArrayList
    }

    fun getPlannedMedicineCheckboxDisplayData(plannedMedicineCheckbox: PlannedMedicineCheckbox): PlannedMedicineCheckboxDisplayData {
        return PlannedMedicineCheckboxDisplayData(
            plannedMedicineID = plannedMedicineCheckbox.plannedMedicineID,
            statusColorResID = plannedMedicineCheckbox.statusOfTaking.colorResID,
            statusIconResID = when (plannedMedicineCheckbox.statusOfTaking) {
                PlannedMedicineEntity.StatusOfTaking.TAKEN -> R.drawable.baseline_check_white_24
                PlannedMedicineEntity.StatusOfTaking.NOT_TAKEN -> R.drawable.round_close_black_24
                PlannedMedicineEntity.StatusOfTaking.WAITING -> null
            }
        )
    }

    private fun getMedicinePlanType(medicinePlanItem: MedicinePlanItem): MedicinePlanType {
        val currDate = AppDateTime.getCurrCalendar().time
        return when (medicinePlanItem.durationType) {
            MedicinePlanEntity.DurationType.ONCE -> {
                when (AppDateTime.compareDates(currDate, medicinePlanItem.startDate)) {
                    1 -> MedicinePlanType.ENDED
                    else -> MedicinePlanType.ONGOING
                }
            }
            MedicinePlanEntity.DurationType.PERIOD -> {
                when (AppDateTime.compareDates(currDate, medicinePlanItem.endDate!!)) {
                    1 -> MedicinePlanType.ENDED
                    else -> MedicinePlanType.ONGOING
                }
            }
            MedicinePlanEntity.DurationType.CONTINUOUS -> MedicinePlanType.ONGOING
        }
    }

    data class MedicinePlanDisplayData(
        val medicinePlanID: Int,
        val medicineName: String,
        val durationType: String,
        val startDate: String,
        val endDate: String,
        val daysType: String,
        val timeOfTaking: String
    )

    data class PlannedMedicineCheckboxGroupedByDate(
        val date: Date,
        val plannedMedicineCheckboxList: List<PlannedMedicineCheckbox>
    )

    data class PlannedMedicineCheckboxDisplayData(
        val plannedMedicineID: Int,
        val statusColorResID: Int,
        val statusIconResID: Int?
    )

    enum class MedicinePlanType(val pageTitle: String) {
        ENDED("Zakończone"),
        ONGOING("Trwające")
    }
}