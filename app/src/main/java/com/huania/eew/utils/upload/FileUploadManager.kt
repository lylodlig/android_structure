package com.huania.eew.utils.upload

import android.content.Context
import android.content.DialogInterface
import com.huania.eew.utils.log.logD
import com.huania.eew.utils.log.logE
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class FileUploadManager private constructor(
    private val context: Context,
    private val fileDatas: List<FileData>
) : UploadRequestBody.OnUploadProgressListener {
    private var current = 1
    private var url = ""
    private val max: Int = fileDatas.size
    private var deleteOriginFile = false //是否删除源文件 = false

    //    private ProgressDialog mProgressDialog;
    private val listUrl: MutableList<String> =
        ArrayList()

    //	private OkHttpClient mHttpClient;
    private var listener: OnUploadFileListener? = null

    constructor(
        context: Context,
        fileDatas: List<FileData>,
        url: String,
        deleteOriginFile: Boolean = false
    ) : this(context, fileDatas) {
        this.deleteOriginFile = deleteOriginFile
        this.url = url
    }

    fun setOnUploadFileListener(listener: OnUploadFileListener?) {
        this.listener = listener
    }

    fun startUpload() {
        uploadFile()
    }

    private fun uploadFile() {
        if (current > fileDatas.size) {
            listener!!.onSuccess(listUrl)
            return
        }
        val fileData = fileDatas[current - 1]
        val file = File(fileData.path)
        val mHttpClient = OkHttpClient.Builder()
            .connectTimeout(
                10,
                TimeUnit.SECONDS
            ) //                .addInterceptor(new MockInterceptor())
            //                .addInterceptor(new LoggerInterceptor())
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                fileData.fileName,
                UploadRequestBody(file, this)
            ) //                .addFormDataPart("fileId", fileData.getFileId())
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        val call = mHttpClient.newCall(request)

//        mProgressDialog = ProgressDialog.show(context, "正在上传" + listUrl.size() + "/" + fileDatas.size(), "", false, false);
//        logE("lzy","threadName:" + Thread.currentThread().getName());
//        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
//            logE("lzy","-------------------------");
//        }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                //                Logger.INSTANCE.d("上传错误:" + e.getMessage());
            }

            @Throws(IOException::class)
            override fun onResponse(
                call: Call,
                response: Response
            ) {
//                logE("lzy","threadName:" + Thread.currentThread().getName());
                var code = response.code
                var msg: String? = null
                if (code == 200) {
                    logD("上传成功");
                    if (deleteOriginFile) {
                        file.delete()
                    }
                    val body = response.body
                    if (body != null) {
                        val bodyString = body.string()
                        logD(bodyString);
                        var `object`: JSONObject? = null
                        try {
                            `object` = JSONObject(bodyString)
                            msg = `object`.getString("message")
                            if (`object`.getInt("code") == 0) {
                                val data = `object`.getString("data")
                                if (data != null && data.length > 0) {
                                    listUrl.add(JSONObject(data).getString("url"))
                                }
                                listener!!.onProgress(current, max, 100, 100, true)
                                current++
                                uploadFile()
                                return
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    logE("上传失败：$code")
                }
            }
        })
    }

    private val onClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE ->                     //重新上传
                    uploadFile()
                DialogInterface.BUTTON_NEGATIVE ->                     //取消上传
                    listener!!.onCancel()
                DialogInterface.BUTTON_NEUTRAL -> {
                    //忽略该文件继续上传
                    current++
                    uploadFile()
                }
            }
        }

    override fun onProgress(
        progress: Long,
        max: Long,
        isFinish: Boolean
    ) {
        if (listener != null) {
            listener!!.onProgress(current, this.max, progress, max, isFinish)
        }
    }

    interface OnUploadFileListener {
        //		void onSuccess(String[] paths);
        fun onSuccess(paths: List<String>?)
        fun onCancel()
        fun onProgress(
            currentCount: Int,
            maxCount: Int,
            progress: Long,
            maxProgress: Long,
            isFinish: Boolean
        )
    }

    companion object {
        fun with(
            context: Context,
            fileDatas: List<FileData>,
            url: String
        ): FileUploadManager {
            return FileUploadManager(context, fileDatas, url)
        }

        fun with(
            context: Context,
            fileDatas: List<FileData>,
            url: String,
            deleteOriginFile: Boolean
        ): FileUploadManager {
            return FileUploadManager(
                context,
                fileDatas,
                url,
                deleteOriginFile
            )
        }
    }

}