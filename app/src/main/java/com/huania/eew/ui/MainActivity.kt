package com.huania.eew.ui

import android.os.Bundle
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.runWithPermissions
import com.huania.eew.R
import com.huania.eew.base.BaseVMActivity
import com.huania.eew.databinding.TestBinding
import com.huania.eew.utils.log.logD
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : BaseVMActivity<MainViewModel, TestBinding>() {
    override fun getLayoutId(): Int = R.layout.test
    override fun initActivity(savedInstanceState: Bundle?) {
        askForPermissions(Permission.CAMERA, Permission.WRITE_EXTERNAL_STORAGE) {
            logD("${it.isAllGranted(Permission.CAMERA)}")
        }

        runWithPermissions(Permission.CAMERA, Permission.WRITE_EXTERNAL_STORAGE) {
            logD("${it.isAllGranted()}")
        }

        launch {
            viewModel.sharedFlow.collect {

            }
        }
    }

    override suspend fun launch() {
        viewModel.sharedFlow.collect {
            println(it)
        }
    }

}