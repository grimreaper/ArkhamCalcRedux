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
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private lateinit var mBlessCheckBox: CheckBox
    private lateinit var mCurseCheckBox: CheckBox
    private lateinit var mShotgunCheckBox: CheckBox
    private lateinit var mMandyCheckBox: CheckBox
    private lateinit var mRerollOnesCheckBox: CheckBox
    private lateinit var mSkidsOnesCheckBox: CheckBox
    private lateinit var mAddOneCheckBox: AddOneTextAndLabelButton
    private lateinit var mResultTextView: TextView

    private var mPreviousChanceValue = 0
    private var mRestoringState = false

    private val Context.dataStore by preferencesDataStore(name = "settings")

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)

        //find controls
        mDiceLabel = findViewById<View>(R.id.diceLabel) as TextView
        mDiceSeekBar = findViewById<View>(R.id.diceSeekBar) as SeekBar
        mDiceValue = findViewById<View>(R.id.diceValue) as TextView
        mToughLabel = findViewById<View>(R.id.toughLabel) as TextView
        mToughSeekBar = findViewById<View>(R.id.toughSeekBar) as SeekBar
        mToughValue = findViewById<View>(R.id.toughValue) as TextView
        mChanceLabel = findViewById<View>(R.id.chanceLabel) as TextView
        mChanceSeekBar = findViewById<View>(R.id.chanceSeekBar) as SeekBar
        mChanceValue = findViewById<View>(R.id.chanceValue) as TextView
        mBlessCheckBox = findViewById<View>(R.id.blessCheckBox) as CheckBox
        mCurseCheckBox = findViewById<View>(R.id.curseCheckBox) as CheckBox
        mShotgunCheckBox = findViewById<View>(R.id.shotgunCheckBox) as CheckBox
        mMandyCheckBox = findViewById<View>(R.id.mandyCheckBox) as CheckBox
        mRerollOnesCheckBox = findViewById<View>(R.id.rerollOnesCheckBox) as CheckBox
        mSkidsOnesCheckBox = findViewById<View>(R.id.skidsOnesCheckBox) as CheckBox
        mAddOneCheckBox = findViewById<AddOneTextAndLabelButton>(R.id.newAddOneCheckBox)
        mResultTextView = findViewById<View>(R.id.resultTextView) as TextView

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
                    mMandyCheckBox.isChecked,
                    R.string.mandy_chances_toast
                )
                handleOneTimeAbilityChancesChanged(
                    mRerollOnesCheckBox.isChecked,
                    R.string.reroll_ones_chances_toast
                )
                handleOneTimeAbilityChancesChanged(
                    mSkidsOnesCheckBox.isChecked,
                    R.string.skids_chances_toast
                )
            }
        })

        //attach setOnCheckedChangeListener
        mBlessCheckBox.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                //can't be cursed and blessed at the same time
                mCurseCheckBox.setChecked(false)
            }
            recalculate()
        }
        mCurseCheckBox.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                //can't be cursed and blessed at the same time
                mBlessCheckBox.setChecked(false)
            }
            recalculate()
        }
        mShotgunCheckBox.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean -> recalculate() }
        mMandyCheckBox.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                //both Mandy and Reroll ones on at same time not supported
                mRerollOnesCheckBox.setChecked(false)
                mSkidsOnesCheckBox.setChecked(false)
            }
            recalculate()
            handleOneTimeAbilityOptionChanged(
                mMandyCheckBox.isChecked,
                R.string.mandy_chances_toast
            )
        }
        mRerollOnesCheckBox.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                //both Mandy and Reroll ones on at same time not supported
                mMandyCheckBox.setChecked(false)
                mSkidsOnesCheckBox.setChecked(false)
            }
            recalculate()
            handleOneTimeAbilityOptionChanged(
                mRerollOnesCheckBox.isChecked,
                R.string.reroll_ones_chances_toast
            )
        }
        mSkidsOnesCheckBox.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                //both Mandy and Reroll ones on at same time not supported
                mMandyCheckBox.setChecked(false)
                mRerollOnesCheckBox.setChecked(false)
            }
            recalculate()
            handleOneTimeAbilityOptionChanged(
                mSkidsOnesCheckBox.isChecked,
                R.string.skids_chances_toast
            )
        }
        mAddOneCheckBox.setOnChangedEvent {
            recalculate()
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
        mBlessCheckBox.setOnLongClickListener { v: View ->
            startHelpActivity("Blessed / Cursed")
            true
        }
        mCurseCheckBox.setOnLongClickListener { v: View ->
            startHelpActivity("Blessed / Cursed")
            true
        }
        mMandyCheckBox.setOnLongClickListener { v: View ->
            startHelpActivity("Mandy")
            true
        }
        mSkidsOnesCheckBox.setOnLongClickListener { v: View ->
            startHelpActivity("Skids")
            true
        }
        mRerollOnesCheckBox.setOnLongClickListener { v: View ->
            startHelpActivity("Reroll Ones")
            true
        }
        mAddOneCheckBox.setOnLongClickListener { v: View ->
            startHelpActivity("Add One")
            true
        }
        mShotgunCheckBox.setOnLongClickListener { v: View ->
            startHelpActivity("Shotgun")
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
        val isBlessed = mBlessCheckBox.isChecked
        val isCursed = mCurseCheckBox.isChecked
        val isShotgun = mShotgunCheckBox.isChecked
        val isMandy = mMandyCheckBox.isChecked
        val isRerollOnes = mRerollOnesCheckBox.isChecked
        val isSkidsOnes = mSkidsOnesCheckBox.isChecked
        val isAddOne = mAddOneCheckBox.isChecked()

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
