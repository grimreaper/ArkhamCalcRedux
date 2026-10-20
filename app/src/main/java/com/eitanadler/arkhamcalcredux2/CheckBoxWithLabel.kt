package com.eitanadler.arkhamcalcredux2

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
internal fun CheckboxAndLabelWithHelp(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    goToHelpScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(
            text = label,
            modifier =
                Modifier.combinedClickable(
                    onClick = {},
                    onLongClickLabel = stringResource(R.string.go_to_help_screen_by_label, label),
                    onLongClick = {
                        goToHelpScreen(label)
                    },
                ),
        )
    }
}
