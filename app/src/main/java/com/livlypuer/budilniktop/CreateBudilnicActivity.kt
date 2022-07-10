package com.livlypuer.budilniktop

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.Time
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.livlypuer.budilniktop.bdKotlin.DBManager
import com.livlypuer.budilniktop.bdKotlin.TimeModel
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class CreateBudilnicActivity : ComponentActivity() {
    lateinit var saverTimes: SaverTimes;
    var mDBConnector: DBManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InitializationParams()
        setContent { MainContent() }
    }

    fun InitializationParams() {
        saverTimes = SaverTimes(this)
        mDBConnector = DBManager(this)
    }

    @Composable
    fun MainContent() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Создать будильник", color = Color.White) },
                    backgroundColor = Color(0xff0f9d58)
                )
            },
            content = { MyContent() }
        )
    }

    @Composable
    fun MyContent() {
        val mContext = LocalContext.current

        // Declaring and initializing a calendar
        val mCalendar = Calendar.getInstance()

        val mHour = mCalendar[Calendar.HOUR_OF_DAY]
        val mMinute = mCalendar[Calendar.MINUTE]
        var mMonday = remember { mutableStateOf(false) };
        var mTuesday = remember { mutableStateOf(false) };
        var mWednesday = remember { mutableStateOf(false) };
        var mThursday = remember { mutableStateOf(false) };
        var mFriday = remember { mutableStateOf(false) };
        var mSaturday = remember { mutableStateOf(false) };
        var mSunday = remember { mutableStateOf(false) };
        // Value for storing time as a string
        val mTime = remember { mutableStateOf("") }

        // Creating a TimePicker dialod
        val mTimePickerDialog = TimePickerDialog(
            mContext,
            { _, mHour: Int, mMinute: Int ->
                mTime.value = "$mHour:$mMinute"
            }, mHour, mMinute, true
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                Button(
                    onClick = {

                        mTimePickerDialog.show()


                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
                ) {
                    Text(text = "Выберите время", color = Color.White)
                }
                Spacer(modifier = Modifier.size(50.dp))
                Text(
                    text = if (mTime.value != "") mTime.value else LocalTime.now().hour.toString() + ":" + LocalTime.now().minute,
                    color = Color.Black,
                    fontSize = 36.sp
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    modifier = Modifier.size(55.dp),

                    onClick = {
                        mMonday.value = !mMonday.value;

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (mMonday.value) Color(
                            0XFF0F9D58
                        ) else Color(0xFF696969)
                    )
                ) {
                    Text(text = "Пн", color = Color.White)
                }
                Button(
                    modifier = Modifier.size(55.dp),

                    onClick = {
                        mTuesday.value = !mTuesday.value;

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (mTuesday.value) Color(
                            0XFF0F9D58
                        ) else Color(0xFF696969)
                    )
                ) {
                    Text(text = "Вт", color = Color.White)
                }
                Button(
                    modifier = Modifier.size(55.dp),

                    onClick = {
                        mWednesday.value = !mWednesday.value;

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (mWednesday.value) Color(
                            0XFF0F9D58
                        ) else Color(0xFF696969)
                    )
                ) {
                    Text(text = "Ср", color = Color.White)
                }
                Button(
                    onClick = {
                        mThursday.value = !mThursday.value;

                    },
                    modifier = Modifier.size(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (mThursday.value) Color(
                            0XFF0F9D58
                        ) else Color(0xFF696969)
                    )
                ) {
                    Text(text = "Чт", color = Color.White)
                }
                Button(
                    modifier = Modifier.size(55.dp),

                    onClick = {
                        mFriday.value = !mFriday.value;

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (mFriday.value) Color(
                            0XFF0F9D58
                        ) else Color(0xFF696969)
                    )
                ) {
                    Text(text = "Пт", color = Color.White)
                }
                Button(
                    modifier = Modifier.size(55.dp),

                    onClick = {
                        mSaturday.value = !mSaturday.value;

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (mSaturday.value) Color(
                            0XFF0F9D58
                        ) else Color(0xFF696969)
                    )
                ) {
                    Text(text = "Сб", color = Color.White)
                }
                Button(
                    modifier = Modifier.size(55.dp),

                    onClick = {
                        mSunday.value = !mSunday.value;

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (mSunday.value) Color(
                            0XFF0F9D58
                        ) else Color(0xFF696969)
                    )
                ) {
                    Text(text = "Вс", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.size(20.dp))

            Button(
                onClick = {
                    val formatter = DateTimeFormatter.ofPattern(TimeModel.PATTERN)
                        .withZone(ZoneId.systemDefault())
                    val time =
                        LocalTime.parse(
                            if (mTime.value == "") LocalTime.now().hour.toString() + ":" + LocalTime.now().minute else mTime.value,
                            formatter
                        )
                    if (!mMonday.value && !mTuesday.value && !mWednesday.value && !mThursday.value && !mFriday.value && !mSaturday.value && !mSunday.value
                    ) {
                        mMonday.value = true;
                        mTuesday.value = true;
                        mWednesday.value = true;
                        mThursday.value = true;
                        mFriday.value = true;
                        mSaturday.value = true;
                        mSunday.value = true;
                    }
                    val timemodel = TimeModel(
                        null, time, true,
                        mMonday.value,
                        mTuesday.value,
                        mWednesday.value,
                        mThursday.value,
                        mFriday.value,
                        mSaturday.value,
                        mSunday.value
                    )
                    if (mDBConnector?.fullExistTimes(timemodel) == false) {
                        mDBConnector?.insertTime(
                            timemodel
                        )
                        Log.d("MY", "1 out")
                        this@CreateBudilnicActivity.finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Такой будильник уже существует",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(
                        0XFF0F9D58
                    )
                )
            ) {
                Text(text = "Добавить будильник", color = Color.White)
            }
            Spacer(modifier = Modifier.size(20.dp))

            Button(
                onClick = {
                    this@CreateBudilnicActivity.finish()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(
                        0XFF0F9D58
                    )
                )
            ) {
                Text(text = "<<Назад", color = Color.White)
            }

        }
    }
}