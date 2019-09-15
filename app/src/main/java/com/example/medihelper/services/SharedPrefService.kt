package com.example.medihelper.services

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.example.medihelper.R
import com.example.medihelper.localdatabase.entities.PersonEntity
import com.example.medihelper.localdatabase.repositories.PersonRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SharedPrefService(
    private val sharedPreferences: SharedPreferences,
    private val personRepository: PersonRepository
) {
    private val TAG = "SharedPrefService"

    fun getMainPersonID() = sharedPreferences.getInt(KEY_MAIN_PERSON_ID, -1)

    fun getMedicineUnitList() = sharedPreferences.getStringSet(KEY_MEDICINE_UNIT_SET, null)?.toList() ?: emptyList()

    fun getPersonColorResIDList() = sharedPreferences.getStringSet(KEY_PERSON_COLOR_RES_ID_SET, null)?.map { stringValue ->
        stringValue.toInt()
    } ?: emptyList()

    fun checkInitialDataLoaded() {
        val initialDataLoaded = sharedPreferences.getBoolean(KEY_INITIAL_DATA_LOADED, false)
        if (!initialDataLoaded) {
            loadInitialData()
        }
    }

    private fun loadInitialData() = GlobalScope.launch {
            val initialPersonID = personRepository.insert(getInitialPerson()).toInt()
            Log.i(TAG, "initialPersonID = $initialPersonID")
            sharedPreferences.edit {
                putInt(KEY_MAIN_PERSON_ID, initialPersonID)
                putStringSet(KEY_MEDICINE_UNIT_SET, getInitialMedicineUnitSet())
                putStringSet(KEY_PERSON_COLOR_RES_ID_SET, getInitialPersonColorResIDSet())
                putBoolean(KEY_INITIAL_DATA_LOADED, true)
                commit()
            }
        }

    private fun getInitialPerson() = PersonEntity(
        personName = "Ja",
        personColorResID = R.color.colorPrimary
    )

    private fun getInitialMedicineUnitSet() = mutableSetOf("dawki", "pigułki", "ml", "g", "mg", "krople")

    private fun getInitialPersonColorResIDSet() = listOf(
        R.color.colorPersonBlue,
        R.color.colorPersonBrown,
        R.color.colorPersonCyan,
        R.color.colorPersonGray,
        R.color.colorPersonLightGreen,
        R.color.colorPersonOrange,
        R.color.colorPersonPurple,
        R.color.colorPersonRed,
        R.color.colorPersonYellow
    ).map { resID ->
        resID.toString()
    }.toMutableSet()

    companion object {
        private const val KEY_INITIAL_DATA_LOADED = "key-initial-data-loaded"
        private const val KEY_MAIN_PERSON_ID = "key-main-person-id"
        private const val KEY_MEDICINE_UNIT_SET = "key-medicine-type-list"
        private const val KEY_PERSON_COLOR_RES_ID_SET = "key-person-color-res-id-array"
    }
}