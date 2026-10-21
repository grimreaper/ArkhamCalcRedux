/*
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

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.eitanadler.arkhamcalcredux2.ui.theme.ArkhamCalcReduxTheme

/**
 * The main activity. Routes user input to Calculator and prints its results.
 */
public class ArkhamCalc : ComponentActivity() {
    private val Context.dataStore by preferencesDataStore(name = "settings")

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ArkahmCalcOuterScaffold()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    internal fun ArkahmCalcOuterScaffold(modifier: Modifier = Modifier) {
        val backStack = remember { mutableStateListOf<ArkhamNavigationKey>(CalculatorUXScreen) }
        ArkhamCalcReduxTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors =
                                TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                            title = { Text(stringResource(R.string.arkham_horror_calculator)) },
                            scrollBehavior =
                                TopAppBarDefaults
                                    .exitUntilCollapsedScrollBehavior(
                                        rememberTopAppBarState(),
                                    ),
                            actions = {
                                ArkhamDropdown(
                                    goToHelpMenu = {
                                        if (backStack.last() !is HelpScreen) {
                                            backStack.add(HelpScreen())
                                        }
                                    },
                                    goToAboutScreen = {
                                        if (backStack.last() !is HelpScreen) {
                                            backStack.add(LibrariesScreen)
                                        }
                                    },
                                )
                            },
                        )
                    },
                ) { paddingValues ->
                    NavDisplay(
                        modifier = Modifier.padding(paddingValues),
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = { key ->
                            when (key) {
                                is CalculatorUXScreen ->
                                    NavEntry(key) {
                                        TrueMainArkhamUX(
                                            dataStore,
                                        ).ComposableTrueMainArkhamTab(
                                            goToHelpScreen = { topic ->
                                                backStack.add(HelpScreen(topic))
                                            },
                                        )
                                    }

                                is HelpScreen ->
                                    NavEntry(key) {
                                        ArkhamCalcHelpTab(specificTopic = key.topic)
                                    }

                                LibrariesScreen ->
                                    NavEntry(key) {
                                        LibrariesTab()
                                    }
                            }
                        },
                    )
                }
            }
        }
    }
}
