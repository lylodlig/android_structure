package com.huania.eew.app

import android.content.Context
import com.huania.eew.utils.log.logD
import com.rousetime.android_startup.AndroidStartup
import com.rousetime.android_startup.Startup
import com.tencent.mmkv.MMKV
import org.koin.core.context.startKoin


class StartUp1 : AndroidStartup<String>() {
    //用来控制create()方法调时所在的线程，返回true代表在主线程执行
    override fun callCreateOnMainThread() = true

    //组件初始化方法，执行需要处理的初始化逻辑，支持返回一个T类型的实例
    override fun create(context: Context): String? {
        logD("rootDir:${MMKV.initialize(context)}")

        startKoin {
            modules(appModule)
        }
        return ""
    }

    // 用来控制当前初始化的组件是否需要在主线程进行等待其完成
    override fun waitOnMainThread() = false

    // 用来表示当前组件在执行之前需要依赖的组件
    override fun dependencies(): List<Class<out Startup<*>>>? {
        return super.dependencies()
    }

}