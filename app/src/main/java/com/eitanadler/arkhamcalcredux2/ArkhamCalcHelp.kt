/*ArkhamCalc
Copyright (C) 2012  Matthew Cole

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.*/
package com.eitanadler.arkhamcalcredux2

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity


@Composable
public fun NewArkhamCalcHelp() {
    MaterialTheme {
        val helpTopics: Array<String> = stringArrayResource(R.array.topics)
        val helpContent: Array<String> = stringArrayResource(R.array.contents)
        Scaffold { innerPadding: PaddingValues ->
            LazyColumn(
                modifier = Modifier.consumeWindowInsets(innerPadding),
                contentPadding = innerPadding
            ) {
                helpTopics.forEachIndexed { index, topic ->
                    item {
                        var isExpanded by remember { mutableStateOf(true) }
                        ListItem(
                            leadingContent = {
                                if (isExpanded) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_drop_down_24px),
                                        contentDescription = stringResource(R.string.expanded)
                                    )

                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_drop_up_24px),
                                        contentDescription = stringResource(R.string.collapsed)
                                    )
                                }
                            },
                            headlineContent = { Text(topic) },
                            supportingContent = {
                                AnimatedVisibility(visible = isExpanded) {
                                    Text(helpContent[index])
                                }
                            },
                            modifier = Modifier.clickable { isExpanded = !isExpanded }
                        )
                    }
                }
            }
        }
    }
}

/**
 * An activity that displays help topics and content. Strings are stored in the
 * res folder. The nth entry in the 'topic' String array is associated with
 * the nth entry in the 'content' String array.
 */
public class ArkhamCalcHelp : FragmentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NewArkhamCalcHelp()
        }
    }

    internal companion object {
        val BUNDLE_TOPIC: String = "BUNDLE_TOPIC"
    }
}
