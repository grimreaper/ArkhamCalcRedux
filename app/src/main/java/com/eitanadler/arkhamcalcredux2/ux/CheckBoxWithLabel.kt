package com.eitanadler.arkhamcalcredux2.ux

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.eitanadler.arkhamcalcredux2.R

@Composable
internal fun CheckboxAndLabelWithHelp(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    label: String,
    goToHelpScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = enabled,
                role = Role.Checkbox,
                onClick = {
                    onCheckedChange(!checked)
                },
                onLongClickLabel = stringResource(R.string.go_to_help_screen_by_label, label),
                onLongClick = {
                    goToHelpScreen(label)
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            enabled = enabled,
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Text(text = label)
    }
}
