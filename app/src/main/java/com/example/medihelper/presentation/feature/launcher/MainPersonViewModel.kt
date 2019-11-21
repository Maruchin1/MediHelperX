package com.example.medihelper.presentation.feature.launcher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medihelper.presentation.framework.ActionLiveData
import com.example.medihelper.domain.usecases.PersonUseCases
import kotlinx.coroutines.launch

class MainPersonViewModel(
    private val personUseCases: PersonUseCases
) : ViewModel() {

    private val _form = FormModel()
    private val _formError = FormErrorModel()
    private val _setupEndAction = ActionLiveData()

    val form: FormModel
        get() = _form
    val formError: FormErrorModel
        get() = _formError
    val setupEndAction: LiveData<Boolean>
        get() = _setupEndAction

    fun saveMainProfile() = viewModelScope.launch {
        if (isFormValid()) {
            personUseCases.addMainPerson(_form.userName.value!!)
            _setupEndAction.sendAction()
        }
    }

    private fun isFormValid(): Boolean {
        val userNameErr = if (_form.userName.value.isNullOrEmpty()) {
            "Twoje imię jest wymagane"
        } else null

        _formError.userNameErr.postValue(userNameErr)

        return userNameErr == null
    }

    class FormModel {
        val userName = MutableLiveData<String>()
    }

    class FormErrorModel {
        val userNameErr = MutableLiveData<String>()
    }
}