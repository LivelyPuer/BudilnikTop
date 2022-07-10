package com.livlypuer.budilniktop.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.livlypuer.budilniktop.R

class MusicService : Service() {
    lateinit var mediaPlayer: MediaPlayer;

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        return START_STICKY
    }
    override fun onCreate() {
        super.onCreate()
        val CHANNEL_ID = "my_channel_01"
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText("Супер-пупер будильник")
            .setContentTitle("Активный").build()

        startForeground(1, notification)
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.music)
        mediaPlayer.start()
        mediaPlayer.isLooping = true
    }
    override fun onDestroy() {
        mediaPlayer.stop();
        Toast.makeText(this, "<Будильник выключен>", Toast.LENGTH_SHORT)
            .show()
    }
}