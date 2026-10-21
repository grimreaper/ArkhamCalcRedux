package com.eitanadler.arkhamcalcredux2

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eitanadler.arkhamcalcredux2.game.BlessOption
import com.eitanadler.arkhamcalcredux2.game.InvestigatorOption
import com.eitanadler.arkhamcalcredux2.ux.CheckboxAndLabelWithHelp

private const val DICE_MAX = 16
private const val TOUGH_MAX = 6
private const val CHANCE_MAX = 6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainArkhamTab(
    goToHelpScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val calculatorResultText = remember { mutableStateOf("Result!") }
    val calculatorResultColor = remember { mutableStateOf(Color.Blue) }

    val (selectedBlessOption, onBlessOptionSelected) = remember { mutableStateOf(BlessOption.NORMAL) }
    val (selectedInvestigatorOption, onInvestigatorOptionSelected) =
        remember {
            mutableStateOf(
                InvestigatorOption.NORMAL,
            )
        }

    var isRerollOnesEnabled by rememberSaveable { mutableStateOf(true) }
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
    val chancesSliderState: SliderState =
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
            investigatorStatus = selectedInvestigatorOption,
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

    chancesSliderState.onValueChange = { v: Float ->
        chancesSliderState.value = v
        simpleInternalRecalculate()
    }

    Column(modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Result", fontWeight = FontWeight.Bold)
            Text(calculatorResultText.value, color = calculatorResultColor.value)
        }

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

        HorizontalDivider()
        ListItem(
            overlineContent = {
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
            },
            headlineContent = {
                Column(modifier = Modifier.selectableGroup()) {
                    BlessOption.entries.forEach { option ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (option == selectedBlessOption),
                                    onClick = {},
                                    role = Role.RadioButton,
                                ).combinedClickable(
                                    onClick = {
                                        onBlessOptionSelected(option)
                                        simpleInternalRecalculate()
                                    },
                                    onLongClick = {
                                        goToHelpScreen("Blessed / Cursed")
                                    },
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
            },
        )
        HorizontalDivider()
        ListItem(
            overlineContent = {
                Text(
                    "Investigator",
                    style = MaterialTheme.typography.titleMedium,
                    modifier =
                        Modifier.combinedClickable(
                            onClick = {},
                            onLongClickLabel =
                                stringResource(
                                    R.string.go_to_help_screen_by_label,
                                    "Investigator",
                                ),
                            onLongClick = {
                                // TODO: consider making this useful
                                goToHelpScreen("Investigator")
                            },
                        ),
                )
            },
            headlineContent = {
                Column(Modifier.selectableGroup()) {
                    InvestigatorOption.entries.forEach { option ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (option == selectedInvestigatorOption),
                                    onClick = { },
                                    role = Role.RadioButton,
                                ).combinedClickable(
                                    onClick = {
                                        if (option != InvestigatorOption.NORMAL) {
                                            isRerollOnesChecked = false
                                            isRerollOnesEnabled = false
                                        } else {
                                            isRerollOnesEnabled = true
                                        }
                                        onInvestigatorOptionSelected(option)
                                        simpleInternalRecalculate()
                                    },
                                    onLongClick = {
                                        goToHelpScreen(option.helpLabel)
                                    },
                                ).padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = (option == selectedInvestigatorOption),
                                onClick = null, // null recommended for accessibility with screen readers
                            )
                            Text(
                                text = option.textLabel,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                    }
                }
            },
            supportingContent = {
                if (
                    selectedInvestigatorOption.singleUseChanceNotice != null &&
                    chancesSliderState.value.toInt() > 1
                ) {
                    Text(stringResource(selectedInvestigatorOption.singleUseChanceNotice))
                }
            },
        )
        HorizontalDivider()
        ListItem(
            overlineContent = {
                Text(
                    "Skills & Weapons",
                    style = MaterialTheme.typography.titleMedium,
                    modifier =
                        Modifier.combinedClickable(
                            onClick = {},
                            onLongClickLabel =
                                stringResource(
                                    R.string.go_to_help_screen_by_label,
                                    "Skills & Weapons",
                                ),
                            onLongClick = {
                                // TODO: consider making this useful
                                goToHelpScreen("Skills & Weapons")
                            },
                        ),
                )
            },
            headlineContent = {
                CheckboxAndLabelWithHelp(
                    enabled = isRerollOnesEnabled,
                    checked = isRerollOnesChecked,
                    onCheckedChange = { newValue ->
                        isRerollOnesChecked = newValue
                        simpleInternalRecalculate()
                    },
                    goToHelpScreen = goToHelpScreen,
                    label = "Reroll Ones",
                )
            },
            supportingContent = {
                if (
                    isRerollOnesChecked &&
                    chancesSliderState.value.toInt() > 1
                ) {
                    Text(stringResource(R.string.reroll_ones_chances_message))
                }
            },
        )
        ListItem(
            headlineContent = {
                CheckboxAndLabelWithHelp(
                    checked = isAddOneChecked,
                    onCheckedChange = { newValue ->
                        isAddOneChecked = newValue
                        simpleInternalRecalculate()
                    },
                    label = "Add One",
                    goToHelpScreen = goToHelpScreen,
                )
            },
        )
        ListItem(
            headlineContent = {
                CheckboxAndLabelWithHelp(
                    checked = isShotgunChecked,
                    onCheckedChange = { newValue ->
                        isShotgunChecked = newValue
                        simpleInternalRecalculate()
                    },
                    label = "Shotgun",
                    goToHelpScreen = goToHelpScreen,
                )
            },
            supportingContent = {
                if (
                    isShotgunChecked &&
                    difficultySliderState.value.toInt() == 1
                ) {
                    Text("Shotgun has no effect when difficulty is equal to 1.")
                }
            },
        )
        HorizontalDivider()
    }
    simpleInternalRecalculate()
}

private fun recalculate(
    calculatorResultText: MutableState<String>,
    calculatorResultColor: MutableState<Color>,
    diceCountSliderValue: Int,
    chancesSliderValue: Int,
    difficultySliderValue: Int,
    blessedStatus: BlessOption,
    isRerollOnesChecked: Boolean,
    isAddOneChecked: Boolean,
    isShotgunChecked: Boolean,
    investigatorStatus: InvestigatorOption,
) {
    val calculator =
        Calculator(
            mDice = diceCountSliderValue,
            mTough = difficultySliderValue,
            mNumberOfChances = chancesSliderValue,
            mBlessStatus = blessedStatus,
            mIsShotgun = isShotgunChecked,
            mIsRerollOnes = isRerollOnesChecked,
            mIsAddOne = isAddOneChecked,
            mInvestigatorOption = investigatorStatus,
        )
    val result = calculator.calculate()

    val formatter = CalculateResultFormatter(result)

    calculatorResultText.value = formatter.resultString
    calculatorResultColor.value = formatter.color
}
