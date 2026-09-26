package com.dailypriorities

import android.content.Context

/** Persists the three daily priorities and their "done" state in SharedPreferences. */
object PrioritiesStore {
    const val COUNT = 3
    private const val PREFS = "priorities"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getText(context: Context, index: Int): String =
        prefs(context).getString("text_$index", "") ?: ""

    fun isDone(context: Context, index: Int): Boolean =
        prefs(context).getBoolean("done_$index", false)

    /** Saves all priorities. Items whose text changed are marked not done. */
    fun save(context: Context, texts: List<String>) {
        val editor = prefs(context).edit()
        texts.forEachIndexed { i, raw ->
            val text = raw.trim()
            if (text != getText(context, i)) editor.putBoolean("done_$i", false)
            editor.putString("text_$i", text)
        }
        editor.apply()
    }

    fun toggleDone(context: Context, index: Int) {
        prefs(context).edit().putBoolean("done_$index", !isDone(context, index)).apply()
    }

    fun clearDone(context: Context) {
        val editor = prefs(context).edit()
        for (i in 0 until COUNT) editor.putBoolean("done_$i", false)
        editor.apply()
    }
}
