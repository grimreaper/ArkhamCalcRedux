package com.eitanadler.arkhamcalcredux2

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun AddOneTextAndLabelContent(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = label, modifier = modifier) // wrong place for it, but leave for now
    }
}
