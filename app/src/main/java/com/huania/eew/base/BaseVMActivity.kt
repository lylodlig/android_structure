package com.huania.eew.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.huania.eew.utils.getClass
import kotlinx.coroutines.launch

abstract class BaseVMActivity<VM : BaseViewModel, VB : ViewDataBinding> : BaseActivity<VB>() {

    val viewModel: VM by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(getClass(this)) as VM
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch {
            launch()
        }
    }

    open suspend fun launch() {
    }

}