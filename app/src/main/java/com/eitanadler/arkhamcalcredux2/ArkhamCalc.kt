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

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.FragmentActivity
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.eitanadler.arkhamcalcredux2.ui.theme.ArkhamCalcReduxTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * The main activity. Routes user input to Calculator and prints its results.
 */
public class ArkhamCalc : FragmentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    private lateinit var chancesSliderState: SliderState

    private var mRestoringState = false

    private val Context.dataStore by preferencesDataStore(name = "settings")


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ArkahmCalcOuterScaffold()
        }
    }

    @Composable
    public fun ArkahmCalcOuterScaffold() {
        val backStack = remember { mutableStateListOf<ArkhamNavigation>(CalculatorUX) }
        ArkhamCalcReduxTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                ) { paddingValues ->
                    NavDisplay(
                        modifier = Modifier.padding(paddingValues),
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = { key ->
                            when (key) {
                                is CalculatorUX -> NavEntry(key) {
                                    TrueMainArkhamUX(
                                        goToHelpScreen = { topic ->
                                            backStack.add(HelpScreen(topic))
                                        }
                                    )
                                }

                                is HelpScreen ->
                                    NavEntry(key) {
                                        NewArkhamCalcHelp()
                                    }
                            }
                        }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    public fun TrueMainArkhamUX(
        goToHelpScreen: (String) -> Unit
    ) {
        val calculatorResultText = rememberSaveable { mutableStateOf("Result!") }
        val calculatorResultColor = remember { mutableStateOf(Color.Blue) }

        var isBlessedChecked by remember { mutableStateOf(false) }
        var isCursedChecked by remember { mutableStateOf(false) }
        var isMandyChecked by remember { mutableStateOf(false) }
        var isSkidsChecked by remember { mutableStateOf(false) }
        var isRerollOnesChecked by remember { mutableStateOf(false) }
        var isAddOneChecked by remember { mutableStateOf(false) }
        var isShotgunChecked by remember { mutableStateOf(false) }

        val diceCountSliderState =
            rememberSliderState(
                value = 1.0f,
                steps = DICE_MAX - 2,
                valueRange = 1f..DICE_MAX.toFloat(),
            )
        val difficulitySliderState = rememberSliderState(
            value = 1.0f,
            steps = TOUGH_MAX - 2,
            valueRange = 1f..TOUGH_MAX.toFloat(),
        )
        chancesSliderState = rememberSliderState(
            value = 1.0f,
            steps = CHANCE_MAX - 2,
            valueRange = 1f..CHANCE_MAX.toFloat(),
        )
        diceCountSliderState.onValueChange = { v: Float ->
            diceCountSliderState.value = v
            recalculate(
                calculatorResultText,
                calculatorResultColor,
                diceCountSliderState = diceCountSliderState,
                difficulitySliderState = difficulitySliderState,
                chancesSliderState = chancesSliderState,
                isBlessed = isBlessedChecked,
                isCursed = isCursedChecked,
                isMandyChecked = isMandyChecked,
                isSkidsChecked = isSkidsChecked,
                isRerollOnesChecked = isRerollOnesChecked,
                isAddOneChecked = isAddOneChecked,
                isShotgunChecked = isShotgunChecked,
            )
            recalculate(
                calculatorResultText,
                calculatorResultColor,
                diceCountSliderState = diceCountSliderState,
                difficulitySliderState = difficulitySliderState,
                chancesSliderState = chancesSliderState,
                isBlessed = isBlessedChecked,
                isCursed = isCursedChecked,
                isMandyChecked = isMandyChecked,
                isSkidsChecked = isSkidsChecked,
                isRerollOnesChecked = isRerollOnesChecked,
                isAddOneChecked = isAddOneChecked,
                isShotgunChecked = isShotgunChecked,
            )
        }

        difficulitySliderState.onValueChange = { v: Float ->
            difficulitySliderState.value = v
            recalculate(
                calculatorResultText,
                calculatorResultColor,
                diceCountSliderState = diceCountSliderState,
                difficulitySliderState = difficulitySliderState,
                chancesSliderState = chancesSliderState,
                isBlessed = isBlessedChecked,
                isCursed = isCursedChecked,
                isMandyChecked = isMandyChecked,
                isSkidsChecked = isSkidsChecked,
                isRerollOnesChecked = isRerollOnesChecked,
                isAddOneChecked = isAddOneChecked,
                isShotgunChecked = isShotgunChecked,
            )
        }

        chancesSliderState.onValueChange = { v: Float ->
            val mPreviousChanceValue = diceCountSliderState.value.toInt()
            chancesSliderState.value = v
            recalculate(
                calculatorResultText,
                calculatorResultColor,
                diceCountSliderState = diceCountSliderState,
                difficulitySliderState = difficulitySliderState,
                chancesSliderState = chancesSliderState,
                isBlessed = isBlessedChecked,
                isCursed = isCursedChecked,
                isMandyChecked = isMandyChecked,
                isSkidsChecked = isSkidsChecked,
                isRerollOnesChecked = isRerollOnesChecked,
                isAddOneChecked = isAddOneChecked,
                isShotgunChecked = isShotgunChecked,
            )
            handleOneTimeAbilityChancesChanged(
                previousChanceValue = mPreviousChanceValue,
                isMandyChecked,
                R.string.mandy_chances_toast,
            )
            handleOneTimeAbilityChancesChanged(
                previousChanceValue = mPreviousChanceValue,
                isRerollOnesChecked,
                R.string.reroll_ones_chances_toast,
            )
            handleOneTimeAbilityChancesChanged(
                previousChanceValue = mPreviousChanceValue,
                isSkidsChecked,
                R.string.skids_chances_toast,
            )
        }

        Column {
            Text("Result", fontWeight = FontWeight.Bold)
            Text(calculatorResultText.value, color = calculatorResultColor.value)

            Column {
                Row {
                    Text(
                        "Dice: ",
                        fontWeight = FontWeight.Bold,
                        modifier =
                            Modifier.combinedClickable(
                                onClick = {},
                                onLongClickLabel = "Show Help For Dice / Difficulty",
                                onLongClick = {
                                    goToHelpScreen("Dice / Difficulty")
                                },
                            ),
                    )
                    Text(
                        diceCountSliderState.value.toInt().toString(),
                    )
                }
                Slider(
                    state = diceCountSliderState,
                )
            }
            Column {
                Row {
                    Text(
                        "Difficulty: ",
                        fontWeight = FontWeight.Bold,
                        modifier =
                            Modifier.combinedClickable(
                                onClick = {},
                                onLongClickLabel = "Show Help For Dice / Difficulty",
                                onLongClick = {
                                    goToHelpScreen("Dice / Difficulty")
                                },
                            ),
                    )
                    Text(
                        difficulitySliderState.value.toInt().toString(),
                    )
                }
                Slider(
                    state = difficulitySliderState,
                )
            }

            Column {
                Row {
                    Text(
                        "Chances: ",
                        fontWeight = FontWeight.Bold,
                        modifier =
                            Modifier.combinedClickable(
                                onClick = {},
                                onLongClickLabel = "Show Help For Chances",
                                onLongClick = {
                                    goToHelpScreen("Chances")
                                },
                            ),
                    )
                    Text(
                        chancesSliderState.value.toInt().toString(),
                    )
                }
                Slider(
                    state = chancesSliderState,
                )
            }

            handleShowFirstTimeDialog()
            HorizontalDivider()
            Row {
                Checkbox(
                    checked = isBlessedChecked,
                    onCheckedChange = { newValue ->
                        isBlessedChecked = newValue
                        if (newValue && isCursedChecked) {
                            isCursedChecked = false
                        }

                        recalculate(
                            calculatorResultText,
                            calculatorResultColor,
                            diceCountSliderState = diceCountSliderState,
                            difficulitySliderState = difficulitySliderState,
                            chancesSliderState = chancesSliderState,
                            isBlessed = isBlessedChecked,
                            isCursed = isCursedChecked,
                            isMandyChecked = isMandyChecked,
                            isSkidsChecked = isSkidsChecked,
                            isRerollOnesChecked = isRerollOnesChecked,
                            isAddOneChecked = isAddOneChecked,
                            isShotgunChecked = isShotgunChecked,
                        )
                    },
                )
                Text(
                    "Blessed",
                    modifier =
                        Modifier
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    goToHelpScreen("Blessed / Cursed")
                                },
                            ),
                )
            }
            Row {
                Checkbox(
                    checked = isCursedChecked,
                    onCheckedChange = { newValue ->
                        isCursedChecked = newValue
                        if (newValue && isBlessedChecked) {
                            isBlessedChecked = false
                        }
                        recalculate(
                            calculatorResultText,
                            calculatorResultColor,
                            diceCountSliderState = diceCountSliderState,
                            difficulitySliderState = difficulitySliderState,
                            chancesSliderState = chancesSliderState,
                            isBlessed = isBlessedChecked,
                            isCursed = isCursedChecked,
                            isMandyChecked = isMandyChecked,
                            isSkidsChecked = isSkidsChecked,
                            isRerollOnesChecked = isRerollOnesChecked,
                            isAddOneChecked = isAddOneChecked,
                            isShotgunChecked = isShotgunChecked,
                        )
                    },
                )
                Text(
                    "Cursed",
                    modifier =
                        Modifier
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    goToHelpScreen("Blessed / Cursed")
                                },
                            ),
                )
            }
            HorizontalDivider()
            Row {
                AddOneTextAndLabelContent(
                    checked = isMandyChecked,
                    onCheckedChange = { newValue ->
                        isMandyChecked = newValue
                        if (isMandyChecked) {
                            // both Mandy and Reroll ones on at same time not supported
                            isRerollOnesChecked = false
                            isSkidsChecked = false
                        }
                        recalculate(
                            calculatorResultText,
                            calculatorResultColor,
                            diceCountSliderState = diceCountSliderState,
                            difficulitySliderState = difficulitySliderState,
                            chancesSliderState = chancesSliderState,
                            isBlessed = isBlessedChecked,
                            isCursed = isCursedChecked,
                            isMandyChecked = isMandyChecked,
                            isSkidsChecked = isSkidsChecked,
                            isRerollOnesChecked = isRerollOnesChecked,
                            isAddOneChecked = isAddOneChecked,
                            isShotgunChecked = isShotgunChecked,
                        )
                        handleOneTimeAbilityOptionChanged(
                            isMandyChecked,
                            R.string.mandy_chances_toast,
                        )
                    },
                    label = "Mandy",
                    modifier =
                        Modifier
                            .combinedClickable(
                                onClick = {
                                },
                                onLongClick = {
                                    goToHelpScreen("Mandy")
                                },
                            ),
                )
            }
            Row {
                AddOneTextAndLabelContent(
                    checked = isSkidsChecked,
                    onCheckedChange = { newValue ->
                        isSkidsChecked = newValue
                        if (isSkidsChecked) {
                            // both Mandy and Reroll ones on at same time not supported
                            isMandyChecked = false
                            isRerollOnesChecked = false
                        }
                        recalculate(
                            calculatorResultText,
                            calculatorResultColor,
                            diceCountSliderState = diceCountSliderState,
                            difficulitySliderState = difficulitySliderState,
                            chancesSliderState = chancesSliderState,
                            isBlessed = isBlessedChecked,
                            isCursed = isCursedChecked,
                            isMandyChecked = isMandyChecked,
                            isSkidsChecked = isSkidsChecked,
                            isRerollOnesChecked = isRerollOnesChecked,
                            isAddOneChecked = isAddOneChecked,
                            isShotgunChecked = isShotgunChecked,
                        )
                        handleOneTimeAbilityOptionChanged(
                            isSkidsChecked,
                            R.string.skids_chances_toast,
                        )
                    },
                    label = "Skids",
                    modifier =
                        Modifier
                            .combinedClickable(
                                onClick = {
                                },
                                onLongClick = {
                                    goToHelpScreen("Skids")
                                },
                            ),
                )
            }
            Row {
                AddOneTextAndLabelContent(
                    checked = isRerollOnesChecked,
                    onCheckedChange = { newValue ->
                        isRerollOnesChecked = newValue
                        if (isRerollOnesChecked) {
                            // both Mandy and Reroll ones on at same time not supported
                            isMandyChecked = false
                            isSkidsChecked = false
                        }
                        recalculate(
                            calculatorResultText,
                            calculatorResultColor,
                            diceCountSliderState = diceCountSliderState,
                            difficulitySliderState = difficulitySliderState,
                            chancesSliderState = chancesSliderState,
                            isBlessed = isBlessedChecked,
                            isCursed = isCursedChecked,
                            isMandyChecked = isMandyChecked,
                            isSkidsChecked = isSkidsChecked,
                            isRerollOnesChecked = isRerollOnesChecked,
                            isAddOneChecked = isAddOneChecked,
                            isShotgunChecked = isShotgunChecked,
                        )
                    },
                    label = "Reroll Ones",
                    modifier =
                        Modifier
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    goToHelpScreen("Reroll Ones")
                                },
                            ),
                )
            }
            HorizontalDivider()
            Row {
                AddOneTextAndLabelContent(
                    checked = isAddOneChecked,
                    onCheckedChange = { newValue ->
                        isAddOneChecked = newValue
                        recalculate(
                            calculatorResultText,
                            calculatorResultColor,
                            diceCountSliderState = diceCountSliderState,
                            difficulitySliderState = difficulitySliderState,
                            chancesSliderState = chancesSliderState,
                            isBlessed = isBlessedChecked,
                            isCursed = isCursedChecked,
                            isMandyChecked = isMandyChecked,
                            isSkidsChecked = isSkidsChecked,
                            isRerollOnesChecked = isRerollOnesChecked,
                            isAddOneChecked = isAddOneChecked,
                            isShotgunChecked = isShotgunChecked,
                        )
                    },
                    label = "Add One",
                    modifier =
                        Modifier
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    goToHelpScreen("Add One")
                                },
                            ),
                )
            }
            Row {
                AddOneTextAndLabelContent(
                    checked = isShotgunChecked,
                    onCheckedChange = { newValue ->
                        isShotgunChecked = newValue
                        recalculate(
                            calculatorResultText,
                            calculatorResultColor,
                            diceCountSliderState = diceCountSliderState,
                            difficulitySliderState = difficulitySliderState,
                            chancesSliderState = chancesSliderState,
                            isBlessed = isBlessedChecked,
                            isCursed = isCursedChecked,
                            isMandyChecked = isMandyChecked,
                            isSkidsChecked = isSkidsChecked,
                            isRerollOnesChecked = isRerollOnesChecked,
                            isAddOneChecked = isAddOneChecked,
                            isShotgunChecked = isShotgunChecked,
                        )
                    },
                    label = "Shotgun",
                    modifier =
                        Modifier
                            .combinedClickable(
                                onClick = {
                                    recalculate(
                                        calculatorResultText,
                                        calculatorResultColor,
                                        diceCountSliderState = diceCountSliderState,
                                        difficulitySliderState = difficulitySliderState,
                                        chancesSliderState = chancesSliderState,
                                        isBlessed = isBlessedChecked,
                                        isCursed = isCursedChecked,
                                        isMandyChecked = isMandyChecked,
                                        isSkidsChecked = isSkidsChecked,
                                        isRerollOnesChecked = isRerollOnesChecked,
                                        isAddOneChecked = isAddOneChecked,
                                        isShotgunChecked = isShotgunChecked,
                                    )
                                },
                                onLongClick = {
                                    goToHelpScreen("Shotgun")
                                },
                            ),
                )
            }
        }
        recalculate(
            calculatorResultText,
            calculatorResultColor,
            diceCountSliderState = diceCountSliderState,
            difficulitySliderState = difficulitySliderState,
            chancesSliderState = chancesSliderState,
            isBlessed = isBlessedChecked,
            isCursed = isCursedChecked,
            isMandyChecked = isMandyChecked,
            isSkidsChecked = isSkidsChecked,
            isRerollOnesChecked = isRerollOnesChecked,
            isAddOneChecked = isAddOneChecked,
            isShotgunChecked = isShotgunChecked,
        )
    }

    override fun onRestoreInstanceState(inState: Bundle) {
        mRestoringState = true
        try {
            super.onRestoreInstanceState(inState)
        } finally {
            mRestoringState = false
        }
    }

    /**
     * Potentially show a dialog depending on if the user has ever opened
     * this version of the app; otherwise do nothing.
     */
    private fun handleShowFirstTimeDialog() {
        val prefKey = stringPreferencesKey(PREFS_KEY_FIRST_TIME_16)
        val prefResultFlow: Flow<String?> =
            dataStore.data.map { preferences ->
                preferences[prefKey]
            }
        val prefResult: String? =
            runBlocking {
                prefResultFlow.first()
            }
        if (prefResult != null) return

        val builder =
            AlertDialog
                .Builder(this)
                .setMessage(R.string.first_dialog_message)
                .setNeutralButton(
                    R.string.first_dialog_button,
                ) { dialog: DialogInterface, which: Int ->
                    dialog.cancel()
                    runBlocking {
                        dataStore.edit { settings ->
                            settings[prefKey] = "a"
                        }
                    }
                }
        builder.create().show()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    internal fun recalculate(
        calculatorResultText: MutableState<String>,
        calculatorResultColor: MutableState<Color>,
        diceCountSliderState: SliderState,
        chancesSliderState: SliderState,
        isBlessed: Boolean,
        isCursed: Boolean,
        isMandyChecked: Boolean,
        isSkidsChecked: Boolean,
        isRerollOnesChecked: Boolean,
        isAddOneChecked: Boolean,
        isShotgunChecked: Boolean,
        difficulitySliderState: SliderState,
    ) {
        val dice: Int = diceCountSliderState.value.toInt()
        val tough = difficulitySliderState.value.toInt()
        val chance = chancesSliderState.value.toInt()

        val calculator =
            Calculator(
                mDice = dice,
                mTough = tough,
                mNumberOfChances = chance,
                mIsBlessed = isBlessed,
                mIsCursed = isCursed,
                mIsShotgun = isShotgunChecked,
                mIsMandy = isMandyChecked,
                mIsRerollOnes = isRerollOnesChecked,
                mIsSkids = isSkidsChecked,
                mIsAddOne = isAddOneChecked,
            )
        val result = calculator.calculate()

        val formatter = CalculateResultFormatter(result)

        calculatorResultText.value = formatter.resultString
        calculatorResultColor.value = formatter.color
    }

    /**
     * Show a message to the user regarding this one-time ability if user has more than one chance.
     * Case where ability has been changed; check number of chances.
     * Do not show a message if we are in the process of restoring state (because we already showed
     * the message before state was saved).
     */
    @OptIn(ExperimentalMaterial3Api::class)
    private fun handleOneTimeAbilityOptionChanged(
        isAbilityChecked: Boolean,
        resourceStringId: Int,
    ) {
        if (isAbilityChecked && chancesSliderState.value.toInt() > 0 && !mRestoringState) {
            showToast(resources.getString((resourceStringId)))
        }
    }

    /**
     * Show a message to the user regarding this one-time ability if user has more than one chance.
     * Case where number of chances have changed; check if ability is selected and chances have changed
     * from one to > 1.
     * Do not show a message if we are in the process of restoring state (because we already showed
     * the message before state was saved).
     */
    @OptIn(ExperimentalMaterial3Api::class)
    internal fun handleOneTimeAbilityChancesChanged(
        previousChanceValue: Int,
        isAbilityChecked: Boolean,
        resourceStringId: Int,
    ) {
        if (isAbilityChecked && previousChanceValue <= 1 && chancesSliderState.value > 1f && !mRestoringState) {
            showToast(resources.getString(resourceStringId))
        }
    }

    private fun showToast(toastText: String) {
        Toast.makeText(baseContext, toastText, Toast.LENGTH_LONG).show()
    }

    public companion object {
        private const val PREFS_KEY_FIRST_TIME_16 = "FirstTime16"

        private const val DICE_MAX = 16
        private const val TOUGH_MAX = 6
        private const val CHANCE_MAX = 6
    }
}
