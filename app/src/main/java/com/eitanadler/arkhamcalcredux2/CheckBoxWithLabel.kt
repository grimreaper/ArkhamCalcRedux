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

public class CheckBoxWithLabel(context: Context, attrs: AttributeSet) :
    AbstractComposeView(context, attrs) {

    private var label: String = ""


    private val _checked = MutableStateFlow(false)

    private var abcNewEvent: (() -> Unit)? = null

    public fun isChecked(): Boolean {
        return _checked.value
    }

    public fun setChecked(new: Boolean) {
        _checked.value = new
    }

    public fun setOnChangedEvent(event: () -> Unit) {
        abcNewEvent = event
    }

    @Composable
    override fun Content() {
        val checked = _checked.collectAsState()
        AddOneTextAndLabelContent(
            checked = checked.value, onCheckedChange = {
                _checked.value = it
                abcNewEvent?.invoke()
            },
            label = label
        )
    }

    internal fun setLabel(newLabel: String) {
        label = newLabel
    }

}

@Composable
internal fun AddOneTextAndLabelContent(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
) {

    MaterialTheme {
        Row {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Text(text = label)

        }
    }
}