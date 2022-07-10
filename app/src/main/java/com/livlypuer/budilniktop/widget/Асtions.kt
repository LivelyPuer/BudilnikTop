package com.livlypuer.budilniktop.widget

import android.content.Context
import android.content.Intent
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat.startActivity
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.livlypuer.budilniktop.CreateBudilnicActivity
import com.livlypuer.budilniktop.bdKotlin.DBManager
import com.livlypuer.budilniktop.ui.theme.BudilnikTopTheme

class RefreshClickAction : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            it.toMutablePreferences()
                .apply {
                }
        }
        BudilnikWidget().update(context, glanceId)
    }
}

class StopBudilnikClickAction : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            it.toMutablePreferences()
                .apply {
                }
        }
        var mDBConnector = DBManager(context)
        var nextTime = mDBConnector.nextBudilnik()
        if (nextTime != null) {
            nextTime.active = false
            mDBConnector.updateTime(nextTime)
        }

        BudilnikWidget().update(context, glanceId)
    }
}