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
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * The main activity. Routes user input to Calculator and prints its results.
 */
public class ArkhamCalc : FragmentActivity() {
    private lateinit var mDiceLabel: TextView
    private lateinit var mDiceSeekBar: SeekBar
    private lateinit var mDiceValue: TextView
    private lateinit var mToughLabel: TextView
    private lateinit var mToughSeekBar: SeekBar
    private lateinit var mToughValue: TextView
    private lateinit var mChanceLabel: TextView
    private lateinit var mChanceSeekBar: SeekBar
    private lateinit var mChanceValue: TextView
    private lateinit var mAllModifiers: AllModifierOptions
    private lateinit var mResultTextView: TextView

    private var mPreviousChanceValue = 0
    private var mRestoringState = false

    private val Context.dataStore by preferencesDataStore(name = "settings")

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)

        //find controls
        mDiceLabel = findViewById(R.id.diceLabel)
        mDiceSeekBar = findViewById(R.id.diceSeekBar)
        mDiceValue = findViewById(R.id.diceValue)
        mToughLabel = findViewById(R.id.toughLabel)
        mToughSeekBar = findViewById(R.id.toughSeekBar)
        mToughValue = findViewById(R.id.toughValue)
        mChanceLabel = findViewById(R.id.chanceLabel)
        mChanceSeekBar = findViewById(R.id.chanceSeekBar)
        mChanceValue = findViewById(R.id.chanceValue)
        mResultTextView = findViewById(R.id.resultTextView)
        mAllModifiers = findViewById(R.id.allModifiers)

        //setup controls
        mDiceSeekBar.setMax(DICE_MAX - 1)
        mToughSeekBar.setMax(TOUGH_MAX - 1)
        mChanceSeekBar.setMax(CHANCE_MAX - 1)

        //attach setOnSeekBarChangeListener
        mDiceSeekBar.setOnSeekBarChangeListener(object : OnSeekBarProgressChangeListener() {
            public override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                setSeekBarValues()
                recalculate()
            }
        })
        mToughSeekBar.setOnSeekBarChangeListener(object : OnSeekBarProgressChangeListener() {
            public override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                setSeekBarValues()
                recalculate()
            }
        })
        mChanceSeekBar.setOnSeekBarChangeListener(object : OnSeekBarProgressChangeListener() {
            public override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                setSeekBarValues()
                recalculate()

                mPreviousChanceValue = this.previousProgress
                handleOneTimeAbilityChancesChanged(
                    mAllModifiers.isMandyChecked(),
                    R.string.mandy_chances_toast
                )
                handleOneTimeAbilityChancesChanged(
                    mAllModifiers.isRerollChecked(),
                    R.string.reroll_ones_chances_toast
                )
                handleOneTimeAbilityChancesChanged(
                    mAllModifiers.isSkidsChecked(),
                    R.string.skids_chances_toast
                )
            }
        })

        //attach setOnCheckedChangeListener
        mAllModifiers.setBlessedOnChangedEvent {
            if (mAllModifiers.isBlessedChecked()) {
                //can't be cursed and blessed at the same time
                mAllModifiers.setCurseChecked(false)
            }
            recalculate()
        }
        mAllModifiers.setCursedOnChangedEvent {
            if (mAllModifiers.isCursedChecked()) {
                //can't be cursed and blessed at the same time
                mAllModifiers.setBlessChecked(false)
            }
            recalculate()
        }

        mAllModifiers.setShotgunOnChangedEvent { recalculate() }
        mAllModifiers.setMandyOnChangedEvent {
            if (mAllModifiers.isMandyChecked()) {
                //both Mandy and Reroll ones on at same time not supported
                mAllModifiers.setRerollChecked(false)
                mAllModifiers.setSkidsChecked(false)
            }
            recalculate()
            handleOneTimeAbilityOptionChanged(
                mAllModifiers.isMandyChecked(),
                R.string.mandy_chances_toast
            )
        }
        mAllModifiers.setRerollOnChangedEvent {
            if (mAllModifiers.isRerollChecked()) {
                //both Mandy and Reroll ones on at same time not supported
                mAllModifiers.setMandyChecked(false)
                mAllModifiers.setSkidsChecked(false)
            }
            recalculate()
            handleOneTimeAbilityOptionChanged(
                mAllModifiers.isRerollChecked(),
                R.string.reroll_ones_chances_toast
            )
        }
        mAllModifiers.setSkidsOnChangedEvent {
            if (mAllModifiers.isSkidsChecked()) {
                //both Mandy and Reroll ones on at same time not supported
                mAllModifiers.setMandyChecked(false)
                mAllModifiers.setRerollChecked(false)
            }
            recalculate()
            handleOneTimeAbilityOptionChanged(
                mAllModifiers.isSkidsChecked(),
                R.string.skids_chances_toast
            )
        }

        //attach setOnLongClickListener
        mDiceLabel.setOnLongClickListener { v: View ->
            startHelpActivity("Dice / Difficulty")
            true
        }
        mToughLabel.setOnLongClickListener { v: View ->
            startHelpActivity("Dice / Difficulty")
            true
        }
        mChanceLabel.setOnLongClickListener { v: View ->
            startHelpActivity("Chances")
            true
        }

        //first calculation
        setSeekBarValues()
        recalculate()

        handleShowFirstTimeDialog()
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

        val mi = menu.findItem(R.id.menu_item_help)
        try {
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        } catch (e: IllegalArgumentException) {
            throw RuntimeException(e)
        }

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
        val prefResultFlow: Flow<String?> = dataStore.data.map { preferences ->
            preferences[prefKey]
        }
        val prefResult: String? = runBlocking {
            prefResultFlow.first()
        }
        if (prefResult != null) return

        val builder = AlertDialog.Builder(this)
            .setMessage(R.string.first_dialog_message)
            .setNeutralButton(
                R.string.first_dialog_button
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
        helpIntent.putExtra(ArkhamCalcHelp.Companion.BUNDLE_TOPIC, topic)
        startActivity(helpIntent)
    }

    private fun recalculate() {
        //get input
        val dice = mDiceSeekBar.progress + 1
        val tough = mToughSeekBar.progress + 1
        val chance = mChanceSeekBar.progress + 1
        val isBlessed = mAllModifiers.isBlessedChecked()
        val isCursed = mAllModifiers.isCursedChecked()
        val isShotgun = mAllModifiers.isShotgunChecked()
        val isMandy = mAllModifiers.isMandyChecked()
        val isRerollOnes = mAllModifiers.isRerollChecked()
        val isSkidsOnes = mAllModifiers.isSkidsChecked()
        val isAddOne = mAllModifiers.isAddOneChecked()

        //calculate
        val calculator = Calculator(dice, tough)
        calculator.setNumberOfChances(chance)
        calculator.setIsBlessed(isBlessed)
        calculator.setIsCursed(isCursed)
        calculator.setIsShotgun(isShotgun)
        calculator.setIsMandy(isMandy)
        calculator.setIsRerollOnes(isRerollOnes)
        calculator.setIsSkids(isSkidsOnes)
        calculator.setIsAddOne(isAddOne)
        val result = calculator.calculate()

        //format and set ui
        val formatter = CalculateResultFormatter(result)

        val resultString = formatter.resultString
        mResultTextView.text = resultString

        val color = formatter.color
        mResultTextView.setTextColor(color)
    }

    private fun setSeekBarValues() {
        mDiceValue.text = (mDiceSeekBar.progress + 1).toString()
        mToughValue.text = (mToughSeekBar.progress + 1).toString()
        mChanceValue.text = (mChanceSeekBar.progress + 1).toString()
    }

    /**
     * Show a message to the user regarding this one-time ability if user has more than one chance.
     * Case where ability has been changed; check number of chances.
     * Do not show a message if we are in the process of restoring state (because we already showed
     * the message before state was saved).
     */
    private fun handleOneTimeAbilityOptionChanged(
        isAbilityChecked: Boolean,
        resourceStringId: Int
    ) {
        if (isAbilityChecked && mChanceSeekBar.progress > 0 && !mRestoringState) {
            showToast(getResourceString(resourceStringId))
        }
    }

    /**
     * Show a message to the user regarding this one-time ability if user has more than one chance.
     * Case where number of chances have changed; check if ability is selected and chances have changed
     * from one to > 1.
     * Do not show a message if we are in the process of restoring state (because we already showed
     * the message before state was saved).
     */
    private fun handleOneTimeAbilityChancesChanged(
        isAbilityChecked: Boolean,
        resourceStringId: Int
    ) {
        if (isAbilityChecked && mPreviousChanceValue <= 0 && mChanceSeekBar.progress > 0 && !mRestoringState) {
            showToast(getResourceString(resourceStringId))
        }
    }

    private fun showToast(toastText: String) {
        Toast.makeText(baseContext, toastText, Toast.LENGTH_LONG).show()
//        Snackbar.make(findViewById(R.id.outerLayout), toastText, Snackbar.LENGTH_LONG).show()
    }

    private fun getResourceString(resourceStringId: Int): String {
        return resources.getString(resourceStringId)
    }

    private abstract inner class OnSeekBarProgressChangeListener : OnSeekBarChangeListener {
        var previousProgress: Int = 0
            private set

        abstract override fun onProgressChanged(
            seekBar: SeekBar, progress: Int,
            fromUser: Boolean
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
