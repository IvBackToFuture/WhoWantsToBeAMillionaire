package com.example.whowantstobeamillionaire.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import com.example.whowantstobeamillionaire.R

class AnswersAudioService : Service() {

    private lateinit var playerCorrect: MediaPlayer
    private lateinit var playerLose: MediaPlayer

    override fun onBind(intent: Intent): Nothing? = null

    override fun onCreate() {
        super.onCreate()
        playerCorrect = MediaPlayer.create(this, R.raw.sound_correct)
        playerLose = MediaPlayer.create(this, R.raw.sound_lose)
        playerLose.isLooping = false
        playerCorrect.isLooping = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val status = intent?.extras?.getBoolean(STATUS)
        if (status != null && status) {
            playerCorrect.start()
        } else {
            playerLose.start()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        playerCorrect.stop()
        playerLose.stop()
    }

    companion object {
        const val STATUS = "STATUS"
    }
}