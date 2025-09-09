package com.eitanadler.arkhamcalcredux2

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.AbstractComposeView
import kotlinx.coroutines.flow.MutableStateFlow

public class AddOneTextAndLabelButton(context: Context, attrs: AttributeSet) :
    AbstractComposeView(context, attrs) {

    private val _checked = MutableStateFlow(false)

    private var abcNewEvent: (() -> Unit)? = null

    public fun isChecked(): Boolean {
        return _checked.value
    }

    public fun setOnChangedEvent(event: () -> Unit) {
        abcNewEvent = event
    }


    @Composable
    override fun Content() {
        val checked = _checked.collectAsState()
        AddOneTextAndLabelContent(checked = checked.value, onCheckedChange = {
            _checked.value = it
            abcNewEvent?.invoke()
        })
    }
}

@Composable
internal fun AddOneTextAndLabelContent(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
) {

    MaterialTheme {
        Row {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Text(text = "Add Ones")

        }
    }
}