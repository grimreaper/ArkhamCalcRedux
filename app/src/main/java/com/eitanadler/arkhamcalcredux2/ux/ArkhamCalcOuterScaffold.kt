package com.eitanadler.arkhamcalcredux2.ux

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
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.eitanadler.arkhamcalcredux2.ArkhamCalcHelpTab
import com.eitanadler.arkhamcalcredux2.ArkhamDropdown
import com.eitanadler.arkhamcalcredux2.ArkhamNavigationKey
import com.eitanadler.arkhamcalcredux2.CalculatorUXScreen
import com.eitanadler.arkhamcalcredux2.HelpScreen
import com.eitanadler.arkhamcalcredux2.LibrariesScreen
import com.eitanadler.arkhamcalcredux2.LibrariesTab
import com.eitanadler.arkhamcalcredux2.MainArkhamTab
import com.eitanadler.arkhamcalcredux2.R
import com.eitanadler.arkhamcalcredux2.ui.theme.ArkhamCalcReduxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArkhamCalcOuterScaffold(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<ArkhamNavigationKey>(CalculatorUXScreen) }
    ArkhamCalcReduxTheme {
        Surface(modifier = modifier.fillMaxSize()) {
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
                                    if (backStack.last() !is LibrariesScreen) {
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
                                    MainArkhamTab(
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
