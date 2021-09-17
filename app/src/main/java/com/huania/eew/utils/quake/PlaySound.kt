package com.huania.eew.utils.quake

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Handler
import com.huania.eew.R
import com.huania.eew.utils.log.logD
import com.huania.eew.utils.log.logE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by lhw on 16-4-20.
 */
class PlaySound private constructor(context: Context) {
    private var PlaySoundTailTime = 10
//    private val interval_ = 485

    private val interval_ = 500
    private val handler_: Handler = Handler()
    private var runnable_: Runnable? = null
    private var soundPool_: SoundPool? = null
    private val list_: MutableList<Int?>
    private val musicId_: HashMap<Int, Int>
    private var soundID = 0
    var currentCountdown = 0
    private var observer: DisposableObserver<Long>? = null
    val isPlaying: Boolean
        get() = list_.size > 0

    //播放声音
    fun play(countdown: Int, intensity: Float) {
        clear()
        PlaySoundTailTime = if (intensity >= 3) {
            18
        } else {
            10
        }
        logD("play,$countdown")
        if (intensity < 3.0f) {
            addToPlayList(0, countdown)
        } else if (intensity < 5.0f) {
            addToPlayList(1, countdown)
        } else {
            addToPlayList(2, countdown)
        }
        playSound()
        timer(countdown, intensity)
    }

    fun timer(countdown: Int, intensity: Float) {
        PlaySoundTailTime = if (intensity >= 3) {
            18
        } else {
            10
        }
        observer?.dispose()
        observer = Observable.interval(0, 1, TimeUnit.SECONDS)
            .map { countdown - it }
            .take(countdown.toLong() + PlaySoundTailTime)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Long>() {
                override fun onComplete() {
                }

                override fun onNext(t: Long) {
                    currentCountdown = t.toInt()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logE("ee:${e.cause}")
                }
            })

    }

    //播放声音
    fun playWithoutDingdong(countdown: Int, intensity: Float) {
        clear()
        PlaySoundTailTime = if (intensity >= 3) {
            18
        } else {
            10
        }
        logD("playWithoutDingdong,$countdown")
        when {
            intensity < 3.0f -> {
                addToPlayListWithoutDingdong(0, countdown)
            }
            intensity < 5.0f -> {
                addToPlayListWithoutDingdong(1, countdown)
            }
            else -> {
                addToPlayListWithoutDingdong(2, countdown)
            }
        }
        playSound()
        timer(countdown, intensity)
    }

    fun clear() {
        runnable_?.let { handler_.removeCallbacks(it) }
        list_.clear()
        soundPool_!!.stop(soundID)
    }

    //停止声音
    fun stop() {
        runnable_?.let { handler_.removeCallbacks(it) }
        list_.clear()
        soundPool_!!.stop(soundID)
        //        soundPool_.release();
    }

    private fun playSound() {
        runnable_ = object : Runnable {
            override fun run() {
                val size = list_.size
                if (0 == size) {
                    runnable_?.let { handler_.removeCallbacks(it) }
                } else {
                    if (size > PlaySoundTailTime) {
                        handler_.postDelayed(this, interval_.toLong())
                    } else handler_.postDelayed(this, 1280)

                    soundID = soundPool_!!.play(list_[0]!!, 1f, 1f, 0, 0, 1f)
                    list_.removeAt(0)

                }
            }
        }
        handler_.postDelayed(runnable_ as Runnable, 0)
    }

    private fun addToPlayList(level: Int, countdown: Int) {
        list_.clear()
        var isShouldAddDi = false // 是否应该加入滴滴声（或无声）
        for (i in countdown downTo -PlaySoundTailTime + 1) {
            if (i == countdown) {
                list_.add(musicId_[0])
            } else if (i <= 0) {
//                list_.add(musicId_.get(11));
                list_.add(musicId_[14])
            } else if (i > 0 && i <= 10) {
                list_.add(musicId_[i])
                if (0 == level) list_.add(musicId_[11]) else if (1 == level) list_.add(
                    musicId_[12]
                ) else if (2 == level) list_.add(musicId_[13])
            } else {
                if (i <= 99) {
                    val ten = i / 10
                    val unit = i % 10
                    isShouldAddDi = if (isShouldAddDi) {
                        if (0 == level) list_.add(musicId_[11]) else if (1 == level) list_.add(
                            musicId_[12]
                        ) else if (2 == level) list_.add(musicId_[13])
                        list_.add(musicId_[11])
                        false
                    } else {
                        if (1 == ten) {
                            list_.add(musicId_[10])
                        } else {
                            list_.add(musicId_[ten])
                        }
                        if (0 == unit) {
                            list_.add(musicId_[10])
                        } else {
                            list_.add(musicId_[unit])
                        }
                        true
                    }
                } else {
                    list_.add(musicId_[11])
                    list_.add(musicId_[15])
                }
            }
        }
    }

    private fun addToPlayListWithoutDingdong(level: Int, countdown: Int) {
        list_.clear()
        var isShouldAddDi = false // 是否应该加入滴滴声（或无声）
        for (i in countdown downTo -PlaySoundTailTime + 1) {
            if (i <= 0) {
//                list_.add(musicId_.get(11));
                list_.add(musicId_[14])
            } else if (i > 0 && i <= 10) {
                list_.add(musicId_[i])
                if (0 == level) list_.add(musicId_[11]) else if (1 == level) list_.add(
                    musicId_[12]
                ) else if (2 == level) list_.add(musicId_[13])
            } else {
                if (i <= 99) {
                    val ten = i / 10
                    val unit = i % 10
                    isShouldAddDi = if (isShouldAddDi) {
                        if (0 == level) list_.add(musicId_[11]) else if (1 == level) list_.add(
                            musicId_[12]
                        ) else if (2 == level) list_.add(musicId_[13])
                        list_.add(musicId_[11])
                        false


                    } else {
                        if (1 == ten) {
                            list_.add(musicId_[10])
                        } else {
                            list_.add(musicId_[ten])
                        }
                        if (0 == unit) {
                            list_.add(musicId_[10])
                        } else {
                            list_.add(musicId_[unit])
                        }
                        true
                    }
                } else {
                    list_.add(musicId_[11])
                    list_.add(musicId_[15])
                }
            }
        }
    }

    fun playDingdong() {
        clear()
        logD("playDingdong")
        list_.clear()
        list_.add(musicId_[0])
        playSound()
    }

    fun playWuwu() {
        clear()
        logD("wuwuwuwuwuwu")
        list_.clear()
        list_.add(musicId_[0])
        for (i in 0..8) {
//            list_.add(musicId_.get(11));
            list_.add(musicId_[14])
        }
        playSound()
    }

    fun playWuwuWithoutDingdong() {
        clear()
        logD("playWuwuWithoutDingdong")
        list_.clear()
        for (i in 0 until PlaySoundTailTime) {
//            list_.add(musicId_.get(11));
            list_.add(musicId_[14])
        }
        playSound()
    }

    companion object {
        private var instance: PlaySound? = null
        fun getInstance(context: Context): PlaySound {
            if (instance == null) {
                synchronized(PlaySound::class.java) {
                    if (instance == null) {
                        instance = PlaySound(context)
                    }
                }
            }
            return instance!!
        }
    }

    //构造
    init {
        list_ = ArrayList()
        musicId_ = HashMap()
        soundPool_ = if (Build.VERSION.SDK_INT >= 21) {
            val attributes = AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC) //                    .setFlags(AudioAttributes.FLAG_HW_AV_SYNC)
                //在 21<SDK<24 是没有任何问题的 但是在SDK>24(7.0 & 7.1.1) 就会出现无法播放
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            SoundPool.Builder()
                .setMaxStreams(15)
                .setAudioAttributes(attributes)
                .build()
        } else {
            SoundPool(15, AudioManager.STREAM_MUSIC, 100)
        }

        //通过load方法加载指定音频流，并将返回的音频ID放入musicId中
        musicId_[0] = soundPool_!!.load(context, R.raw.dingdong, 10) //0-1
        musicId_[1] = soundPool_!!.load(context, R.raw.one, 1) //1-2
        musicId_[2] = soundPool_!!.load(context, R.raw.two, 1) //2-3
        musicId_[3] = soundPool_!!.load(context, R.raw.three, 1)
        musicId_[4] = soundPool_!!.load(context, R.raw.four, 1)
        musicId_[5] = soundPool_!!.load(context, R.raw.five, 1)
        musicId_[6] = soundPool_!!.load(context, R.raw.six, 1)
        musicId_[7] = soundPool_!!.load(context, R.raw.seven, 1)
        musicId_[8] = soundPool_!!.load(context, R.raw.eight, 1)
        musicId_[9] = soundPool_!!.load(context, R.raw.nine, 1)
        musicId_[10] = soundPool_!!.load(context, R.raw.ten, 1)
        musicId_[11] = soundPool_!!.load(context, R.raw.non, 1)
        musicId_[12] = soundPool_!!.load(context, R.raw.di, 1)
        musicId_[13] = soundPool_!!.load(context, R.raw.didi, 1)
        musicId_[14] = soundPool_!!.load(context, R.raw.wu, 1)
        musicId_[15] = soundPool_!!.load(context, R.raw.dudu, 1)
        logD("musicId_ $musicId_")
    }
}