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

/**
 * The probability engine. Using a combination of constructor and property
 * inputs, calculates the probability of a successful encounter.
 */
public final class Calculator
{
    private int mDice;
    private int mTough;
    private int mNumberOfChances;
    private boolean mIsBlessed;
    private boolean mIsCursed;
    private boolean mIsShotgun;
    private boolean mIsMandy;
    private boolean mIsRerollOnes;
    private boolean mIsSkids;
    private boolean mIsAddOne;

    public void setNumberOfChances(int value)
    {
        mNumberOfChances = value;
    }

    public void setIsBlessed(boolean value)
    {
        mIsBlessed = value;
    }

    public void setIsCursed(boolean value)
    {
        mIsCursed = value;
    }

    public void setIsShotgun(boolean value)
    {
        mIsShotgun = value;
    }

    public void setIsMandy(boolean value)
    {
        mIsMandy = value;
    }

    public void setIsRerollOnes(boolean value)
    {
        mIsRerollOnes = value;
    }

    public void setIsSkids(boolean value)
    {
        mIsSkids = value;
    }

    public void setIsAddOne(boolean value)
    {
        mIsAddOne = value;
    }
    
    public Calculator(int dice, int tough)
    {
        mDice = dice;
        mTough = tough;
        mNumberOfChances = 1;
    }

    public Calculator(int dice, int tough, boolean isBlessed, boolean isCursed)
    {
        this(dice, tough);
        
        mIsBlessed = isBlessed;
        mIsCursed = isCursed;
    }

    /**
     * Calculate probability of success given all calculator properties
     */
    public double calculate()
    {
        double probSuccess = 0.0;

        probSuccess += baseCalc(mDice, mTough);

        double probMandySuccess = 0.0;
        double probRerollOnesSuccess = 0.0;
        if (mIsMandy) {
            probMandySuccess = getProbMandyReroll();
        } else if (mIsRerollOnes) {
            probRerollOnesSuccess = getProbRerollOnes(1);
        } else if (mIsSkids) {
            probRerollOnesSuccess = getProbRerollOnes(2);            
        }

        return probSuccessWithChances(probSuccess, probMandySuccess, probRerollOnesSuccess, mNumberOfChances);
    }

    private double baseCalc(int totalDice, int totalToughness)
    {
        double probSuccess = 0.0;

        for (int i = totalToughness; i <= totalDice; i++) {
            probSuccess += getProbExactSuccess(totalDice, i, getProbOneSuccess());
        }
        if (mIsShotgun) {
            probSuccess += handleIsShotgun(totalDice, totalToughness);
        }
        return probSuccess;
    }

    private double getProbMandyReroll()
    {
        double probSuccessMandy = 0.0;
        for (int sixSuccesses = 0; sixSuccesses < mTough; sixSuccesses++) {
            double probExactSixes = getProbExactSuccess(mDice, sixSuccesses, getProbSix());
            int sixSuccessValue = mIsShotgun ? 2 * sixSuccesses : sixSuccesses;

            for (int nonSixSuccesses = 0; nonSixSuccesses + sixSuccessValue < mTough; nonSixSuccesses++) {
                double probFirstRoll = probExactSixes * getProbExactSuccess(mDice - sixSuccesses, nonSixSuccesses, getProbSuccessWithoutSix());
                probSuccessMandy += probFirstRoll * baseCalc(mDice - sixSuccesses - nonSixSuccesses, mTough - sixSuccessValue - nonSixSuccesses);
            }
        }
        return probSuccessMandy;
    }

    private double getProbRerollOnes(int numberOfOnesRerolls)
    {
        double probSuccessRerollOnes = 0.0;
        for (int sixSuccesses = 0; sixSuccesses < mTough; sixSuccesses++) {
            double probExactSixes = getProbExactSuccess(mDice, sixSuccesses, getProbSix());
            int sixSuccessValue = mIsShotgun ? 2 * sixSuccesses : sixSuccesses;

            for (int nonSixSuccesses = 0; nonSixSuccesses + sixSuccessValue < mTough; nonSixSuccesses++) {
                double probFirstRollSuccesses = probExactSixes * getProbExactSuccess(mDice - sixSuccesses, nonSixSuccesses, getProbSuccessWithoutSix());
                for (int ones = 1; ones <= mDice - nonSixSuccesses - sixSuccesses; ones++) {
                    double probExactOnes = getProbExactSuccess(mDice - nonSixSuccesses - sixSuccesses, ones, getProbOneWithoutSuccesses());
                    double probFirstRoll = probFirstRollSuccesses * probExactOnes;
                    probSuccessRerollOnes += probFirstRoll * baseCalc(ones * numberOfOnesRerolls, mTough - sixSuccessValue - nonSixSuccesses);
                }
            }
        }

        return probSuccessRerollOnes;
    }

    private double handleIsShotgun(int totalDice, int totalToughness)
    {
        double probSuccessShotgun = 0.0;
        //go from one six to either all sixes or to the toughness, whichever comes first.
        //once we get to the toughness (i.e. on a 5 to do 3, once we get to three sixes), 
        //we've already counted in the base calculation.
        for (int i = 1; i < totalToughness && i <= totalDice; i++) {
            double exactSixes = getProbExactSuccess(totalDice, i, getProbSix());
            int remainingSuccessesRequired = totalToughness - 2 * i;
            for (int j = 0; i + j < totalToughness && i + j <= totalDice; j++) {
                //j represents the number of non-sixes that are successes. Don't count sixes + successes
                //that are >= toughness - those have already been counted in base calc
                if (j >= remainingSuccessesRequired) {
                    //i.e. the non-six successes (j) are enough to win. Count all the ways to roll exactly that many
                    //sixes and with the remaining dice roll that many non-six successes.
                    probSuccessShotgun += exactSixes * getProbExactSuccess(totalDice - i, j, getProbSuccessWithoutSix());
                }
            }
        }
        return probSuccessShotgun;
    }

    private double getProbSuccessWithoutSix()
    {
        return (getProbOneSuccess() - getProbSix()) * 6 / 5;
    }

    private double getProbOneWithoutSuccesses()
    {
        final double numerator = 1;
        double denominator = 4; //without any perks, prob of one without success is 1/4 (1, 2, 3, 4)
        if (mIsBlessed){
            denominator--;
        }
        if (mIsCursed){
            denominator++;
        }
        if (mIsAddOne) {
            denominator--;
        }
        return numerator / denominator;
    }

    private double getProbOneSuccess()
    {
        final double denominator = 6;
        double numerator = 2; //without any perks, prob of success is 2/6 (rolling a 5 or rolling a 6)

        if (mIsBlessed) {
            numerator++;
        }
        if (mIsCursed) {
            numerator--;
        }
        if (mIsAddOne) {
            numerator++;
        }
        return numerator / denominator;
    }

    private static double getProbSix()
    {
        return (double)1 / 6;
    }

    private static double probSuccessWithChances(double probSuccessOneChance, double probMandySuccess, double probRerollOnesSuccess, int numberOfChances)
    {
        //mandy can only be used once - if you have any other chances, they won't include Mandy
        double probFailureFirstChance = 1 - probSuccessOneChance - probMandySuccess - probRerollOnesSuccess;
        double probFailureOtherChances = 1 - probSuccessOneChance;

        //note, numberOfChances always > 0, the exponent will never be < 0
        double probFailureAllChances = probFailureFirstChance * Math.pow(probFailureOtherChances, numberOfChances - 1);
        return 1 - probFailureAllChances;
    }

    private static double getProbExactSuccess(int totalDice, int exactSuccesses, double probOneSuccess)
    {
        return MathHelper.nCr(totalDice, exactSuccesses) * Math.pow(probOneSuccess, exactSuccesses) * Math.pow(1 - probOneSuccess, totalDice - exactSuccesses);
    }
}
