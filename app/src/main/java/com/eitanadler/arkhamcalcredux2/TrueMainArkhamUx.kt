package com.eitanadler.arkhamcalcredux2

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eitanadler.arkhamcalcredux2.game.BlessOption
import com.eitanadler.arkhamcalcredux2.ux.CheckboxAndLabelWithHelp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class TrueMainArkhamUX(
    private val dataStore: DataStore<Preferences>,
) {
    @OptIn(ExperimentalMaterial3Api::class)
    private lateinit var chancesSliderState: SliderState

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ComposableTrueMainArkhamTab(
        goToHelpScreen: (String) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val calculatorResultText = remember { mutableStateOf("Result!") }
        val calculatorResultColor = remember { mutableStateOf(Color.Blue) }

        val (selectedBlessOption, onBlessOptionSelected) = remember { mutableStateOf(BlessOption.NORMAL) }

        var isMandyChecked by rememberSaveable { mutableStateOf(false) }
        var isSkidsChecked by rememberSaveable { mutableStateOf(false) }
        var isRerollOnesChecked by rememberSaveable { mutableStateOf(false) }
        var isAddOneChecked by rememberSaveable { mutableStateOf(false) }
        var isShotgunChecked by rememberSaveable { mutableStateOf(false) }

        val diceCountSliderState =
            rememberSliderState(
                value = 1.0f,
                steps = DICE_MAX - 2,
                valueRange = 1f..DICE_MAX.toFloat(),
            )
        val difficultySliderState =
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
                difficultySliderValue = difficultySliderState.value.toInt(),
                chancesSliderValue = chancesSliderState.value.toInt(),
                blessedStatus = selectedBlessOption,
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

        difficultySliderState.onValueChange = { v: Float ->
            difficultySliderState.value = v
            simpleInternalRecalculate()
        }

        val context = LocalContext.current
        val resources = LocalResources.current
        chancesSliderState.onValueChange = { v: Float ->
            val mPreviousChanceValue = chancesSliderState.value.toInt()
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

        Column(modifier) {
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
                        difficultySliderState.value.toInt().toString(),
                    )
                }
                Slider(
                    state = difficultySliderState,
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

            LaunchedEffect(Unit) { }

            val crScope = rememberCoroutineScope()
            handleShowFirstTimeDialog(LocalContext.current, crScope)
            HorizontalDivider()
            Column(Modifier.selectableGroup()) {
                Text(
                    "Blessed / Cursed",
                    style = MaterialTheme.typography.titleMedium,
                    modifier =
                        Modifier.combinedClickable(
                            onClick = {},
                            onLongClickLabel =
                                stringResource(
                                    R.string.go_to_help_screen_by_label,
                                    "Blessed / Cursed",
                                ),
                            onLongClick = {
                                goToHelpScreen("Blessed / Cursed")
                            },
                        ),
                )
                BlessOption.entries.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == selectedBlessOption),
                                onClick = {
                                    onBlessOptionSelected(option)
                                    simpleInternalRecalculate()
                                },
                                role = Role.RadioButton,
                            ).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = (option == selectedBlessOption),
                            onClick = null, // null recommended for accessibility with screen readers
                        )
                        Text(
                            text = option.text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }
                }
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
    private fun handleShowFirstTimeDialog(
        context: Context,
        crScope: CoroutineScope,
    ) {
        val prefKey = stringPreferencesKey(PREFS_KEY_FIRST_TIME_16)
        val prefResultFlow: Flow<String?> =
            dataStore.data.map { preferences ->
                preferences[prefKey]
            }
        crScope.launch {
            val prefResult = prefResultFlow.first()
            if (prefResult != null) return@launch
            val builder =
                AlertDialog
                    .Builder(context)
                    .setMessage(R.string.first_dialog_message)
                    .setNeutralButton(
                        R.string.first_dialog_button,
                    ) { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                        crScope.launch {
                            dataStore.edit { settings ->
                                settings[prefKey] = "a"
                            }
                        }
                    }
            builder.create().show()
        }
    }

    private fun recalculate(
        calculatorResultText: MutableState<String>,
        calculatorResultColor: MutableState<Color>,
        diceCountSliderValue: Int,
        chancesSliderValue: Int,
        difficultySliderValue: Int,
        blessedStatus: BlessOption,
        isMandyChecked: Boolean,
        isSkidsChecked: Boolean,
        isRerollOnesChecked: Boolean,
        isAddOneChecked: Boolean,
        isShotgunChecked: Boolean,
    ) {
        val calculator =
            Calculator(
                mDice = diceCountSliderValue,
                mTough = difficultySliderValue,
                mNumberOfChances = chancesSliderValue,
                mIsBlessed = blessedStatus == BlessOption.BLESS,
                mIsCursed = blessedStatus == BlessOption.CURSED,
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
        if (isAbilityChecked && chancesSliderState.value.toInt() > 1) {
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
