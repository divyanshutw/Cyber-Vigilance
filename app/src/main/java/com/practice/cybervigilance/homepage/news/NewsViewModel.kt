package com.practice.cybervigilance.homepage.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewsViewModel:ViewModel() {
    var isLoading: Boolean=false
    val isLoadingDialogVisible=MutableLiveData<Boolean>()
}