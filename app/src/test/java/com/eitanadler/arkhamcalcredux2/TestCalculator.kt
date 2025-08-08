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

import junit.framework.TestCase
import org.junit.Assert
import java.util.Random

public class TestCalculator : TestCase() {
    private var random: Random? = null

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        random = Random()
    }

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
            Calculator(requiredDice, requiredSuccesses, isBlessed = false, isCursed = false).calculate(),
            EPS
        )
    }

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
            Calculator(requiredDice, requiredSuccesses, isBlessed = true, isCursed = false).calculate(),
            EPS
        )
    }

    fun testCalculateBlessedProperty() {
        val requiredDice = 6
        val requiredSuccesses = 2

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
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
        val calc = Calculator(requiredDice, requiredSuccesses)
        calc.setIsBlessed(true)
        Assert.assertEquals(percentageWins, calc.calculate(), EPS)
    }

    fun testCalculateCursed() {
        val requiredDice = 3
        val requiredSuccesses = 1

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
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
            Calculator(requiredDice, requiredSuccesses, isBlessed = false, isCursed = true).calculate(),
            EPS
        )
    }

    fun testCalculateCursedProperty() {
        val requiredDice = 3
        val requiredSuccesses = 1

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
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
        val calc = Calculator(requiredDice, requiredSuccesses)
        calc.setIsCursed(true)
        Assert.assertEquals(percentageWins, calc.calculate(), EPS)
    }

    fun testCalculateNoSuccesses() {
        Assert.assertEquals(0.0, Calculator(2, 3, isBlessed = false, isCursed = false).calculate(), 0.0)
    }

    fun testCalculateChances() {
        val requiredDice = 7
        val requiredSuccesses = 4
        val requiredChances = 3

        var totalWins = 0
        for (i in 0..<NUMBER_ITERATIONS) for (j in 0..<requiredChances) {
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

        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS
        val calc = Calculator(requiredDice, requiredSuccesses)
        calc.setNumberOfChances(requiredChances)
        Assert.assertEquals(percentageWins, calc.calculate(), EPS)
    }

    fun testCalculateShotgun() {
        val requiredDice = 6
        val requiredSuccesses = 4

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsShotgun(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = true,
            isCursed = false
        )
        calculator.setIsShotgun(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testCalculateShotgunCursed() {
        val requiredDice = 6
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = true
        )
        calculator.setIsShotgun(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testCalculateShotgunHuge() {
        val requiredDice = 12
        val requiredSuccesses = 5

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsShotgun(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testCalculateShotgunOneSuccess() {
        val shotgunCalc = Calculator(4, 1, isBlessed = false, isCursed = false)
        shotgunCalc.setIsShotgun(true)
        Assert.assertEquals(
            Calculator(4, 1, isBlessed = false, isCursed = false).calculate(),
            shotgunCalc.calculate(),
            0.0
        )
    }

    fun testCalculateShotgunImpossible() {
        val requiredDice = 2
        val requiredSuccesses = 5

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsShotgun(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testCalculateShotgunAlmostImpossible() {
        val requiredDice = 3
        val requiredSuccesses = 6

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsShotgun(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testCalcMandy() {
        val requiredDice = 6
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++
                }
            }
            val firstRollSuccesses = totalSuccesses
            for (j in 0..<requiredDice - firstRollSuccesses) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsMandy(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testCalcMandyCursed() {
        val requiredDice = 7
        val requiredSuccesses = 4

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses++
                }
            }
            val firstRollSuccesses = totalSuccesses
            for (j in 0..<requiredDice - firstRollSuccesses) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = true
        )
        calculator.setIsMandy(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testCalcMandyShotgun() {
        val requiredDice = 5
        val requiredSuccesses = 4

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var secondRollDice = requiredDice
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses += 2
                    secondRollDice--
                } else if (dieValue == 5) {
                    totalSuccesses++
                    secondRollDice--
                }
            }
            for (j in 0..<secondRollDice) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsMandy(true)
        calculator.setIsShotgun(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testMandyImpossible() {
        val requiredDice = 2
        val requiredSuccesses = 3

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsMandy(true)

        Assert.assertEquals(0.0, calculator.calculate(), 0.0)
    }

    fun testMandySameAsChances() {
        val requiredDice = 3
        val requiredSuccesses = 1

        val twoChancesCalc = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        twoChancesCalc.setNumberOfChances(2)
        val mandyCalc = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        mandyCalc.setIsMandy(true)
        Assert.assertEquals(twoChancesCalc.calculate(), mandyCalc.calculate(), EPS)
    }

    fun testMandyMultipleChances() {
        val requiredDice = 7
        val requiredSuccesses = 5

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++
                }
            }
            val firstRollSuccesses = totalSuccesses
            for (j in 0..<requiredDice - firstRollSuccesses) {
                val rerollDieValue = this.randomDieValue
                if (rerollDieValue == 5 || rerollDieValue == 6) {
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            } else { //try again w/o mandy's ability
                totalSuccesses = 0
                for (j in 0..<requiredDice) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setNumberOfChances(2)
        calculator.setIsMandy(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS) //two chances
    }

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
        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsRerollOnes(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testSkidsOnes() {
        val requiredDice = 8
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            for (j in 0..<numberOfOnes * 2) {
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
        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsSkids(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testRerollOnesCursed() {
        val requiredDice = 6
        val requiredSuccesses = 2

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue >= 6) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            for (j in 0..<numberOfOnes) {
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
        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = true
        )
        calculator.setIsRerollOnes(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testRerollOnesBlessed() {
        val requiredDice = 3
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue >= 4) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            for (j in 0..<numberOfOnes) {
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
        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = true,
            isCursed = false
        )
        calculator.setIsRerollOnes(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testSkidsOnesBlessed() {
        val requiredDice = 3
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue >= 4) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            for (j in 0..<numberOfOnes * 2) {
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
        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = true,
            isCursed = false
        )
        calculator.setIsSkids(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testRerollOnesMultipleChances() {
        val requiredDice = 7
        val requiredSuccesses = 5

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            for (j in 0..<numberOfOnes) {
                val dieValue = this.randomDieValue
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++
                }
            }

            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            } else { //try again w/o reroll ability
                totalSuccesses = 0
                for (j in 0..<requiredDice) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setNumberOfChances(2)
        calculator.setIsRerollOnes(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS) //two chances
    }

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
        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsRerollOnes(true)
        calculator.setIsShotgun(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testSkidsOnesAlmostImpossible() {
        val requiredDice = 1
        val requiredSuccesses = 2

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            for (j in 0..<numberOfOnes * 2) {
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
        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        calculator.setIsSkids(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    //	Note - this functionality is not currently supported (reroll ones skill + mandy ability)
    //	public void testRerollOnesMandy() {
    //		int requiredDice = 5;
    //		int requiredSuccesses = 4;
    //		
    //		int totalWins = 0;
    //		for (int i = 0; i < NUMBER_ITERATIONS; i++) {
    //			int totalSuccesses = 0;
    //			int numberOfOnes = 0;
    //			for (int j = 0; j < requiredDice; j++) {
    //				int dieValue = getRandomDieValue();
    //				if (dieValue >= 4) {
    //					totalSuccesses++;
    //				} else if (dieValue == 1) {
    //					numberOfOnes++;
    //				}
    //			}
    //			//reroll ones part
    //			for (int j = 0; j < numberOfOnes; j++) {
    //				int dieValue = getRandomDieValue();
    //				if (dieValue >= 4) {
    //					totalSuccesses++;
    //				}				
    //			}
    //			//mandy part
    //			int firstRollSuccesses = totalSuccesses;
    //			for (int j = 0; j < requiredDice - firstRollSuccesses; j++) {
    //				int dieValue = getRandomDieValue();
    //				if (dieValue >= 4) {
    //					totalSuccesses++;
    //				}					
    //			}
    //			if (totalSuccesses >= requiredSuccesses) {
    //				totalWins++;
    //			}
    //		}
    //		
    //		double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
    //		Calculator calculator = new Calculator(requiredDice, requiredSuccesses, true, false);
    //		calculator.setIsRerollOnes(true);
    //		calculator.setIsMandy(true);
    //		assertEquals(percentageWins, calculator.calculate(), EPS);
    //	}
    fun testAddOneComparedToBlessed() {
        val requiredDice = 5
        val requiredSuccesses = 4
        val blessedCalc = Calculator(requiredDice, requiredSuccesses,
            isBlessed = true,
            isCursed = false
        )
        val addOneCalc = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = false
        )
        addOneCalc.setIsAddOne(true)
        Assert.assertEquals(blessedCalc.calculate(), addOneCalc.calculate(), 0.0)
    }

    fun testAddOneAndBlessed() {
        val requiredDice = 3
        val requiredSuccesses = 3

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = true,
            isCursed = false
        )
        calculator.setIsAddOne(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testAddOneCursed() {
        val requiredDice = 8
        val requiredSuccesses = 4

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = true
        )
        calculator.setIsAddOne(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testAddOneShotgunCursed() {
        val requiredDice = 8
        val requiredSuccesses = 4

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue == 6) {
                    totalSuccesses += 2
                } else if (dieValue == 5) {
                    //even though a rolled 5 becomes a 6 for the purpose of checking successes, it doesn't count as a shotgun.
                    totalSuccesses++
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++
            }
        }
        val percentageWins: Double = totalWins.toDouble() / NUMBER_ITERATIONS

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = true
        )
        calculator.setIsShotgun(true)
        calculator.setIsAddOne(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testRerollOnesAddOneBlessed() {
        val requiredDice = 6
        val requiredSuccesses = 6

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            var numberOfOnes = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue >= 3) {
                    totalSuccesses++
                } else if (dieValue == 1) {
                    numberOfOnes++
                }
            }
            for (j in 0..<numberOfOnes) {
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
        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = true,
            isCursed = false
        )
        calculator.setIsRerollOnes(true)
        calculator.setIsAddOne(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    fun testCalcMandyCursedAddOne() {
        val requiredDice = 7
        val requiredSuccesses = 5

        var totalWins = 0
        (0..<NUMBER_ITERATIONS).forEach { i ->
            var totalSuccesses = 0
            for (j in 0..<requiredDice) {
                val dieValue = this.randomDieValue
                if (dieValue >= 5) {
                    totalSuccesses++
                }
            }
            val firstRollSuccesses = totalSuccesses
            for (j in 0..<requiredDice - firstRollSuccesses) {
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

        val calculator = Calculator(requiredDice, requiredSuccesses,
            isBlessed = false,
            isCursed = true
        )
        calculator.setIsMandy(true)
        calculator.setIsAddOne(true)
        Assert.assertEquals(percentageWins, calculator.calculate(), EPS)
    }

    private val randomDieValue: Int
        get() = random!!.nextInt(6) + 1

    companion object {
        private const val NUMBER_ITERATIONS = 10000000
        private const val EPS = 0.001
    }
}
