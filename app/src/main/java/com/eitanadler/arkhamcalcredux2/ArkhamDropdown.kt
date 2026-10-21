package com.eitanadler.arkhamcalcredux2

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
internal fun ArkhamDropdown(
    goToHelpMenu: () -> Unit,
    goToAboutScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier,
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                painter = painterResource(R.drawable.more_vert_24px),
                contentDescription = stringResource(R.string.help_menu),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.help_24px),
                        contentDescription = "Help Icon",
                    )
                },
                text = { Text(stringResource(R.string.menu_item_string_help)) },
                onClick = {
                    goToHelpMenu()
                    expanded = false
                },
            )
            DropdownMenuItem(
                text = { Text("Open Source Libraries") },
                onClick = {
                    goToAboutScreen()
                    expanded = false
                },
            )
        }
    }
}
