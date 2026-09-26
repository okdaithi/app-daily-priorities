package com.dailypriorities

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.widget.RemoteViews

/** Home-screen widget showing today's three priorities. Tap an item to mark it done. */
class PrioritiesWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { manager.updateAppWidget(it, buildViews(context)) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE) {
            val index = intent.getIntExtra(EXTRA_INDEX, -1)
            if (index in 0 until PrioritiesStore.COUNT && PrioritiesStore.getText(context, index).isNotEmpty()) {
                PrioritiesStore.toggleDone(context, index)
                updateAll(context)
            }
        }
    }

    companion object {
        private const val ACTION_TOGGLE = "com.dailypriorities.TOGGLE"
        private const val EXTRA_INDEX = "index"

        private val ROW_IDS = intArrayOf(R.id.item1, R.id.item2, R.id.item3)

        /** Redraws every placed instance of the widget. Call after priorities change. */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, PrioritiesWidget::class.java))
            if (ids.isNotEmpty()) {
                val views = buildViews(context)
                ids.forEach { manager.updateAppWidget(it, views) }
            }
        }

        private fun buildViews(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_priorities)

            val openApp = PendingIntent.getActivity(
                context, 100,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.header, openApp)

            val anySet = (0 until PrioritiesStore.COUNT).any { PrioritiesStore.getText(context, it).isNotEmpty() }
            val normalColor = context.getColor(R.color.widget_text)
            val doneColor = context.getColor(R.color.widget_text_done)

            ROW_IDS.forEachIndexed { i, rowId ->
                val text = PrioritiesStore.getText(context, i)
                val done = PrioritiesStore.isDone(context, i)

                val label: CharSequence = when {
                    text.isEmpty() && i == 0 && !anySet -> context.getString(R.string.widget_empty)
                    text.isEmpty() -> "—"
                    done -> SpannableString("${i + 1}.  $text").apply {
                        setSpan(StrikethroughSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    else -> "${i + 1}.  $text"
                }
                views.setTextViewText(rowId, label)
                views.setTextColor(rowId, if (done || text.isEmpty()) doneColor else normalColor)

                val click = if (text.isEmpty()) {
                    openApp
                } else {
                    PendingIntent.getBroadcast(
                        context, i,
                        Intent(context, PrioritiesWidget::class.java)
                            .setAction(ACTION_TOGGLE)
                            .putExtra(EXTRA_INDEX, i),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
                views.setOnClickPendingIntent(rowId, click)
            }
            return views
        }
    }
}
