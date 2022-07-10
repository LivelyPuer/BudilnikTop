package com.livlypuer.budilniktop.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.livlypuer.budilniktop.MainActivity
import com.livlypuer.budilniktop.R
import com.livlypuer.budilniktop.bdKotlin.DBManager
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class TimeoutService : Service() {
    var mDBConnector: DBManager? = null

    companion object {
        const val INTERVAL: Long = 1000 //variable for execute services every 1 minute
        var mediaPlayer: MediaPlayer? = null

    }

    // Идентификатор уведомления
    private val NOTIFY_ID = 101
    private val CHANNEL_ID = "Budilnik channel"
    private val mHandler: Handler = Handler() // run on another Thread to avoid crash
    private var mTimer: Timer? = null // timer handling
    var ms: Intent? = null;
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.music)
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
        mDBConnector = DBManager(this)
        TimerService()

    }

    fun TimerService() {
        if (mTimer != null) mTimer!!.cancel() else mTimer = Timer() // recreate new timer
        mTimer!!.scheduleAtFixedRate(TimeDisplayTimerTask(), 0, INTERVAL) // schedule task
    }

    override fun onDestroy() {
        Toast.makeText(this, "In Destroy", Toast.LENGTH_SHORT)
            .show() //display toast when method called
        with(mTimer) { this?.cancel() } //cancel the timer
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    //inner class of TimeDisplayTimerTask
    private inner class TimeDisplayTimerTask : TimerTask() {

        var min: Int = LocalTime.now().minute

        @SuppressLint("SimpleDateFormat")
        override fun run() {

            mHandler.post {
                val current = LocalTime.now()
                if (min != current.minute) {
                    min = current.minute
                    val mCalendar = Calendar.getInstance()
                    var day_week = mCalendar[Calendar.DAY_OF_WEEK]
                    if (day_week == 0){
                        day_week = 6;
                    }else{
                        day_week -= 1
                    }
                    val formatter = DateTimeFormatter.ofPattern("H:m")
                    val formatted = current.format(formatter)

                    if (mDBConnector!!.existTimes(formatted) && mDBConnector!!.selectTime(formatted).weeks[day_week] == true) {
                        val notificationIntent =
                            Intent(applicationContext, MainActivity::class.java)
                        val pendingIntent = PendingIntent.getActivity(
                            applicationContext,
                            0, notificationIntent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        val notificationBuilder: NotificationCompat.Builder =
                            NotificationCompat.Builder(applicationContext, MainActivity.CHANNEL_ID)
                                .setSmallIcon(R.drawable.clock)
                                .setContentText("Супер-пупер будильник")
                                .setContentTitle(current.format(DateTimeFormatter.ofPattern("HH:mm")))
                                .addAction(R.drawable.cross_svgrepo_com, "Отключить", pendingIntent)
                                .setAutoCancel(true)

                        val notificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                        notificationManager.notify(
                            MainActivity.NOTIFICATION_ID,
                            notificationBuilder.build()
                        )
                        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.music)
                        mediaPlayer.start()
                        mediaPlayer.isLooping = true;
                        Toast.makeText(applicationContext, "Notify", Toast.LENGTH_SHORT).show()
                    } else {

                    }
                }
            }
        }
    }


}