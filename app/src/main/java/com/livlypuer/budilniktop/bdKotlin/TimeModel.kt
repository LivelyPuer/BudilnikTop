package com.livlypuer.budilniktop.bdKotlin

import android.util.Log
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TimeModel(
    id: Long?,
    var time: LocalTime,
    var active: Boolean = true,
    private var mon: Boolean = true,
    private var tue: Boolean = true,
    private var wed: Boolean = true,
    private var thu: Boolean = true,
    private var fri: Boolean = true,
    private var sat: Boolean = true,
    private var sun: Boolean = true
) : BaseModel(id) {

    val weeks: Array<Boolean?>
        get() {
            val weeks = arrayOfNulls<Boolean>(7)
            weeks[0] = mon
            weeks[1] = tue
            weeks[2] = wed
            weeks[3] = thu
            weeks[4] = fri
            weeks[5] = sat
            weeks[6] = sun
            return weeks
        }

    fun isWeek(weekOfDay: Int): Boolean? {
        val weeks = weeks
        Log.d("MY", weekOfDay.toString() + " " + weeks[weekOfDay])
        return weeks[weekOfDay]
    }

    fun setMon(active: Boolean) {
        mon = active
    }

    fun setTue(active: Boolean) {
        tue = active
    }

    fun setWed(active: Boolean) {
        wed = active
    }

    fun setThu(active: Boolean) {
        thu = active
    }

    fun setFri(active: Boolean) {
        fri = active
    }

    fun setSat(active: Boolean) {
        sat = active
    }

    fun setSun(active: Boolean) {
        sun = active
    }

    val stringTime: String
        get() {
            val formatter = DateTimeFormatter.ofPattern(PATTERN).withZone(
                ZoneId.systemDefault()
            )
            return formatter.format(time)
        }
    val niceStringTime: String
        get() {
            val formatter = DateTimeFormatter.ofPattern(NICE_PATTERN).withZone(
                ZoneId.systemDefault()
            )
            return formatter.format(time)
        }

    companion object {
        var PATTERN = "H:m"
        var NICE_PATTERN = "HH:mm"
    }
}