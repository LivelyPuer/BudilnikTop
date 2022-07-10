package com.livlypuer.budilniktop.bdKotlin

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.BinaryOperator
import kotlin.collections.ArrayList

class DBManager(context: Context?) {
    private val mDataBase: SQLiteDatabase

    companion object {
        private const val DATABASE_NAME = "budilnik.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME_TIMES = "Times"

        private const val COLUMN_TIME_ID = "_id"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_ACTIVE = "active"
        private const val COLUMN_MONDAY = "monday"
        private const val COLUMN_TUESDAY = "tuesday"
        private const val COLUMN_WEDNESDAY = "wednesday"
        private const val COLUMN_THURSDAY = "thursday"
        private const val COLUMN_FRIDAY = "friday"
        private const val COLUMN_SATURDAY = "saturday"
        private const val COLUMN_SUNDAY = "sunday"
        private const val COLUMN_DELETED = "deleted"

        private const val NUM_COLUMN_TIME_ID = 0
        private const val NUM_COLUMN_TIME = 1
        private const val NUM_COLUMN_ACTIVE = 2
        private const val NUM_COLUMN_MONDAY = 3
        private const val NUM_COLUMN_TUESDAY = 4
        private const val NUM_COLUMN_WEDNESDAY = 5
        private const val NUM_COLUMN_THURSDAY = 6
        private const val NUM_COLUMN_FRIDAY = 7
        private const val NUM_COLUMN_SATURDAY = 8
        private const val NUM_COLUMN_SUNDAY = 9
        private const val NUM_COLUMN_DELETED = 10
    }

    init {
        val mOpenHelper = OpenHelper(context)
        mDataBase = mOpenHelper.getWritableDatabase()
    }

    fun existTimes(id: Long): Boolean {
        val mCursor = mDataBase.query(
            TABLE_NAME_TIMES,
            null,
            COLUMN_TIME_ID + " = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        return mCursor.count != 0
    }

    fun existTimes(time: String): Boolean {
        val mCursor = mDataBase.query(
            TABLE_NAME_TIMES,
            null,
            COLUMN_TIME + " = ?",
            arrayOf(time),
            null,
            null,
            null
        )
        return mCursor.count != 0
    }
    fun nextBudilnik(): TimeModel?{
        var timeMinutes = LocalTime.now().minute + LocalTime.now().hour * 60;
        val mCalendar = Calendar.getInstance()
        var day_of_week = mCalendar[Calendar.DAY_OF_WEEK]
//        if (day_of_week == 0){
//            day_of_week = 6;
//        }else{
//            day_of_week -= 1
//        }
        Log.d("MY", day_of_week.toString())
        val mCursor: Cursor;
        when(day_of_week) {
            2 -> mCursor = mDataBase.query(TABLE_NAME_TIMES, null, COLUMN_MONDAY + " = ?", arrayOf("1"), null, null, null)
            3 -> mCursor = mDataBase.query(TABLE_NAME_TIMES, null, COLUMN_TUESDAY + " = ?", arrayOf("1"), null, null, null)
            4 -> mCursor = mDataBase.query(TABLE_NAME_TIMES, null, COLUMN_WEDNESDAY + " = ?", arrayOf("1"), null, null, null)
            5 -> mCursor = mDataBase.query(TABLE_NAME_TIMES, null, COLUMN_THURSDAY + " = ?", arrayOf("1"), null, null, null)
            6 -> mCursor = mDataBase.query(TABLE_NAME_TIMES, null, COLUMN_FRIDAY + " = ?", arrayOf("1"), null, null, null)
            7 -> mCursor = mDataBase.query(TABLE_NAME_TIMES, null, COLUMN_SATURDAY + " = ?", arrayOf("1"), null, null, null)
            1 -> mCursor = mDataBase.query(TABLE_NAME_TIMES, null, COLUMN_SUNDAY + " = ?", arrayOf("1"), null, null, null)
            else -> {mCursor = mDataBase.query(TABLE_NAME_TIMES, null, null, null, null, null, null)}
        }
        mCursor.moveToFirst()
        var min = TimeModel(null, LocalTime.MAX)
        if (!mCursor.isAfterLast) {
            do {
                val timeTmp = selectTime(mCursor.getLong(NUM_COLUMN_TIME_ID))
                val minutesTmp = timeTmp.time.hour * 60 + timeTmp.time.minute;
                Log.d("MY", minutesTmp.toString() + " " + min.time.hour * 60 + min.time.minute)
                if (timeMinutes < minutesTmp && minutesTmp < min.time.hour * 60 + min.time.minute){
                    min = timeTmp
                }
            } while (mCursor.moveToNext())
        }
        if (min.id == null){
            return null
        }
        return min
    }


    fun fullExistTimes(time: TimeModel): Boolean {
        val mCursor = mDataBase.query(
            TABLE_NAME_TIMES,
            null,
            COLUMN_TIME + " = ? AND " + COLUMN_MONDAY + " = ? AND " + COLUMN_TUESDAY + " = ? AND " + COLUMN_WEDNESDAY + " = ? AND " + COLUMN_THURSDAY + " = ? AND " + COLUMN_FRIDAY + " = ? AND " + COLUMN_SATURDAY + " = ? AND " + COLUMN_SUNDAY + " = ?",
            arrayOf(time.stringTime, if (time.weeks[0] == true) "1" else "0", if (time.weeks[1] == true) "1" else "0", if (time.weeks[2] == true) "1" else "0", if (time.weeks[3] == true) "1" else "0", if (time.weeks[4] == true) "1" else "0", if (time.weeks[5] == true) "1" else "0",if (time.weeks[6] == true) "1" else "0"),
            null,
            null,
            null
        )
        return mCursor.count != 0
    }

    fun insertTime(time: TimeModel): Long {
        val cv = ContentValues()
        val weeks = time.weeks
        cv.put(COLUMN_TIME, time.stringTime)
        cv.put(COLUMN_ACTIVE, if (time.active) 1 else 0)
        cv.put(COLUMN_MONDAY, if (weeks[0] == true) 1 else 0)
        cv.put(COLUMN_TUESDAY, if (weeks[1] == true) 1 else 0)
        cv.put(COLUMN_WEDNESDAY, if (weeks[2] == true) 1 else 0)
        cv.put(COLUMN_THURSDAY, if (weeks[3] == true) 1 else 0)
        cv.put(COLUMN_FRIDAY, if (weeks[4] == true) 1 else 0)
        cv.put(COLUMN_SATURDAY, if (weeks[5] == true) 1 else 0)
        cv.put(COLUMN_SUNDAY, if (weeks[6] == true) 1 else 0)
        cv.put(COLUMN_DELETED, 0)
        return mDataBase.insert(TABLE_NAME_TIMES, null, cv)
    }

    fun safeInsertTime(time: TimeModel): Long {
        return if (time.id != null) {
            -1
        } else {
            insertTime(time)
        }

    }
    fun updateTime(time: TimeModel): Long {
        val cv = ContentValues()
        val weeks = time.weeks
        cv.put(COLUMN_TIME, time.stringTime)
        cv.put(COLUMN_ACTIVE, if (time.active) 1 else 0)
        cv.put(COLUMN_MONDAY, if (weeks[0] == true) 1 else 0)
        cv.put(COLUMN_TUESDAY, if (weeks[1] == true) 1 else 0)
        cv.put(COLUMN_WEDNESDAY, if (weeks[2] == true) 1 else 0)
        cv.put(COLUMN_THURSDAY, if (weeks[3] == true) 1 else 0)
        cv.put(COLUMN_FRIDAY, if (weeks[4] == true) 1 else 0)
        cv.put(COLUMN_SATURDAY, if (weeks[5] == true) 1 else 0)
        cv.put(COLUMN_SUNDAY, if (weeks[6] == true) 1 else 0)
        Log.d("MY", "UPDATE: " + cv[COLUMN_TIME].toString())
        return mDataBase.update(
            TABLE_NAME_TIMES,
            cv,
            COLUMN_TIME_ID + " = ?",
            arrayOf(time.id.toString())
        )
            .toLong()
    }

    fun selectTime(id: Long): TimeModel {
        @SuppressLint("Recycle") val mCursor = mDataBase.query(
            TABLE_NAME_TIMES,
            null,
            COLUMN_TIME_ID + " = ? AND " + COLUMN_DELETED + " != 1",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        mCursor.moveToFirst()
        Log.d("MY", mCursor.getString(NUM_COLUMN_TIME));
        val formatter = DateTimeFormatter.ofPattern(TimeModel.PATTERN)
            .withZone(ZoneId.systemDefault())
        val time =
            LocalTime.parse(mCursor.getString(NUM_COLUMN_TIME), formatter)
        val active = mCursor.getInt(NUM_COLUMN_ACTIVE) == 1
        val mon = mCursor.getInt(NUM_COLUMN_MONDAY) == 1
        val tue = mCursor.getInt(NUM_COLUMN_TUESDAY) == 1
        val wed = mCursor.getInt(NUM_COLUMN_WEDNESDAY) == 1
        val thu = mCursor.getInt(NUM_COLUMN_THURSDAY) == 1
        val fri = mCursor.getInt(NUM_COLUMN_FRIDAY) == 1
        val sat = mCursor.getInt(NUM_COLUMN_SATURDAY) == 1
        val sun = mCursor.getInt(NUM_COLUMN_SUNDAY) == 1
        return TimeModel(id, time, active = active, mon, tue, wed, thu, fri, sat, sun)
    }

    fun deleteTime(id: Long) {
        val cv = ContentValues()
        cv.put(COLUMN_DELETED, 1)
        mDataBase.update(TABLE_NAME_TIMES, cv, COLUMN_TIME_ID + " = ?", arrayOf(id.toString()))
    }

    fun deleteTime(time: String) {
        val cv = ContentValues()
        cv.put(COLUMN_DELETED, 1)
        mDataBase.update(TABLE_NAME_TIMES, cv, COLUMN_TIME + " = ?", arrayOf(time))
    }

    fun selectTime(time: String): TimeModel {
        @SuppressLint("Recycle") val mCursor = mDataBase.query(
            TABLE_NAME_TIMES,
            null,
            COLUMN_TIME + " = ? AND " + COLUMN_DELETED + " != 1",
            arrayOf(time),
            null,
            null,
            null
        )
        mCursor.moveToFirst()
        val formatter = DateTimeFormatter.ofPattern(TimeModel.PATTERN)
            .withZone(ZoneId.systemDefault())
        val time =
            LocalTime.parse(mCursor.getString(NUM_COLUMN_TIME), formatter)
        val id = mCursor.getLong(NUM_COLUMN_TIME_ID)
        val active = mCursor.getInt(NUM_COLUMN_ACTIVE) == 1
        val mon = mCursor.getInt(NUM_COLUMN_MONDAY) == 1
        val tue = mCursor.getInt(NUM_COLUMN_TUESDAY) == 1
        val wed = mCursor.getInt(NUM_COLUMN_WEDNESDAY) == 1
        val thu = mCursor.getInt(NUM_COLUMN_THURSDAY) == 1
        val fri = mCursor.getInt(NUM_COLUMN_FRIDAY) == 1
        val sat = mCursor.getInt(NUM_COLUMN_SATURDAY) == 1
        val sun = mCursor.getInt(NUM_COLUMN_SUNDAY) == 1
        return TimeModel(id, time, active = active, mon, tue, wed, thu, fri, sat, sun)
    }

    fun selectAllTimes(): ArrayList<TimeModel> {
        val mCursor =
            mDataBase.query(TABLE_NAME_TIMES, null, null, null, null, null, null)
        val arr: ArrayList<TimeModel> = ArrayList()
        mCursor.moveToFirst()
        if (!mCursor.isAfterLast) {
            do {
                arr.add(selectTime(mCursor.getLong(NUM_COLUMN_TIME_ID)))
            } while (mCursor.moveToNext())
        }
        return arr
    }

    private class OpenHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(
            context,
            DATABASE_NAME,
            null,
            DATABASE_VERSION
        ) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE " + TABLE_NAME_TIMES + " (" +
                        COLUMN_TIME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TIME + " TEXT, " +
                        COLUMN_ACTIVE + " INTEGER, " +
                        COLUMN_MONDAY + " INTEGER, " +
                        COLUMN_TUESDAY + " INTEGER, " +
                        COLUMN_WEDNESDAY + " INTEGER, " +
                        COLUMN_THURSDAY + " INTEGER, " +
                        COLUMN_FRIDAY + " INTEGER, " +
                        COLUMN_SATURDAY + " INTEGER, " +
                        COLUMN_SUNDAY + " INTEGER, " +
                        COLUMN_DELETED + " INTEGER);"
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TIMES)
            onCreate(db)
        }
    }


}