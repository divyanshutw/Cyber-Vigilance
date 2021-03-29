package com.practice.cybervigilance.homepage.passwordmanager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PasswordsActivityViewModel:ViewModel() {

    val isLoadingDialogVisible=MutableLiveData<Boolean>()
}