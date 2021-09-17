package com.huania.eew.utils.upload

import android.annotation.SuppressLint
import android.content.Context
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ZipUtils
import com.huania.eew.utils.log.LoganParser
import com.huania.eew.utils.log.logE
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 *   created by lzy
 *   on 2020/8/18
 */
@SuppressLint("CheckResult")
fun uploadLog(context: Context,files:MutableList<File>,srcPath:String) {
//    val files =
//        FileUtils.listFilesInDir("${context.getExternalFilesDir("Log")?.absolutePath}/logan_v1")
    var p =
        LoganParser("0123456789012345".toByteArray(), "0123456789012345".toByteArray())
    Observable.create<File> { e ->
        FileUtils.deleteAllInDir(srcPath)
        files.forEach { e.onNext(it) }
        e.onComplete()
    }.observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
        .subscribe({
            val fos = FileOutputStream(
                File("${srcPath}/${it.name}.txt")
            )
            val fis = FileInputStream(it)
            p.parse(fis, fos)
            fis.close()
            fos.close()
        }, {
            it.printStackTrace()
            logE("解密错误:${it.message}")
        }, {
            val target = File("${srcPath}/log.zip")
            ZipUtils.zipFiles(
                FileUtils.listFilesInDir(srcPath),
                target
            )
//            var files = mutableListOf<FileData>()
//            files.add(FileData(target.name, target.absolutePath))
//            var fileManager = FileUploadManager.with(context, files)
//            fileManager.startUpload()
        })
}