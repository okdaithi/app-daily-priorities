package com.dailypriorities

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

/** Screen where the user types in their three priorities for the day. */
class MainActivity : Activity() {

    private lateinit var fields: List<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fields = listOf(
            findViewById(R.id.priority1),
            findViewById(R.id.priority2),
            findViewById(R.id.priority3),
        )
        fields.forEachIndexed { i, field -> field.setText(PrioritiesStore.getText(this, i)) }

        fields.last().setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { save(); true } else false
        }

        findViewById<Button>(R.id.save).setOnClickListener { save() }

        findViewById<Button>(R.id.clear).setOnClickListener {
            fields.forEach { it.setText("") }
            save()
        }

        findViewById<Button>(R.id.reset_done).setOnClickListener {
            PrioritiesStore.clearDone(this)
            PrioritiesWidget.updateAll(this)
            Toast.makeText(this, R.string.reset_done_toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun save() {
        PrioritiesStore.save(this, fields.map { it.text.toString() })
        PrioritiesWidget.updateAll(this)
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
    }
}
