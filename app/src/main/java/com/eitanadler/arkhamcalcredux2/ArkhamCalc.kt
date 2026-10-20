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

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.FragmentActivity
import com.eitanadler.arkhamcalcredux2.databinding.MainBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * The main activity. Routes user input to Calculator and prints its results.
 */
public class ArkhamCalc : FragmentActivity() {
    private lateinit var mDiceLabel: SettableText
    private lateinit var mDiceSeekBar: SeekBar
    private lateinit var mDiceValue: SettableText
    private lateinit var mToughLabel: SettableText
    private lateinit var mToughSeekBar: SeekBar
    private lateinit var mToughValue: SettableText
    private lateinit var mChanceLabel: SettableText
    private lateinit var mChanceSeekBar: SeekBar
    private lateinit var mChanceValue: SettableText

    private var mPreviousChanceValue = 0
    private var mRestoringState = false

    private val Context.dataStore by preferencesDataStore(name = "settings")

    @OptIn(ExperimentalMaterial3Api::class)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        },
                    ) { innerPadding ->
                        val calculatorResultText = rememberSaveable { mutableStateOf("Result!") }
                        val calculatorResultColor = remember { mutableStateOf(Color.Blue) }
                        var isBlessedChecked by remember { mutableStateOf(false) }
                        var isCursedChecked by remember { mutableStateOf(false) }
                        var isMandyChecked by remember { mutableStateOf(false) }
                        var isSkidsChecked by remember { mutableStateOf(false) }
                        var isRerollOnesChecked by remember { mutableStateOf(false) }
                        var isAddOneChecked by remember { mutableStateOf(false) }
                        var isShotgunChecked by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier.padding(innerPadding),
                        ) {
                            Text("Result", fontWeight = FontWeight.Bold)
                            Text(calculatorResultText.value, color = calculatorResultColor.value)

                            AndroidViewBinding(
                                MainBinding::inflate,
                                modifier = Modifier.fillMaxHeight(0.40f),
                            ) {
                                // find controls
                                mDiceLabel = findViewById(R.id.diceLabel)
                                mDiceSeekBar = findViewById(R.id.diceSeekBar)
                                mDiceValue = findViewById(R.id.diceValue)
                                mToughLabel = findViewById(R.id.toughLabel)
                                mToughSeekBar = findViewById(R.id.toughSeekBar)
                                mToughValue = findViewById(R.id.toughValue)
                                mChanceLabel = findViewById(R.id.chanceLabel)
                                mChanceSeekBar = findViewById(R.id.chanceSeekBar)
                                mChanceValue = findViewById(R.id.chanceValue)

                                calculatorResultColor.value = Color.Red

                                // setup controls
                                mDiceSeekBar.setMax(DICE_MAX - 1)
                                mToughSeekBar.setMax(TOUGH_MAX - 1)
                                mChanceSeekBar.setMax(CHANCE_MAX - 1)

                                // attach setOnSeekBarChangeListener
                                mDiceSeekBar.setOnSeekBarChangeListener(
                                    object :
                                        OnSeekBarProgressChangeListener() {
                                        public override fun onProgressChanged(
                                            seekBar: SeekBar,
                                            progress: Int,
                                            fromUser: Boolean,
                                        ) {
                                            setSeekBarValues()
                                            recalculate(
                                                calculatorResultText,
                                                calculatorResultColor,
                                                isBlessed = isBlessedChecked,
                                                isCursed = isCursedChecked,
                                                isMandyChecked = isMandyChecked,
                                                isSkidsChecked = isSkidsChecked,
                                                isRerollOnesChecked = isRerollOnesChecked,
                                                isAddOneChecked = isAddOneChecked,
                                                isShotgunChecked = isShotgunChecked,
                                            )
                                        }
                                    },
                                )
                                mToughSeekBar.setOnSeekBarChangeListener(
                                    object :
                                        OnSeekBarProgressChangeListener() {
                                        public override fun onProgressChanged(
                                            seekBar: SeekBar,
                                            progress: Int,
                                            fromUser: Boolean,
                                        ) {
                                            setSeekBarValues()
                                            recalculate(
                                                calculatorResultText,
                                                calculatorResultColor,
                                                isBlessed = isBlessedChecked,
                                                isCursed = isCursedChecked,
                                                isMandyChecked = isMandyChecked,
                                                isSkidsChecked = isSkidsChecked,
                                                isRerollOnesChecked = isRerollOnesChecked,
                                                isAddOneChecked = isAddOneChecked,
                                                isShotgunChecked = isShotgunChecked,
                                            )
                                        }
                                    },
                                )
                                mChanceSeekBar.setOnSeekBarChangeListener(
                                    object :
                                        OnSeekBarProgressChangeListener() {
                                        public override fun onProgressChanged(
                                            seekBar: SeekBar,
                                            progress: Int,
                                            fromUser: Boolean,
                                        ) {
                                            setSeekBarValues()
                                            recalculate(
                                                calculatorResultText,
                                                calculatorResultColor,
                                                isBlessed = isBlessedChecked,
                                                isCursed = isCursedChecked,
                                                isMandyChecked = isMandyChecked,
                                                isSkidsChecked = isSkidsChecked,
                                                isRerollOnesChecked = isRerollOnesChecked,
                                                isAddOneChecked = isAddOneChecked,
                                                isShotgunChecked = isShotgunChecked,
                                            )

                                            mPreviousChanceValue = this.previousProgress
                                            handleOneTimeAbilityChancesChanged(
                                                isMandyChecked,
                                                R.string.mandy_chances_toast,
                                            )
                                            handleOneTimeAbilityChancesChanged(
                                                isRerollOnesChecked,
                                                R.string.reroll_ones_chances_toast,
                                            )
                                            handleOneTimeAbilityChancesChanged(
                                                isSkidsChecked,
                                                R.string.skids_chances_toast,
                                            )
                                        }
                                    },
                                )
                                // attach setOnLongClickListener
                                mDiceLabel.setText("Dice:")
                                mDiceLabel.setOnLongClickListener {
                                    startHelpActivity("Dice / Difficulty")
                                    true
                                }
                                mToughLabel.setText("Difficulty:")
                                mToughLabel.setOnLongClickListener {
                                    startHelpActivity("Dice / Difficulty")
                                    true
                                }
                                mChanceLabel.setText("Chances:")
                                mChanceLabel.setOnLongClickListener {
                                    startHelpActivity("Chances")
                                    true
                                }

                                // first calculation
                                setSeekBarValues()
                                recalculate(
                                    calculatorResultText,
                                    calculatorResultColor,
                                    isBlessed = isBlessedChecked,
                                    isCursed = isCursedChecked,
                                    isMandyChecked = isMandyChecked,
                                    isSkidsChecked = isSkidsChecked,
                                    isRerollOnesChecked = isRerollOnesChecked,
                                    isAddOneChecked = isAddOneChecked,
                                    isShotgunChecked = isShotgunChecked,
                                )

                                handleShowFirstTimeDialog()
                            }
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
                                                    startHelpActivity("Blessed / Cursed")
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
                                                    startHelpActivity("Blessed / Cursed")
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
                                                    startHelpActivity("Mandy")
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
                                                    startHelpActivity("Skids")
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
                                                    startHelpActivity("Reroll Ones")
                                                },
                                            ),
                                )
                            }
                            Row {
                                AddOneTextAndLabelContent(
                                    checked = isAddOneChecked,
                                    onCheckedChange = { newValue ->
                                        isAddOneChecked = newValue
                                        recalculate(
                                            calculatorResultText,
                                            calculatorResultColor,
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
                                                    startHelpActivity("Add One")
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
                                                    startHelpActivity("Shotgun")
                                                },
                                            ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onRestoreInstanceState(inState: Bundle) {
        mRestoringState = true
        try {
            super.onRestoreInstanceState(inState)
        } finally {
            mRestoringState = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = getMenuInflater()
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        val itemId = item.itemId
        when (itemId) {
            R.id.menu_item_help -> {
                startActivity(Intent(this, ArkhamCalcHelp::class.java))
                return true
            }

            else -> return false
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

    /**
     * Start the help activity with the specified topic opened. The topic passed
     * into this method must exist in the help.xml 'topics' array.
     */
    private fun startHelpActivity(topic: String) {
        val helpIntent = Intent(this, ArkhamCalcHelp::class.java)
        helpIntent.putExtra(ArkhamCalcHelp.BUNDLE_TOPIC, topic)
        startActivity(helpIntent)
    }

    internal fun recalculate(
        calculatorResultText: MutableState<String>,
        calculatorResultColor: MutableState<Color>,
        isBlessed: Boolean,
        isCursed: Boolean,
        isMandyChecked: Boolean,
        isSkidsChecked: Boolean,
        isRerollOnesChecked: Boolean,
        isAddOneChecked: Boolean,
        isShotgunChecked: Boolean,
    ) {
        // get input
        val dice = mDiceSeekBar.progress + 1
        val tough = mToughSeekBar.progress + 1
        val chance = mChanceSeekBar.progress + 1

        // calculate
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

        // format and set ui
        val formatter = CalculateResultFormatter(result)

        calculatorResultText.value = formatter.resultString

        calculatorResultColor.value = formatter.color
    }

    internal fun setSeekBarValues() {
        mDiceValue.setText((mDiceSeekBar.progress + 1).toString())
        mToughValue.setText((mToughSeekBar.progress + 1).toString())
        mChanceValue.setText((mChanceSeekBar.progress + 1).toString())
    }

    /**
     * Show a message to the user regarding this one-time ability if user has more than one chance.
     * Case where ability has been changed; check number of chances.
     * Do not show a message if we are in the process of restoring state (because we already showed
     * the message before state was saved).
     */
    private fun handleOneTimeAbilityOptionChanged(
        isAbilityChecked: Boolean,
        resourceStringId: Int,
    ) {
        if (isAbilityChecked && mChanceSeekBar.progress > 0 && !mRestoringState) {
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
    internal fun handleOneTimeAbilityChancesChanged(
        isAbilityChecked: Boolean,
        resourceStringId: Int,
    ) {
        if (isAbilityChecked && mPreviousChanceValue <= 0 && mChanceSeekBar.progress > 0 && !mRestoringState) {
            showToast(resources.getString(resourceStringId))
        }
    }

    private fun showToast(toastText: String) {
        Toast.makeText(baseContext, toastText, Toast.LENGTH_LONG).show()
//        Snackbar.make(findViewById(R.id.outerLayout), toastText, Snackbar.LENGTH_LONG).show()
    }

    private abstract inner class OnSeekBarProgressChangeListener : OnSeekBarChangeListener {
        var previousProgress: Int = 0
            private set

        abstract override fun onProgressChanged(
            seekBar: SeekBar,
            progress: Int,
            fromUser: Boolean,
        )

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            this.previousProgress = seekBar.progress
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }
    }

    public companion object {
        private const val PREFS_KEY_FIRST_TIME_16 = "FirstTime16"

        private const val DICE_MAX = 16
        private const val TOUGH_MAX = 6
        private const val CHANCE_MAX = 6
    }
}
