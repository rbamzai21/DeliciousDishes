package com.project.deliciousdishes.activities

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import java.lang.NullPointerException

object AudioPlay {

    private lateinit var mediaPlayer: MediaPlayer

    fun initAudio() {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource("https://prodbyrishab.com/foodapp/bgmusic.mp3")
    }

    fun playAudio() {
        if (!mediaPlayer.isPlaying)
        {
            mediaPlayer!!.prepare()
            mediaPlayer.start()
        }
    }

    fun stopAudio() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer!!.stop()
        }
    }
}
