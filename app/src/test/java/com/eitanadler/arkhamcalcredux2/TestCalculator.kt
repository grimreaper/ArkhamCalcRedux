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

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Random

public class TestCalculator {
    private var random: Random? = null

    @Before
    fun setUp() {
        random = Random()
    }

    @Test
    fun testOneOneOneSimpleCase() {
        Assert.assertEquals(
            0.3333,
            Calculator(
                1,
                1,
                mNumberOfChances = 1,
            ).calculate(),
            EPS,
        )
    }

    @Test
    fun testOneOneOneBlessCase() {
        Assert.assertEquals(
            0.50,
            Calculator(
                1,
                1,
                mNumberOfChances = 1,
                mIsBlessed = true,
            ).calculate(),
            EPS,
        )
    }

    @Test
    fun testCalculate() {
        val requiredDice = 8
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        Assert.assertEquals(
            percentageWins,
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
            ).calculate(),
            EPS,
        )
    }

    @Test
    fun testCalculateBlessed() {
        val requiredDice = 6
        val requiredSuccesses = 2

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 4) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        Assert.assertEquals(
            percentageWins,
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = true,
                mIsCursed = false,
            ).calculate(),
            EPS,
        )
    }

    @Test
    fun testCalculateBlessedProperty() {
        val requiredDice = 6
        val requiredSuccesses = 2

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 4) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val blessedCalc =
            Calculator(
                mDice = requiredDice,
                mTough = requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = true,
                mIsCursed = false,
                mIsShotgun = false,
                mIsMandy = false,
                mIsRerollOnes = false,
                mIsSkids = false,
                mIsAddOne = false,
            )
        Assert.assertEquals(percentageWins, blessedCalc.calculate(), EPS)
    }

    @Test
    fun testCalculateCursed() {
        val requiredDice = 3
        val requiredSuccesses = 1

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 6) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        Assert.assertEquals(
            percentageWins,
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = true,
            ).calculate(),
            EPS,
        )
    }

    @Test
    fun testCalculateCursedProperty() {
        val requiredDice = 3
        val requiredSuccesses = 1

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 6) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val calc =
            Calculator(
                mDice = requiredDice,
                mTough = requiredSuccesses,
                mNumberOfChances = 1,
                mIsCursed = true,
            )
        Assert.assertEquals(percentageWins, calc.calculate(), EPS)
    }

    @Test
    fun testCalculateNoSuccesses() {
        Assert.assertEquals(
            0.0,
            Calculator(
                2,
                3,
                mNumberOfChances = 1,
            ).calculate(),
            0.0,
        )
    }

    @Test
    fun testCalculateChances() {
        // XXX
        val requiredDice = 7
        val requiredSuccesses = 4
        val requiredChances = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            for (j in 0..<requiredChances) {
                var totalSuccesses = 0
                (0..<requiredDice).forEach { k ->
                    val dieValue = this.randomDieValue
                    if (dieValue >= 5) {
                        totalSuccesses++
                    }
                }
                if (totalSuccesses >= requiredSuccesses) {
                    totalWins++
                    break
                }
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val calc =
            Calculator(
                mDice = requiredDice,
                mTough = requiredSuccesses,
                mNumberOfChances = requiredChances,
            )
        Assert.assertEquals(percentageWins, calc.calculate(), EPS)
    }

    @Test
    fun testCalculateShotgun() {
        val requiredDice = 6
        val requiredSuccesses = 4

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses += 2
                } else if (dieValue == 5) {
                    totalSuccesses += 1
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsShotgun = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testCalculateShotgunBlessed() {
        val requiredDice = 6
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses += 2
                } else if (dieValue >= 4) {
                    totalSuccesses += 1
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = true,
                mIsShotgun = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testCalculateShotgunCursed() {
        val requiredDice = 6
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses += 2
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = true,
                mIsShotgun = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testCalculateShotgunHuge() {
        val requiredDice = 12
        val requiredSuccesses = 5

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses += 2
                } else if (dieValue == 5) {
                    totalSuccesses += 1
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsShotgun = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testCalculateShotgunOneSuccess() {
        val shotgunCalc =
            Calculator(
                4,
                1,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsShotgun = true,
            )
        Assert.assertEquals(
            Calculator(
                4,
                1,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
            ).calculate(),
            shotgunCalc.calculate(),
            0.0,
        )
    }

    @Test
    fun testCalculateShotgunImpossible() {
        val requiredDice = 2
        val requiredSuccesses = 5

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses += 2
                } else if (dieValue == 5) {
                    totalSuccesses += 1
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsShotgun = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testCalculateShotgunAlmostImpossible() {
        val requiredDice = 3
        val requiredSuccesses = 6

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses += 2
                } else if (dieValue == 5) {
                    totalSuccesses += 1
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsShotgun = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testCalcMandy() {
        val requiredDice = 6
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++
                }
            }
            val firstRollSuccesses = totalSuccesses
            (0..<requiredDice - firstRollSuccesses).forEach { j ->
                val rerollDieValue = this.randomDieValue
                if (rerollDieValue == 5 || rerollDieValue == 6) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsMandy = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testCalcMandyCursed() {
        val requiredDice = 7
        val requiredSuccesses = 4

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses++
                }
            }
            val firstRollSuccesses = totalSuccesses
            (0..<requiredDice - firstRollSuccesses).forEach { j ->
                val rerollDieValue = this.randomDieValue
                if (rerollDieValue == 6) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = true,
                mIsMandy = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testCalcMandyShotgun() {
        val requiredDice = 5
        val requiredSuccesses = 4

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var secondRollDice = requiredDice
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses += 2
                    secondRollDice--
                } else if (dieValue == 5) {
                    totalSuccesses++
                    secondRollDice--
                }
            }
            (0..<secondRollDice).forEach { j ->
                val rerollDieValue = this.randomDieValue
                if (rerollDieValue == 6) {
                    totalSuccesses += 2
                } else if (rerollDieValue == 5) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsMandy = true,
                mIsShotgun = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testMandyImpossible() {
        val requiredDice = 2
        val requiredSuccesses = 3

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsMandy = true,
            )

        Assert.assertEquals(0.0, calculator.calculate(), 0.0)
    }

    @Test
    fun testMandySameAsChances() {
        val requiredDice = 3
        val requiredSuccesses = 1

        val twoChancesCalc =
            Calculator(
                mDice = requiredDice,
                mTough = requiredSuccesses,
                mNumberOfChances = 2,
                mIsBlessed = false,
                mIsCursed = false,
                mIsShotgun = false,
                mIsMandy = false,
                mIsRerollOnes = false,
                mIsSkids = false,
                mIsAddOne = false,
            )

        val mandyCalc =
            Calculator(
                mDice = requiredDice,
                mTough = requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsShotgun = false,
                mIsMandy = true,
                mIsRerollOnes = false,
                mIsSkids = false,
                mIsAddOne = false,
            )
        Assert.assertEquals(twoChancesCalc.calculate(), mandyCalc.calculate(), EPS)
    }

    @Test
    fun testMandyMultipleChances() {
        val requiredDice = 7
        val requiredSuccesses = 5

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++
                }
            }
            val firstRollSuccesses = totalSuccesses
            (0..<requiredDice - firstRollSuccesses).forEach { j ->
                val rerollDieValue = this.randomDieValue
                if (rerollDieValue == 5 || rerollDieValue == 6) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            } else { // try again w/o mandy's ability
                totalSuccesses = 0
                (0..<requiredDice).forEach { j ->
                    val dieValue = this.randomDieValue
                    if (dieValue >= 5) {
                        totalSuccesses++
                    }
                }
                if (totalSuccesses >= requiredSuccesses) {
                    totalWins++
                }
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 2,
                mIsBlessed = false,
                mIsCursed = false,
                mIsMandy = true,
            )

        Assert.assertEquals(percentageWins, calculator.calculate(), EPS) // two chances
    }

    @Test
    fun testRerollOnes() {
        val requiredDice = 8
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            (0..<numberOfOnes).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsRerollOnes = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testSkidsOnes() {
        val requiredDice = 8
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            (0..<numberOfOnes * 2).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = false,
                mIsCursed = false,
                mIsSkids = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testRerollOnesCursed() {
        val requiredDice = 6
        val requiredSuccesses = 2

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 6) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            (0..<numberOfOnes).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 6) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsCursed = true,
                mIsRerollOnes = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testRerollOnesBlessed() {
        val requiredDice = 3
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 4) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            (0..<numberOfOnes).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 4) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = true,
                mIsRerollOnes = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testSkidsOnesBlessed() {
        val requiredDice = 3
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 4) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            (0..<numberOfOnes * 2).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 4) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = true,
                mIsSkids = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testRerollOnesMultipleChances() {
        val requiredDice = 7
        val requiredSuccesses = 5

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            (0..<numberOfOnes).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++
                }
            }

            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            } else { // try again w/o reroll ability
                totalSuccesses = 0
                (0..<requiredDice).forEach { j ->
                    val dieValue = this.randomDieValue
                    if (dieValue >= 5) {
                        totalSuccesses++
                    }
                }
                if (totalSuccesses >= requiredSuccesses) {
                    totalWins++
                }
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 2,
                mIsRerollOnes = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS) // two chances
    }

    @Test
    fun testRerollOnesShotgun() {
        val requiredDice = 10
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                when (dieValue) {
                    5 -> {
                        totalSuccesses++
                    }

                    6 -> {
                        totalSuccesses += 2
                    }

                    1 -> {
                        numberOfOnes++
                    }
                }
            }
            (0..<numberOfOnes).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 5) {
                    totalSuccesses++
                } else if (dieValue == 6) {
                    totalSuccesses += 2
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsRerollOnes = true,
                mIsShotgun = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testSkidsOnesAlmostImpossible() {
        val requiredDice = 1
        val requiredSuccesses = 2

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            (0..<numberOfOnes * 2).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        Assert.assertTrue(percentageWins > 0)
        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsSkids = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    // 	Note - this functionality is not currently supported (reroll ones skill + mandy ability)
    // 	public void testRerollOnesMandy() {
    // 		int requiredDice = 5;
    // 		int requiredSuccesses = 4;
    // 		
    // 		int totalWins = 0;
    // 		for (int i = 0; i < NUMBER_ITERATIONS; i++) {
    // 			int totalSuccesses = 0;
    // 			int numberOfOnes = 0;
    // 			for (int j = 0; j < requiredDice; j++) {
    // 				int dieValue = getRandomDieValue();
    // 				if (dieValue >= 4) {
    // 					totalSuccesses++;
    // 				} else if (dieValue == 1) {
    // 					numberOfOnes++;
    // 				}
    // 			}
    // 			//reroll ones part
    // 			for (int j = 0; j < numberOfOnes; j++) {
    // 				int dieValue = getRandomDieValue();
    // 				if (dieValue >= 4) {
    // 					totalSuccesses++;
    // 				}				
    // 			}
    // 			//mandy part
    // 			int firstRollSuccesses = totalSuccesses;
    // 			for (int j = 0; j < requiredDice - firstRollSuccesses; j++) {
    // 				int dieValue = getRandomDieValue();
    // 				if (dieValue >= 4) {
    // 					totalSuccesses++;
    // 				}					
    // 			}
    // 			if (totalSuccesses >= requiredSuccesses) {
    // 				totalWins++;
    // 			}
    // 		}
    // 		
    // 		double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
    // 		Calculator calculator = new Calculator(requiredDice, requiredSuccesses, true, false);
    // 		calculator.setIsRerollOnes(true);
    // 		calculator.setIsMandy(true);
    // 		assertEquals(percentageWins, calculator.calculate(), EPS);
    // 	}
    @Test
    fun testAddOneComparedToBlessed() {
        val requiredDice = 5
        val requiredSuccesses = 4
        val blessedCalc =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = true,
            )
        val addOneCalc =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsAddOne = true,
            )
        Assert.assertEquals(blessedCalc.calculate(), addOneCalc.calculate(), 0.0)
    }

    @Test
    fun testAddOneAndBlessed() {
        val requiredDice = 3
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 3) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = true,
                mIsAddOne = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testAddOneCursed() {
        val requiredDice = 8
        val requiredSuccesses = 4

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsCursed = true,
                mIsAddOne = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testAddOneShotgunCursed() {
        val requiredDice = 8
        val requiredSuccesses = 4

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses += 2
                } else if (dieValue == 5) {
                    // even though a rolled 5 becomes a 6 for the purpose of checking successes, it doesn't count as a shotgun.
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsCursed = true,
                mIsShotgun = true,
                mIsAddOne = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testRerollOnesAddOneBlessed() {
        val requiredDice = 6
        val requiredSuccesses = 6

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 3) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            (0..<numberOfOnes).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 3) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsBlessed = true,
                mIsRerollOnes = true,
                mIsAddOne = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    @Test
    fun testCalcMandyCursedAddOne() {
        val requiredDice = 7
        val requiredSuccesses = 5

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            (0..<requiredDice).forEach { j ->
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                }
            }
            val firstRollSuccesses = totalSuccesses
            (0..<requiredDice - firstRollSuccesses).forEach { j ->
                val rerollDieValue = this.randomDieValue
                if (rerollDieValue >= 5) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator =
            Calculator(
                requiredDice,
                requiredSuccesses,
                mNumberOfChances = 1,
                mIsCursed = true,
                mIsMandy = true,
                mIsAddOne = true,
            )
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    private val randomDieValue: Int
        get() = random!!.nextInt(6) + 1

    companion object {
        private const val NUMBER_ITERATIONS = 10000000
        private const val EPS = 0.001
    }
}
