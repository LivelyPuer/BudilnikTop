package com.livlypuer.budilniktop.widget

import com.livlypuer.budilniktop.R
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.livlypuer.budilniktop.MainActivity
import com.livlypuer.budilniktop.bdKotlin.DBManager
import java.util.prefs.Preferences


class BudilnikWidget : GlanceAppWidget() {

    var mDBConnector: DBManager? = null

    @Composable
    override fun Content() {
        val context = LocalContext.current
        mDBConnector = DBManager(context)
        val nextTimes = mDBConnector!!.nextBudilnik()
        var outTime = "Сегодня нет"
        if (nextTimes != null) {
            outTime = nextTimes.niceStringTime
        }
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(
                    Color.Blue,
                ).clickable(
                    onClick = actionStartActivity<MainActivity>()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BudilnikWidgetCounter(
                outTime,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
            )
            ButtonRefresh()

        }
    }

    @Composable
    fun BudilnikWidgetCounter(
        time: String,
        modifier: GlanceModifier
    ) {
        var active by remember { mutableStateOf(true) }
        val tt = mDBConnector!!.nextBudilnik()
        if (tt != null) {
            active = tt.active
        }
        Text(
            text = if (active) "Следующий будильник: " + time else "Следующего будильника в " + time + " не будет",
            modifier = modifier,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = ColorProvider(
                    color = if (active) Color.White else Color.Red
                )
            ),
        )


    }

    @Composable
    fun ButtonRefresh(

    ) {
        Row {
            Image(
                provider = ImageProvider(
                    resId = R.drawable.refresh_svgrepo_com
                ),
                contentDescription = null,
                modifier = GlanceModifier
                    .clickable(
                        onClick = actionRunCallback<RefreshClickAction>()
                    ).size(40.dp)
            )
            Spacer(modifier = GlanceModifier.size(30.dp))
            Image(
                provider = ImageProvider(
                    resId = R.drawable.stop_svgrepo_com
                ),
                contentDescription = null,
                modifier = GlanceModifier
                    .clickable(
                        onClick = actionRunCallback<StopBudilnikClickAction>()
                    ).size(40.dp)
            )
        }
    }
}

class BudilnikWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = BudilnikWidget()
}
