package com.practice.cybervigilance.homepage.contribute

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContributeViewModel:ViewModel() {
    val isLoadingDialogVisible=MutableLiveData<Boolean>()
}