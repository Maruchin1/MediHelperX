package com.example.medihelper.mainapp.family

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medihelper.AppRepository
import com.example.medihelper.localdatabase.pojos.PersonItem
import com.example.medihelper.localdatabase.repositories.PersonRepository
import com.example.medihelper.services.PersonProfileService
import kotlinx.coroutines.launch

class PersonViewModel(
    private val personRepository: PersonRepository,
    private val personProfileService: PersonProfileService
) : ViewModel() {

    val personItemListLive = personRepository.getItemListLive()
    val optionsEnabledPersonIDLive = MutableLiveData<Int>()

    fun selectPerson(personID: Int) = personProfileService.selectCurrPerson(personID)

    fun deletePerson(personID: Int) {
        if (personID == personProfileService.getCurrPersonItemLive().value?.personID) {
            personProfileService.selectMainPerson()
        }
        viewModelScope.launch {
            personRepository.delete(personID)
        }
    }

    fun getPersonItemDisplayData(personItem: PersonItem) = PersonItemDisplayData(
        personID = personItem.personID,
        personName = personItem.personName,
        personColorResID = personItem.personColorResID,
        isMainPerson = personProfileService.isMainPerson(personItem.personID),
        optionsEnabled = false
    )

    data class PersonItemDisplayData(
        val personID: Int,
        val personName: String,
        val personColorResID: Int,
        val isMainPerson: Boolean,
        var optionsEnabled: Boolean
    )
}