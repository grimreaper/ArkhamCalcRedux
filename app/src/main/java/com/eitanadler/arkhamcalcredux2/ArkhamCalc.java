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

package com.eitanadler.arkhamcalcredux2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main activity. Routes user input to Calculator and prints its results.
 */
public class ArkhamCalc extends Activity
{
	private static final String PREFS_NAME = "ArkhamCalcPreferences";
	private static final String PREFS_KEY_FIRST_TIME_16 = "FirstTime16";
	private static final String URL_WIKI = "http://code.google.com/p/arkham-calc/";
	
	private static final int DICE_MAX = 16;
	private static final int TOUGH_MAX = 6;
	private static final int CHANCE_MAX = 6;

	private TextView mDiceLabel;
	private SeekBar mDiceSeekBar;
	private TextView mDiceValue;
	private TextView mToughLabel;
	private SeekBar mToughSeekBar;
	private TextView mToughValue;
	private TextView mChanceLabel;
	private SeekBar mChanceSeekBar;
	private TextView mChanceValue;
	private CheckBox mBlessCheckBox;
	private CheckBox mCurseCheckBox;
	private CheckBox mShotgunCheckBox;
	private CheckBox mMandyCheckBox;
	private CheckBox mRerollOnesCheckBox;
	private CheckBox mSkidsOnesCheckBox;
	private CheckBox mAddOneCheckBox;
	private TextView mResultTextView;

	private int mPreviousChanceValue;
	private boolean mRestoringState;
	private static Method mMenuItemShowAsAction;
	
	static
	{
		initCompatibility();
	}
	
	private static void initCompatibility() throws SecurityException
	{
		try {
			mMenuItemShowAsAction = MenuItem.class.getMethod("setShowAsAction", new Class[] { int.class });
		} catch (NoSuchMethodException e) {
			//occurs if android 1.x or 2.x
			mMenuItemShowAsAction = null;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		//find controls
		mDiceLabel = (TextView) findViewById(R.id.diceLabel);
		mDiceSeekBar = (SeekBar) findViewById(R.id.diceSeekBar);
		mDiceValue = (TextView) findViewById(R.id.diceValue);
		mToughLabel = (TextView) findViewById(R.id.toughLabel);
		mToughSeekBar = (SeekBar) findViewById(R.id.toughSeekBar);
		mToughValue = (TextView) findViewById(R.id.toughValue);
		mChanceLabel = (TextView) findViewById(R.id.chanceLabel);
		mChanceSeekBar = (SeekBar) findViewById(R.id.chanceSeekBar);
		mChanceValue = (TextView) findViewById(R.id.chanceValue);
		mBlessCheckBox = (CheckBox) findViewById(R.id.blessCheckBox);
		mCurseCheckBox = (CheckBox) findViewById(R.id.curseCheckBox);
		mShotgunCheckBox = (CheckBox) findViewById(R.id.shotgunCheckBox);
		mMandyCheckBox = (CheckBox) findViewById(R.id.mandyCheckBox);
		mRerollOnesCheckBox = (CheckBox) findViewById(R.id.rerollOnesCheckBox);
		mSkidsOnesCheckBox = (CheckBox) findViewById(R.id.skidsOnesCheckBox);
		mAddOneCheckBox = (CheckBox) findViewById(R.id.addOneCheckBox);
		mResultTextView = (TextView) findViewById(R.id.resultTextView);

		//setup controls
		mDiceSeekBar.setMax(DICE_MAX - 1);
		mToughSeekBar.setMax(TOUGH_MAX - 1);
		mChanceSeekBar.setMax(CHANCE_MAX - 1);

		//attach setOnSeekBarChangeListener
		mDiceSeekBar.setOnSeekBarChangeListener(new OnSeekBarProgressChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setSeekBarValues();
				recalculate();
			}
		});
		mToughSeekBar.setOnSeekBarChangeListener(new OnSeekBarProgressChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setSeekBarValues();
				recalculate();
			}
		});
		mChanceSeekBar.setOnSeekBarChangeListener(new OnSeekBarProgressChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setSeekBarValues();
				recalculate();

				mPreviousChanceValue = getPreviousProgress();
				handleOneTimeAbilityChancesChanged(mMandyCheckBox.isChecked(), R.string.mandy_chances_toast);
				handleOneTimeAbilityChancesChanged(mRerollOnesCheckBox.isChecked(), R.string.reroll_ones_chances_toast);
				handleOneTimeAbilityChancesChanged(mSkidsOnesCheckBox.isChecked(), R.string.skids_chances_toast);
			}
		});
		
		//attach setOnCheckedChangeListener
		mBlessCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                //can't be cursed and blessed at the same time
                mCurseCheckBox.setChecked(false);
            }
            recalculate();

        });
		mCurseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                //can't be cursed and blessed at the same time
                mBlessCheckBox.setChecked(false);
            }
            recalculate();

        });
		mShotgunCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> recalculate());
		mMandyCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //both Mandy and Reroll ones on at same time not supported
                mRerollOnesCheckBox.setChecked(false);
                mSkidsOnesCheckBox.setChecked(false);
            }
            recalculate();
            handleOneTimeAbilityOptionChanged(mMandyCheckBox.isChecked(), R.string.mandy_chances_toast);
        });
		mRerollOnesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //both Mandy and Reroll ones on at same time not supported
                mMandyCheckBox.setChecked(false);
                mSkidsOnesCheckBox.setChecked(false);
            }
            recalculate();
            handleOneTimeAbilityOptionChanged(mRerollOnesCheckBox.isChecked(), R.string.reroll_ones_chances_toast);
        });
		mSkidsOnesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //both Mandy and Reroll ones on at same time not supported
                mMandyCheckBox.setChecked(false);
                mRerollOnesCheckBox.setChecked(false);
            }
            recalculate();
            handleOneTimeAbilityOptionChanged(mSkidsOnesCheckBox.isChecked(), R.string.skids_chances_toast);
        });
		mAddOneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> recalculate());
		
		//attach setOnLongClickListener
		mDiceLabel.setOnLongClickListener(v -> {
            startHelpActivity("Dice / Difficulty");
            return true;
        });
		mToughLabel.setOnLongClickListener(v -> {
            startHelpActivity("Dice / Difficulty");
            return true;
        });
		mChanceLabel.setOnLongClickListener(v -> {
            startHelpActivity("Chances");
            return true;
        });
		mBlessCheckBox.setOnLongClickListener(v -> {
            startHelpActivity("Blessed / Cursed");
            return true;
        });
		mCurseCheckBox.setOnLongClickListener(v -> {
            startHelpActivity("Blessed / Cursed");
            return true;
        });
		mMandyCheckBox.setOnLongClickListener(v -> {
            startHelpActivity("Mandy");
            return true;
        });
		mSkidsOnesCheckBox.setOnLongClickListener(v -> {
            startHelpActivity("Skids");
            return true;
        });
		mRerollOnesCheckBox.setOnLongClickListener(v -> {
            startHelpActivity("Reroll Ones");
            return true;
        });
		mAddOneCheckBox.setOnLongClickListener(v -> {
            startHelpActivity("Add One");
            return true;
        });
		mShotgunCheckBox.setOnLongClickListener(v -> {
            startHelpActivity("Shotgun");
            return true;
        });

		//first calculation
		setSeekBarValues();
		recalculate();
		
		handleShowFirstTimeDialog();
	}

	@Override
	protected void onRestoreInstanceState(Bundle inState)
	{
		mRestoringState = true;
		try {
			super.onRestoreInstanceState(inState);
		} finally {
			mRestoringState = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		
		if (mMenuItemShowAsAction != null) {
			MenuItem mi = menu.findItem(R.id.menu_item_help);
			try {
				mMenuItemShowAsAction.invoke(mi, 1); //ifRoom
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case R.id.menu_item_feedback:
				sendFeedbackEmail();
				return true;
			case R.id.menu_item_help:
				startActivity(new Intent(this, ArkhamCalcHelp.class));
				return true;
			case R.id.menu_item_wiki:
				openWiki();
		}
		return false;
	}
	
	/**
	 * Potentially show a dialog depending on if the user has ever opened
	 * this version of the app; otherwise do nothing.
	 */
	private void handleShowFirstTimeDialog()
	{
		final SharedPreferences sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		if (sharedPrefs.getString(PREFS_KEY_FIRST_TIME_16, null) != null) return;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setMessage(R.string.first_dialog_message)
			.setNeutralButton(R.string.first_dialog_button, (dialog, which) -> {
                dialog.cancel();

                sharedPrefs.edit().putString(PREFS_KEY_FIRST_TIME_16, "a").apply();
            });
		builder.create().show();
	}	
	
	/**
	 * Start the help activity with the specified topic opened. The topic passed
	 * into this method must exist in the help.xml 'topics' array.
	 * @param topic
	 */
	private void startHelpActivity(String topic)
	{
		Intent helpIntent = new Intent(this, ArkhamCalcHelp.class);
		helpIntent.putExtra(ArkhamCalcHelp.BUNDLE_TOPIC, topic);
		startActivity(helpIntent);
	}

	private void sendFeedbackEmail()
	{
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { getResourceString(R.string.email_to) });
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResourceString(R.string.email_subject));
		emailIntent.setType("plain/text");
		try {
			startActivity(emailIntent);
		} catch (ActivityNotFoundException e) {
			showToast(getResourceString(R.string.toast_exception_email));
		}
	}
	
	private void openWiki()
	{
		Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_WIKI));
		startActivity(webIntent);
	}

	private void recalculate()
	{
		//get input
		int dice = mDiceSeekBar.getProgress() + 1;
		int tough = mToughSeekBar.getProgress() + 1;
		int chance = mChanceSeekBar.getProgress() + 1;
		boolean isBlessed = mBlessCheckBox.isChecked();
		boolean isCursed = mCurseCheckBox.isChecked();
		boolean isShotgun = mShotgunCheckBox.isChecked();
		boolean isMandy = mMandyCheckBox.isChecked();
		boolean isRerollOnes = mRerollOnesCheckBox.isChecked();
		boolean isSkidsOnes = mSkidsOnesCheckBox.isChecked();
		boolean isAddOne = mAddOneCheckBox.isChecked();

		//calculate
		Calculator calculator = new Calculator(dice, tough);
		calculator.setNumberOfChances(chance);
		calculator.setIsBlessed(isBlessed);
		calculator.setIsCursed(isCursed);
		calculator.setIsShotgun(isShotgun);
		calculator.setIsMandy(isMandy);
		calculator.setIsRerollOnes(isRerollOnes);
		calculator.setIsSkids(isSkidsOnes);
		calculator.setIsAddOne(isAddOne);
		double result = calculator.calculate();

		//format and set ui
		CalculateResultFormatter formatter = new CalculateResultFormatter(result);

		String resultString = formatter.getResultString();
		mResultTextView.setText(resultString);

		int color = formatter.getColor();
		mResultTextView.setTextColor(color);
	}

	private void setSeekBarValues()
	{
		mDiceValue.setText(Integer.toString(mDiceSeekBar.getProgress() + 1));
		mToughValue.setText(Integer.toString(mToughSeekBar.getProgress() + 1));
		mChanceValue.setText(Integer.toString(mChanceSeekBar.getProgress() + 1));
	}

	/**
	 * Show a message to the user regarding this one-time ability if user has more than one chance.
	 * Case where ability has been changed; check number of chances.
	 * Do not show a message if we are in the process of restoring state (because we already showed
	 * the message before state was saved).
	 */
	private void handleOneTimeAbilityOptionChanged(boolean isAbilityChecked, int resourceStringId)
	{
		if (isAbilityChecked && mChanceSeekBar.getProgress() > 0 && !mRestoringState) {
			showToast(getResourceString(resourceStringId));
		}
	}

	/**
	 * Show a message to the user regarding this one-time ability if user has more than one chance.
	 * Case where number of chances have changed; check if ability is selected and chances have changed
	 * from one to > 1.
	 * Do not show a message if we are in the process of restoring state (because we already showed
	 * the message before state was saved).
	 */
	private void handleOneTimeAbilityChancesChanged(boolean isAbilityChecked, int resourceStringId)
	{
		if (isAbilityChecked && mPreviousChanceValue <= 0 && mChanceSeekBar.getProgress() > 0 && !mRestoringState) {
			showToast(getResourceString(resourceStringId));
		}
	}

	private void showToast(String toastText)
	{
		Toast.makeText(getBaseContext(), toastText, Toast.LENGTH_LONG).show();
	}

	private String getResourceString(int resourceStringId)
	{
		return getResources().getString(resourceStringId);
	}

	private abstract class OnSeekBarProgressChangeListener implements OnSeekBarChangeListener
	{
		private int mPreviousProgress;
		public int getPreviousProgress(){
			return mPreviousProgress;
		}

		@Override
		public abstract void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser);

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mPreviousProgress = seekBar.getProgress();
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	}
}
