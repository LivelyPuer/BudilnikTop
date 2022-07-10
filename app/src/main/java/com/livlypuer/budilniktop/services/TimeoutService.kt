package com.livlypuer.budilniktop.services

import com.livlypuer.budilniktop.R
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
import com.livlypuer.budilniktop.QuizeActivity
import com.livlypuer.budilniktop.bdKotlin.DBManager
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


        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel1 = NotificationChannel(CHANNEL_ID, "Budilnik", importance)
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel1)

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
                    if (day_week == 1) {
                        day_week = 6;
                    } else {
                        day_week -= 1
                    }
                    Log.d("MY", day_week.toString())
                    val formatter = DateTimeFormatter.ofPattern("H:m")
                    val formatted = current.format(formatter)

                    if (mDBConnector!!.existTimes(formatted) && mDBConnector!!.selectTime(formatted).weeks[day_week] == true) {
                        if (!mDBConnector!!.selectTime(formatted).active) {
                            var t = mDBConnector!!.selectTime(formatted)
                            t.active = true
                            mDBConnector!!.updateTime(t);
                        } else {
                            val notificationIntent =
                                Intent(applicationContext, QuizeActivity::class.java)
                            val pendingIntent = PendingIntent.getActivity(
                                applicationContext,
                                0, notificationIntent,
                                PendingIntent.FLAG_IMMUTABLE
                            )

                            val notificationBuilder: NotificationCompat.Builder =
                                NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.clock)
                                    .setContentText("Супер-пупер будильник")
                                    .setContentTitle(current.format(DateTimeFormatter.ofPattern("HH:mm")))
                                    .addAction(R.drawable.clock, "Отключить", pendingIntent)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)

                            val notificationManager =
                                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                            notificationManager.notify(
                                MainActivity.NOTIFICATION_ID,
                                notificationBuilder.build()
                            )

                            startService(Intent(applicationContext, MusicService::class.java))

                            Toast.makeText(applicationContext, "Notify", Toast.LENGTH_SHORT).show()
                        }

                    } else {

                    }
                }
            }
        }
    }


}