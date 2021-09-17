package com.huania.eew.ui

import com.huania.eew.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel:BaseViewModel() {
    val sharedFlow = MutableStateFlow<String>("")

    init {

    }
}