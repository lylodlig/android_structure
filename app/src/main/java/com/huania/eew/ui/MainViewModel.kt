package com.huania.eew.ui

import androidx.lifecycle.viewModelScope
import com.huania.eew.base.BaseViewModel
import com.huania.eew.config.Settings
import com.huania.eew.data.api.RetrofitClient
import com.huania.eew.utils.safeLaunch
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : BaseViewModel() {
    val sharedFlow = MutableStateFlow<String>("")

    init {
        viewModelScope.safeLaunch({
            val response = RetrofitClient.getInStance().service.login()
            if (response.success) {
                Settings.Account.username = "lzy"
            }
        })
    }


}