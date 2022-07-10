package com.livlypuer.budilniktop

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.livlypuer.budilniktop.bdKotlin.DBManager
import com.livlypuer.budilniktop.bdKotlin.TimeModel

class SaverTimes(private val activity: Activity) {
    val timesList = mutableStateListOf<TimeModel>()
    var mDBConnector: DBManager = DBManager(activity)

    init {
        updateTimesList()
    }

    fun updateTimesList() {
        timesList.clear();
        for (time in mDBConnector.selectAllTimes()) {
            timesList.add(time);
        }
    }

    fun addTime(time: TimeModel, activateTime: Boolean) {
        if (!mDBConnector.existTimes(time.stringTime)) {
            mDBConnector.safeInsertTime(time)
            timesList.add(time)
        }
    }

    fun delTime(time: String) {
        mDBConnector.deleteTime(time)
    }

    fun getAllTimes(): ArrayList<TimeModel> {
        return mDBConnector.selectAllTimes()
    }

    fun countTimes(): Int {
        return getAllTimes().size
    }

    fun getActiveTime(time: String): Boolean {
        return mDBConnector.selectTime(time).active
    }
}