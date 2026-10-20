package com.eitanadler.arkhamcalcredux2

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.font.FontWeight
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

internal class TrueMainArkhamUX(
    private val dataStore: DataStore<Preferences>,
) {
    @OptIn(ExperimentalMaterial3Api::class)
    private lateinit var chancesSliderState: SliderState

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ComposableTrueMainArkhamUX(
        goToHelpScreen: (String) -> Unit,
        modifier: Modifier = Modifier,
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
        val difficulitySliderState =
            rememberSliderState(
                value = 1.0f,
                steps = TOUGH_MAX - 2,
                valueRange = 1f..TOUGH_MAX.toFloat(),
            )
        chancesSliderState =
            rememberSliderState(
                value = 1.0f,
                steps = CHANCE_MAX - 2,
                valueRange = 1f..CHANCE_MAX.toFloat(),
            )

        fun simpleInternalRecalculate() {
            recalculate(
                calculatorResultText,
                calculatorResultColor,
                diceCountSliderValue = diceCountSliderState.value.toInt(),
                difficulitySliderValue = difficulitySliderState.value.toInt(),
                chancesSliderValue = chancesSliderState.value.toInt(),
                isBlessed = isBlessedChecked,
                isCursed = isCursedChecked,
                isMandyChecked = isMandyChecked,
                isSkidsChecked = isSkidsChecked,
                isRerollOnesChecked = isRerollOnesChecked,
                isAddOneChecked = isAddOneChecked,
                isShotgunChecked = isShotgunChecked,
            )
        }

        diceCountSliderState.onValueChange = { v: Float ->
            diceCountSliderState.value = v
            simpleInternalRecalculate()
        }

        difficulitySliderState.onValueChange = { v: Float ->
            difficulitySliderState.value = v
            simpleInternalRecalculate()
        }

        val context = LocalContext.current
        val resources = LocalResources.current
        chancesSliderState.onValueChange = { v: Float ->
            val mPreviousChanceValue = diceCountSliderState.value.toInt()
            chancesSliderState.value = v
            simpleInternalRecalculate()
            handleOneTimeAbilityChancesChanged(
                baseContext = context,
                resources = resources,
                previousChanceValue = mPreviousChanceValue,
                isMandyChecked,
                R.string.mandy_chances_toast,
            )
            handleOneTimeAbilityChancesChanged(
                baseContext = context,
                resources = resources,
                previousChanceValue = mPreviousChanceValue,
                isRerollOnesChecked,
                R.string.reroll_ones_chances_toast,
            )
            handleOneTimeAbilityChancesChanged(
                baseContext = context,
                resources = resources,
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

            handleShowFirstTimeDialog(LocalContext.current)
            HorizontalDivider()
            Row {
                Checkbox(
                    checked = isBlessedChecked,
                    onCheckedChange = { newValue ->
                        isBlessedChecked = newValue
                        if (newValue && isCursedChecked) {
                            isCursedChecked = false
                        }

                        simpleInternalRecalculate()
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
                        simpleInternalRecalculate()
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
                val context = LocalContext.current
                val resources = LocalResources.current
                CheckboxAndLabelWithHelp(
                    checked = isMandyChecked,
                    onCheckedChange = { newValue ->
                        isMandyChecked = newValue
                        if (isMandyChecked) {
                            // both Mandy and Reroll ones on at same time not supported
                            isRerollOnesChecked = false
                            isSkidsChecked = false
                        }
                        simpleInternalRecalculate()
                        handleOneTimeAbilityOptionChanged(
                            baseContext = context,
                            resources = resources,
                            isAbilityChecked = isMandyChecked,
                            resourceStringId = R.string.mandy_chances_toast,
                        )
                    },
                    label = "Mandy",
                    goToHelpScreen = goToHelpScreen,
                )
            }
            Row {
                val context = LocalContext.current
                val resources = LocalResources.current
                CheckboxAndLabelWithHelp(
                    checked = isSkidsChecked,
                    onCheckedChange = { newValue ->
                        isSkidsChecked = newValue
                        if (isSkidsChecked) {
                            // both Mandy and Reroll ones on at same time not supported
                            isMandyChecked = false
                            isRerollOnesChecked = false
                        }
                        simpleInternalRecalculate()
                        handleOneTimeAbilityOptionChanged(
                            baseContext = context,
                            resources = resources,
                            isAbilityChecked = isSkidsChecked,
                            R.string.skids_chances_toast,
                        )
                    },
                    goToHelpScreen = goToHelpScreen,
                    label = "Skids",
                )
            }
            Row {
                CheckboxAndLabelWithHelp(
                    checked = isRerollOnesChecked,
                    onCheckedChange = { newValue ->
                        isRerollOnesChecked = newValue
                        if (isRerollOnesChecked) {
                            // both Mandy and Reroll ones on at same time not supported
                            isMandyChecked = false
                            isSkidsChecked = false
                        }
                        simpleInternalRecalculate()
                    },
                    goToHelpScreen = goToHelpScreen,
                    label = "Reroll Ones",
                )
            }
            HorizontalDivider()
            Row {
                CheckboxAndLabelWithHelp(
                    checked = isAddOneChecked,
                    onCheckedChange = { newValue ->
                        isAddOneChecked = newValue
                        simpleInternalRecalculate()
                    },
                    goToHelpScreen = goToHelpScreen,
                    label = "Add One",
                )
            }
            Row {
                CheckboxAndLabelWithHelp(
                    checked = isShotgunChecked,
                    onCheckedChange = { newValue ->
                        isShotgunChecked = newValue
                        simpleInternalRecalculate()
                    },
                    goToHelpScreen = goToHelpScreen,
                    label = "Shotgun",
                )
            }
        }
        simpleInternalRecalculate()
    }

    /**
     * Potentially show a dialog depending on if the user has ever opened
     * this version of the app; otherwise do nothing.
     */
    private fun handleShowFirstTimeDialog(context: Context) {
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
                .Builder(context)
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

    private fun recalculate(
        calculatorResultText: MutableState<String>,
        calculatorResultColor: MutableState<Color>,
        diceCountSliderValue: Int,
        chancesSliderValue: Int,
        difficulitySliderValue: Int,
        isBlessed: Boolean,
        isCursed: Boolean,
        isMandyChecked: Boolean,
        isSkidsChecked: Boolean,
        isRerollOnesChecked: Boolean,
        isAddOneChecked: Boolean,
        isShotgunChecked: Boolean,
    ) {
        val dice: Int = diceCountSliderValue
        val tough = difficulitySliderValue
        val chance = chancesSliderValue

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
        baseContext: Context,
        resources: Resources,
        isAbilityChecked: Boolean,
        resourceStringId: Int,
    ) {
        if (isAbilityChecked && chancesSliderState.value.toInt() > 0) {
            showToast(baseContext, resources.getString((resourceStringId)))
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
    private fun handleOneTimeAbilityChancesChanged(
        baseContext: Context,
        resources: Resources,
        previousChanceValue: Int,
        isAbilityChecked: Boolean,
        resourceStringId: Int,
    ) {
        if (isAbilityChecked && previousChanceValue <= 1 && chancesSliderState.value > 1f) {
            showToast(baseContext, resources.getString(resourceStringId))
        }
    }

    private fun showToast(
        baseContext: Context,
        toastText: String,
    ) {
        Toast.makeText(baseContext, toastText, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val PREFS_KEY_FIRST_TIME_16 = "FirstTime16"

        private const val DICE_MAX = 16
        private const val TOUGH_MAX = 6
        private const val CHANCE_MAX = 6
    }
}
