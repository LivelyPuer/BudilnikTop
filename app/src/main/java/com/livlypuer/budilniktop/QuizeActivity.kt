package com.livlypuer.budilniktop

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.livlypuer.budilniktop.services.MusicService
import org.json.JSONObject
import org.json.JSONTokener
import java.net.URL
import java.util.concurrent.Executors

class QuizeActivity : ComponentActivity() {

    val pokemonJson = mutableStateOf(JSONTokener("{\"name\": \"voltorb\", \"is_mega\": false}").nextValue() as JSONObject)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainContent() }
        Executors.newSingleThreadExecutor().execute {
            val json = URL("https://pokeapi.co/api/v2/pokemon-form/${(1..300).random()}").readText()
            pokemonJson.value = JSONTokener(json).nextValue() as JSONObject
            val js = pokemonJson.value
            Log.d("MY", js.getString("name") + " " + js.getBoolean("is_mega"))
        }

    }

    @Composable
    fun MainContent() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Super-пупер квиз, чтобы не спал", color = Color.White) },
                    backgroundColor = Color(0xff0f9d58)
                )
            },

            content = { BaseContent()}
        )
    }

    @Composable
    fun BaseContent() {


        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Есть ли у ${(pokemonJson.value as JSONObject).getString("name")} мегаэволюция?")
            Spacer(modifier = Modifier.size(30.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Button(
                    onClick = {
                        if (pokemonJson.value.getBoolean("is_mega")){
                            Toast.makeText(applicationContext, "Верно", Toast.LENGTH_SHORT).show()
                            stopService(Intent(this@QuizeActivity, MusicService::class.java))
                            startActivity(Intent(this@QuizeActivity, MainActivity::class.java))
                        }else{
                            Toast.makeText(applicationContext, "Не верно", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
                ) {
                    Text(text = "Есть", color = Color.White)
                }
                Button(
                    onClick = {
                        if (!pokemonJson.value.getBoolean("is_mega")){
                            Toast.makeText(applicationContext, "Верно", Toast.LENGTH_SHORT).show()
                            stopService(Intent(this@QuizeActivity, MusicService::class.java))
                            startActivity(Intent(this@QuizeActivity, MainActivity::class.java))
                        }else{
                            Toast.makeText(applicationContext, "Не верно", Toast.LENGTH_SHORT).show()
                        }

                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
                ) {
                    Text(text = "Нет", color = Color.White)
                }

            }
        }
    }
}
