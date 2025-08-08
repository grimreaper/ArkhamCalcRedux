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

import kotlin.math.pow

/**
 * The probability engine. Using a combination of constructor and property
 * inputs, calculates the probability of a successful encounter.
 */
internal class Calculator
    (private val mDice: Int, private val mTough: Int) {
    private var mNumberOfChances = 1
    private var mIsBlessed = false
    private var mIsCursed = false
    private var mIsShotgun = false
    private var mIsMandy = false
    private var mIsRerollOnes = false
    private var mIsSkids = false
    private var mIsAddOne = false

    fun setNumberOfChances(value: Int) {
        mNumberOfChances = value
    }

    fun setIsBlessed(value: Boolean) {
        mIsBlessed = value
    }

    fun setIsCursed(value: Boolean) {
        mIsCursed = value
    }

    fun setIsShotgun(value: Boolean) {
        mIsShotgun = value
    }

    fun setIsMandy(value: Boolean) {
        mIsMandy = value
    }

    fun setIsRerollOnes(value: Boolean) {
        mIsRerollOnes = value
    }

    fun setIsSkids(value: Boolean) {
        mIsSkids = value
    }

    fun setIsAddOne(value: Boolean) {
        mIsAddOne = value
    }

    constructor(dice: Int, tough: Int, isBlessed: Boolean, isCursed: Boolean) : this(dice, tough) {
        mIsBlessed = isBlessed
        mIsCursed = isCursed
    }

    /**
     * Calculate probability of success given all calculator properties
     */
    fun calculate(): Double {
        var probSuccess = 0.0

        probSuccess += baseCalc(mDice, mTough)

        var probMandySuccess = 0.0
        var probRerollOnesSuccess = 0.0
        if (mIsMandy) {
            probMandySuccess = this.probMandyReroll
        } else if (mIsRerollOnes) {
            probRerollOnesSuccess = getProbRerollOnes(1)
        } else if (mIsSkids) {
            probRerollOnesSuccess = getProbRerollOnes(2)
        }

        return probSuccessWithChances(
            probSuccess,
            probMandySuccess,
            probRerollOnesSuccess,
            mNumberOfChances
        )
    }

    private fun baseCalc(totalDice: Int, totalToughness: Int): Double {
        var probSuccess = 0.0

        for (i in totalToughness..totalDice) {
            probSuccess += getProbExactSuccess(
                totalDice, i,
                this.probOneSuccess
            )
        }
        if (mIsShotgun) {
            probSuccess += handleIsShotgun(totalDice, totalToughness)
        }
        return probSuccess
    }

    private val probMandyReroll: Double
        get() {
            var probSuccessMandy = 0.0
            for (sixSuccesses in 0..<mTough) {
                val probExactSixes: Double =
                    getProbExactSuccess(
                        mDice,
                        sixSuccesses,
                        probSix
                    )
                val sixSuccessValue =
                    if (mIsShotgun) 2 * sixSuccesses else sixSuccesses

                var nonSixSuccesses = 0
                while (nonSixSuccesses + sixSuccessValue < mTough) {
                    val probFirstRoll: Double =
                        probExactSixes * getProbExactSuccess(
                            mDice - sixSuccesses,
                            nonSixSuccesses,
                            this.probSuccessWithoutSix
                        )
                    probSuccessMandy += probFirstRoll * baseCalc(
                        mDice - sixSuccesses - nonSixSuccesses,
                        mTough - sixSuccessValue - nonSixSuccesses
                    )
                    nonSixSuccesses++
                }
            }
            return probSuccessMandy
        }

    private fun getProbRerollOnes(numberOfOnesRerolls: Int): Double {
        var probSuccessRerollOnes = 0.0
        for (sixSuccesses in 0..<mTough) {
            val probExactSixes: Double = getProbExactSuccess(
                mDice, sixSuccesses,
                probSix
            )
            val sixSuccessValue = if (mIsShotgun) 2 * sixSuccesses else sixSuccesses

            var nonSixSuccesses = 0
            while (nonSixSuccesses + sixSuccessValue < mTough) {
                val probFirstRollSuccesses: Double = probExactSixes * getProbExactSuccess(
                    mDice - sixSuccesses, nonSixSuccesses,
                    this.probSuccessWithoutSix
                )
                for (ones in 1..mDice - nonSixSuccesses - sixSuccesses) {
                    val probExactOnes: Double = getProbExactSuccess(
                        mDice - nonSixSuccesses - sixSuccesses, ones,
                        this.probOneWithoutSuccesses
                    )
                    val probFirstRoll = probFirstRollSuccesses * probExactOnes
                    probSuccessRerollOnes += probFirstRoll * baseCalc(
                        ones * numberOfOnesRerolls,
                        mTough - sixSuccessValue - nonSixSuccesses
                    )
                }
                nonSixSuccesses++
            }
        }

        return probSuccessRerollOnes
    }

    private fun handleIsShotgun(totalDice: Int, totalToughness: Int): Double {
        var probSuccessShotgun = 0.0
        //go from one six to either all sixes or to the toughness, whichever comes first.
        //once we get to the toughness (i.e. on a 5 to do 3, once we get to three sixes), 
        //we've already counted in the base calculation.
        var i = 1
        while (i < totalToughness && i <= totalDice) {
            val exactSixes: Double = getProbExactSuccess(
                totalDice, i,
                probSix
            )
            val remainingSuccessesRequired = totalToughness - 2 * i
            var j = 0
            while (i + j < totalToughness && i + j <= totalDice) {
                //j represents the number of non-sixes that are successes. Don't count sixes + successes
                //that are >= toughness - those have already been counted in base calc
                if (j >= remainingSuccessesRequired) {
                    //i.e. the non-six successes (j) are enough to win. Count all the ways to roll exactly that many
                    //sixes and with the remaining dice roll that many non-six successes.
                    probSuccessShotgun += exactSixes * getProbExactSuccess(
                        totalDice - i, j,
                        this.probSuccessWithoutSix
                    )
                }
                j++
            }
            i++
        }
        return probSuccessShotgun
    }

    private val probSuccessWithoutSix: Double
        get() = (this.probOneSuccess - probSix) * 6 / 5

    private val probOneWithoutSuccesses: Double
        get() {
            val numerator = 1.0
            var denominator =
                4.0 //without any perks, prob of one without success is 1/4 (1, 2, 3, 4)
            if (mIsBlessed) {
                denominator--
            }
            if (mIsCursed) {
                denominator++
            }
            if (mIsAddOne) {
                denominator--
            }
            return numerator / denominator
        }

    private val probOneSuccess: Double
        get() {
            val denominator = 6.0
            var numerator =
                2.0 //without any perks, prob of success is 2/6 (rolling a 5 or rolling a 6)

            if (mIsBlessed) {
                numerator++
            }
            if (mIsCursed) {
                numerator--
            }
            if (mIsAddOne) {
                numerator++
            }
            return numerator / denominator
        }

    companion object {
        private val probSix: Double
            get() = 1.0 / 6

        private fun probSuccessWithChances(
            probSuccessOneChance: Double,
            probMandySuccess: Double,
            probRerollOnesSuccess: Double,
            numberOfChances: Int
        ): Double {
            //mandy can only be used once - if you have any other chances, they won't include Mandy
            val probFailureFirstChance =
                1 - probSuccessOneChance - probMandySuccess - probRerollOnesSuccess
            val probFailureOtherChances = 1 - probSuccessOneChance

            //note, numberOfChances always > 0, the exponent will never be < 0
            val probFailureAllChances =
                probFailureFirstChance * probFailureOtherChances.pow((numberOfChances - 1).toDouble())
            return 1 - probFailureAllChances
        }

        private fun getProbExactSuccess(
            totalDice: Int,
            exactSuccesses: Int,
            probOneSuccess: Double
        ): Double {
            return MathHelper.nCr(
                totalDice,
                exactSuccesses
            ) * probOneSuccess.pow(exactSuccesses.toDouble()) * (1 - probOneSuccess).pow((totalDice - exactSuccesses).toDouble())
        }
    }
}
