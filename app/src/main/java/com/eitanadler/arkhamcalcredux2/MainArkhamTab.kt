package com.eitanadler.arkhamcalcredux2

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eitanadler.arkhamcalcredux2.game.BlessOption
import com.eitanadler.arkhamcalcredux2.game.InvestigatorOption
import com.eitanadler.arkhamcalcredux2.ui.theme.chanceColorPalette
import com.eitanadler.arkhamcalcredux2.util.CalculateResultFormatter
import com.eitanadler.arkhamcalcredux2.ux.CheckboxAndLabelWithHelp
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

private const val DICE_MAX = 16
private const val TOUGH_MAX = 6
private const val CHANCE_MAX = 6

@Composable
internal fun MainArkhamTab(
    goToHelpScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            trackRange = 1f..DICE_MAX.toFloat(),
        )
    val difficultySliderState =
        rememberSliderState(
            value = 1.0f,
            steps = TOUGH_MAX - 2,
            trackRange = 1f..TOUGH_MAX.toFloat(),
        )
    val chancesSliderState: SliderState =
        rememberSliderState(
            value = 1.0f,
            steps = CHANCE_MAX - 2,
            trackRange = 1f..CHANCE_MAX.toFloat(),
        )

    val palette = MaterialTheme.chanceColorPalette

    val calculator =
        Calculator(
            mDice = diceCountSliderState.value.toInt(),
            mTough = difficultySliderState.value.toInt(),
            mNumberOfChances = chancesSliderState.value.toInt(),
            mBlessStatus = selectedBlessOption,
            mIsShotgun = isShotgunChecked,
            mIsRerollOnes = isRerollOnesChecked,
            mIsAddOne = isAddOneChecked,
            mInvestigatorOption = selectedInvestigatorOption,
        )
    val result = calculator.calculate()

    val formatter = CalculateResultFormatter(result, palette = palette)

    val calculatorResultText = formatter.resultString
    val calculatorResultColor = formatter.color

    val listState = rememberLazyListState()
    LazyColumnScrollbar(
        state = listState,
        settings = ScrollbarSettings.Default,
        modifier = modifier,
    ) {
        LazyColumn(
            state = listState,
        ) {
            // not sure why these two are required.
            item {
                ListItem { }
            }
            item {
                ListItem { }
            }
            item {
                ListItem {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Result", fontWeight = FontWeight.Bold)
                        Text(calculatorResultText, color = calculatorResultColor)
                    }
                }
            }
            item {
                Column {
                    Row {
                        Text(
                            "Dice: ",
                            fontWeight = FontWeight.Bold,
                            modifier =
                            Modifier.combinedClickable(
                                onClick = {},
                                onLongClickLabel = stringResource(
                                    R.string.go_to_help_screen_by_label,
                                    "Dice / Difficulty",
                                ),
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
                        onValueChange = { v ->
                            diceCountSliderState.value = v
                        },
                    )
                }
            }
            item {
                Column {
                    Row {
                        Text(
                            "Difficulty: ",
                            fontWeight = FontWeight.Bold,
                            modifier =
                            Modifier.combinedClickable(
                                onClick = {},
                                onLongClickLabel = stringResource(
                                    R.string.go_to_help_screen_by_label,
                                    "Dice / Difficulty",
                                ),
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
                        onValueChange = { v ->
                            difficultySliderState.value = v
                        },
                    )
                }
            }
            item {
                Column {
                    Row {
                        Text(
                            "Chances: ",
                            fontWeight = FontWeight.Bold,
                            modifier =
                            Modifier.combinedClickable(
                                onClick = {},
                                onLongClickLabel = stringResource(
                                    R.string.go_to_help_screen_by_label,
                                    "Chances",
                                ),
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
                        onValueChange = { v ->
                            chancesSliderState.value = v
                        },
                    )
                }
            }
            item {
                HorizontalDivider()
            }
            item {
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
                ) {
                    Column(modifier = Modifier.selectableGroup()) {
                        BlessOption.entries.forEach { option ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (option == selectedBlessOption),
                                        onClick = {},
                                        role = Role.RadioButton,
                                    )
                                    .combinedClickable(
                                        onClick = {
                                            onBlessOptionSelected(option)
                                        },
                                        onLongClick = {
                                            goToHelpScreen("Blessed / Cursed")
                                        },
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = (option == selectedBlessOption),
                                    onClick = null,
                                )
                                Text(
                                    text = option.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp),
                                )
                            }
                        }
                    }
                }
            }
            item {
                HorizontalDivider()
            }
            item {
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
                    supportingContent = {
                        if (
                            selectedInvestigatorOption.singleUseChanceNotice != null &&
                            chancesSliderState.value.toInt() > 1
                        ) {
                            Text(stringResource(selectedInvestigatorOption.singleUseChanceNotice))
                        }
                    },

                ) {
                    Column(Modifier.selectableGroup()) {
                        InvestigatorOption.entries.forEach { option ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (option == selectedInvestigatorOption),
                                        onClick = { },
                                        role = Role.RadioButton,
                                    )
                                    .combinedClickable(
                                        onClick = {
                                            if (option != InvestigatorOption.NORMAL) {
                                                isRerollOnesChecked = false
                                                isRerollOnesEnabled = false
                                            } else {
                                                isRerollOnesEnabled = true
                                            }
                                            onInvestigatorOptionSelected(option)
                                        },
                                        onLongClick = {
                                            goToHelpScreen(option.helpLabel)
                                        },
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = (option == selectedInvestigatorOption),
                                    onClick = null,
                                )
                                Text(
                                    text = option.textLabel,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp),
                                )
                            }
                        }
                    }
                }
            }

            item {
                HorizontalDivider()
            }
            item {
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
                    supportingContent = {
                        if (
                            isRerollOnesChecked &&
                            chancesSliderState.value.toInt() > 1
                        ) {
                            Text(stringResource(R.string.reroll_ones_chances_message))
                        }
                    },
                ) {
                    CheckboxAndLabelWithHelp(
                        enabled = isRerollOnesEnabled,
                        checked = isRerollOnesChecked,
                        onCheckedChange = { newValue ->
                            isRerollOnesChecked = newValue
                        },
                        goToHelpScreen = goToHelpScreen,
                        label = "Reroll Ones",
                    )
                }
            }
            item {
                ListItem {
                    CheckboxAndLabelWithHelp(
                        checked = isAddOneChecked,
                        onCheckedChange = { newValue ->
                            isAddOneChecked = newValue
                        },
                        label = "Add One",
                        goToHelpScreen = goToHelpScreen,
                    )
                }
            }
            item {
                ListItem(
                    supportingContent = {
                        if (
                            isShotgunChecked &&
                            difficultySliderState.value.toInt() == 1
                        ) {
                            Text(stringResource(R.string.shotgun_has_no_effect_when_difficulty_is_equal_to_1))
                        }
                    },
                ) {
                    CheckboxAndLabelWithHelp(
                        checked = isShotgunChecked,
                        onCheckedChange = { newValue ->
                            isShotgunChecked = newValue
                        },
                        label = "Shotgun",
                        goToHelpScreen = goToHelpScreen,
                    )
                }
            }
            item {
                HorizontalDivider()
            }
        }
    }
}
