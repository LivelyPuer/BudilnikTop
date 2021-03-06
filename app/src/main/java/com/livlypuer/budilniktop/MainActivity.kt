package com.livlypuer.budilniktop

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.RecognizerResultsIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.livlypuer.budilniktop.bdKotlin.DBManager
import com.livlypuer.budilniktop.bdKotlin.TimeModel
import com.livlypuer.budilniktop.services.MusicService
import com.livlypuer.budilniktop.services.TimeoutService
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        setContent { MainContent() }

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
                    title = { Text("Super-?????????? ??????????????????", color = Color.White) },
                    backgroundColor = Color(0xff0f9d58)
                )
            },
            content = { MyContent() },
            floatingActionButton = {
                AddTweetButton()
            }
        )
    }

    @Composable
    fun AddTweetButton() {
        FloatingActionButton(onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            resultLauncher.launch(intent)
        }) {
            Icon(
                Icons.Default.Call, ""
            )
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                if (data != null) {
                    val textArray = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val text = textArray!![0].lowercase()
                    Log.d("MYCALL", text);
                    val regex = """[0-9]+:[0-9]+""".toRegex()
                    var time = "";
                    regex.find(text)?.groupValues?.get(0)?.let { time = it.toString() }
                    if (time != "" && ("????????" in text || "??????????" in text || "??????" in text) && "??????????" in text) {
                        val formatter = DateTimeFormatter.ofPattern("H:mm")
                            .withZone(ZoneId.systemDefault())
                        mDBConnector!!.insertTime(
                            TimeModel(
                                null, LocalTime.parse(time, formatter)
                            )
                        )
                        saverTimes.updateTimesList();
                    }else if ("????????" in text){
                        startActivity(Intent(applicationContext, QuizeActivity::class.java))
                    }
                }
            }
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
                onClick = {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            CreateBudilnicActivity::class.java
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
            ) {
                Text(text = "???????????????? ??????????????????", color = Color.White)
            }
            ListWithHeader()


        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ListWithHeader() {

        LazyColumn(state = rememberLazyListState()) {
            stickyHeader {
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
    fun StickyHeaderTimeLine() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(144, 238, 144, 255)),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = "??????????", fontSize = 20.sp);
            Text(text = "????", fontSize = 15.sp)
            Text(text = "????", fontSize = 15.sp)
            Text(text = "????", fontSize = 15.sp)
            Text(text = "????", fontSize = 15.sp)
            Text(text = "????", fontSize = 15.sp)
            Text(text = "????", fontSize = 15.sp)
            Text(text = "????", fontSize = 15.sp)
            Text(
                text = "????????????????????", fontSize = 20.sp
            )

        }
        Spacer(modifier = Modifier.size(10.dp))
    }

    @Composable
    fun TimeLine(time: TimeModel) {
        var active by remember { mutableStateOf(false) }
        active = time.active
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Text(text = time.niceStringTime, fontSize = 30.sp);
            Text(text = if (time.weeks[0] == true) "???" else "???", fontSize = 20.sp)
            Text(text = if (time.weeks[1] == true) "???" else "???", fontSize = 20.sp)
            Text(text = if (time.weeks[2] == true) "???" else "???", fontSize = 20.sp)
            Text(text = if (time.weeks[3] == true) "???" else "???", fontSize = 20.sp)
            Text(text = if (time.weeks[4] == true) "???" else "???", fontSize = 20.sp)
            Text(text = if (time.weeks[5] == true) "???" else "???", fontSize = 20.sp)
            Text(text = if (time.weeks[6] == true) "???" else "???", fontSize = 20.sp)
            Button(
                onClick = {
                    active = !time.active
                    time.active = active
                    mDBConnector!!.updateTime(time)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (active) Color(0XFF0F9D58) else Color(
                        0xFFA2A2A2
                    )
                )
            ) {
                Text(
                    text = if (active) " ??????????????  " else "????????????????",
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
