package com.example.medihelper.mainapp.medicine

import android.util.Log
import androidx.lifecycle.*
import com.example.medihelper.localdata.entity.MedicineEntity
import com.example.medihelper.localdata.pojo.MedicineItem
import com.example.medihelper.service.AppMode
import com.example.medihelper.service.MedicineService
import com.example.medihelper.service.PersonService
import com.example.medihelper.service.ServerApiService
import java.io.File

class MedicinesViewModel(
    private val personService: PersonService,
    private val medicineService: MedicineService,
    private val serverApiService: ServerApiService
) : ViewModel() {
    private val TAG = "MedicinesViewModel"

    val colorPrimaryLive: LiveData<Int>
    val isAppModeConnectedLive: LiveData<Boolean>
    val searchQueryLive = MutableLiveData("")
    val medicineItemListLive: LiveData<List<MedicineItem>>
    val medicineAvailableLive: LiveData<Boolean>

    init {
        isAppModeConnectedLive = Transformations.map(serverApiService.getAppModeLive()) {
            it == AppMode.CONNECTED
        }
        medicineItemListLive = Transformations.switchMap(searchQueryLive) { searchQuery ->
            Log.d(TAG, "searchQuery change = $searchQuery")
            if (searchQuery.isNullOrEmpty()) {
                medicineService.getItemListLive()
            } else {
                medicineService.getFilteredItemListLive(searchQuery)
            }
        }
        medicineAvailableLive = Transformations.map(medicineItemListLive) { list ->
            list != null && list.isNotEmpty()
        }
        colorPrimaryLive = personService.getMainPersonColorLive()
    }

    fun getMedicineItemDisplayData(medicineItem: MedicineItem): MedicineItemDisplayData {
        val medicineStateData = MedicineEntity.StateData(medicineItem.packageSize, medicineItem.currState)
        return MedicineItemDisplayData(
            medicineID = medicineItem.medicineId,
            medicineName = medicineItem.medicineName,
            medicineUnit = medicineItem.medicineUnit,
            stateAvailable = medicineStateData.stateAvailable,
            stateLayoutWeight = medicineStateData.stateWeight,
            emptyLayoutWeight = medicineStateData.emptyWeight,
            stateColorId = medicineStateData.colorId,
            medicineImageFile = medicineItem.imageName?.let { medicineService.getImageFile(it) }
        )
    }

    data class MedicineItemDisplayData(
        val medicineID: Int,
        val medicineName: String,
        val medicineUnit: String,
        val stateAvailable: Boolean,
        val stateLayoutWeight: Float?,
        val emptyLayoutWeight: Float?,
        val stateColorId: Int?,
        val medicineImageFile: File?
    )
}