package com.maruchin.medihelper.presentation.feature.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.maruchin.medihelper.domain.entities.AppDate
import com.maruchin.medihelper.domain.entities.AppTime
import com.maruchin.medihelper.domain.usecases.PersonUseCases
import com.maruchin.medihelper.domain.usecases.PlannedMedicineUseCases
import com.maruchin.medihelper.presentation.model.PlannedMedicineItem

class CalendarDayViewModel(
    private val personUseCases: PersonUseCases,
    private val plannedMedicineUseCases: PlannedMedicineUseCases
) : ViewModel() {

    val date = MutableLiveData<AppDate>()
    val morningPlannedMedicineItemList: LiveData<List<PlannedMedicineItem>> = MutableLiveData(emptyList())
    val afternoonPlannedMedicineItemList: LiveData<List<PlannedMedicineItem>> = MutableLiveData(emptyList())
    val eveningPlannedMedicineItemList: LiveData<List<PlannedMedicineItem>> = MutableLiveData(emptyList())
    val plannedMedicineAvailable: LiveData<Boolean> = MutableLiveData(false)
    val morningMedicineAvailable: LiveData<Boolean> = MutableLiveData(false)
    val afternoonMedicineAvailable: LiveData<Boolean> = MutableLiveData(false)
    val eveningMedicineAvailable: LiveData<Boolean> = MutableLiveData(false)

//    private val plannedMedicinesForDate: LiveData<List<PlannedMedicineWithMedicine>>

    init {
//        plannedMedicinesForDate = Transformations.switchMap(date) { date ->
//            Transformations.switchMap(personUseCases.getCurrPersonLive()) { person ->
//                person?.let {
//                    plannedMedicineUseCases.getPlannedMedicineWithMedicineListLiveByDateAndPerson(date, person.profileId)
//                }
//            }
//        }
//        morningPlannedMedicineItemList = Transformations.map(plannedMedicinesForDate) { list ->
//            list.filter {
//                it.plannedTime < MORNING_AFTERNOON_LIMIT
//            }.sortedBy { it.plannedTime }.map { PlannedMedicineItem(it) }
//        }
//        afternoonPlannedMedicineItemList = Transformations.map(plannedMedicinesForDate) { list ->
//            list.filter {
//                it.plannedTime >= MORNING_AFTERNOON_LIMIT &&
//                        it.plannedTime < AFTERNOON_EVENING_LIMIT
//            }.sortedBy { it.plannedTime }.map { PlannedMedicineItem(it) }
//        }
//        eveningPlannedMedicineItemList = Transformations.map(plannedMedicinesForDate) { list ->
//            list.filter {
//                it.plannedTime >= AFTERNOON_EVENING_LIMIT
//            }.sortedBy { it.plannedTime }.map { PlannedMedicineItem(it) }
//        }
//        plannedMedicineAvailable = Transformations.map(plannedMedicinesForDate) { !it.isNullOrEmpty() }
//        morningMedicineAvailable = Transformations.map(morningPlannedMedicineItemList) { !it.isNullOrEmpty() }
//        afternoonMedicineAvailable = Transformations.map(afternoonPlannedMedicineItemList) { !it.isNullOrEmpty() }
//        eveningMedicineAvailable = Transformations.map(eveningPlannedMedicineItemList) { !it.isNullOrEmpty() }
    }

    companion object {
        private val MORNING_AFTERNOON_LIMIT = AppTime(12, 0)
        private val AFTERNOON_EVENING_LIMIT = AppTime(18, 0)
    }
}