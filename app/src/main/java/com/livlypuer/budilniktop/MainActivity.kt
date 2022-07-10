package com.livlypuer.budilniktop

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.livlypuer.budilniktop.bdKotlin.DBManager
import com.livlypuer.budilniktop.bdKotlin.TimeModel
import com.livlypuer.budilniktop.services.TimeoutService
import java.util.*


class MainActivity : ComponentActivity() {
    companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "channelID"
    }
    lateinit var saverTimes: SaverTimes;
    var mDBConnector: DBManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InitializationParams()
        setContent { MainContent() }
        if (TimeoutService.mediaPlayer != null){
            Log.d("MY", "STOPED")
            TimeoutService.mediaPlayer!!.isLooping = false
            TimeoutService.mediaPlayer!!.pause()
            TimeoutService.mediaPlayer!!.stop()
            TimeoutService.mediaPlayer!!.release()
        }
        val mService = startService(Intent(this, TimeoutService::class.java))
    }

    fun InitializationParams() {
        saverTimes = SaverTimes(this)
        mDBConnector = DBManager(this)
    }

    override fun onResume() {
        super.onResume()
        saverTimes.updateTimesList();
    }
    @Composable
    fun MainContent() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Super-пупер будильник", color = Color.White) },
                    backgroundColor = Color(0xff0f9d58)
                )
            },
            content = { MyContent() }
        )
    }
    @Composable
    fun MyContent() {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Header()
            Button(
                onClick = {startActivity(Intent(this@MainActivity, CreateBudilnicActivity::class.java)) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
            ) {
                Text(text = "Добавить будильник", color = Color.White)
            }
            ListWithHeader()


        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ListWithHeader() {

        LazyColumn(state = rememberLazyListState()) {
            stickyHeader{
                StickyHeaderTimeLine()
            }
            items(saverTimes.timesList) { time ->
                TimeLine(time)
            }
        }
    }

    @Composable
    fun Header() {
        Spacer(modifier = Modifier.size(20.dp))

    }
    @Composable
    fun StickyHeaderTimeLine(){
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color(144, 238, 144, 255)), horizontalArrangement = Arrangement.SpaceAround) {
            Text(text = "Время", fontSize = 20.sp);
            Text(text = "Пн", fontSize = 15.sp)
            Text(text = "Вт", fontSize = 15.sp)
            Text(text = "Ср", fontSize = 15.sp)
            Text(text = "Чт", fontSize = 15.sp)
            Text(text = "Пт", fontSize = 15.sp)
            Text(text = "Сб", fontSize = 15.sp)
            Text(text = "Вс", fontSize = 15.sp)
            Text(
                text = "Активность", fontSize = 20.sp
            )

        }
        Spacer(modifier = Modifier.size(10.dp))
    }

    @Composable
    fun TimeLine(time: TimeModel) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Text(text = time.niceStringTime, fontSize = 30.sp);
            Text(text = if (time.weeks[0] == true) "✅" else "❌", fontSize = 20.sp)
            Text(text = if (time.weeks[1] == true) "✅" else "❌", fontSize = 20.sp)
            Text(text = if (time.weeks[2] == true) "✅" else "❌", fontSize = 20.sp)
            Text(text = if (time.weeks[3] == true) "✅" else "❌", fontSize = 20.sp)
            Text(text = if (time.weeks[4] == true) "✅" else "❌", fontSize = 20.sp)
            Text(text = if (time.weeks[5] == true) "✅" else "❌", fontSize = 20.sp)
            Text(text = if (time.weeks[6] == true) "✅" else "❌", fontSize = 20.sp)
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (time.active) Color(0XFF0F9D58) else Color(
                    0xFFA2A2A2
                )
                )
            ) {
                Text(
                    text = if (time.active) " Включен  " else "Выключен",
                    color = Color.White
                )
            }

        }
        Spacer(modifier = Modifier.size(10.dp))

    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MainContent()
    }
}
