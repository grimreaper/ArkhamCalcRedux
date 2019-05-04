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

package com.eitanadler.arkhamcalcredux;

import junit.framework.TestCase;

import java.util.Random;

public class TestCalculator extends TestCase {
    private static final int NUMBER_ITERATIONS = 10000000;
    private static final double EPS = 0.001;

    private Random random;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        random = new Random();
    }

    public void testCalculate() {
        int requiredDice = 8;
        int requiredSuccesses = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 5) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        assertEquals(percentageWins, new Calculator(requiredDice, requiredSuccesses, false, false).calculate(), EPS);
    }

    public void testCalculateBlessed() {
        int requiredDice = 6;
        int requiredSuccesses = 2;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 4) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        assertEquals(percentageWins, new Calculator(requiredDice, requiredSuccesses, true, false).calculate(), EPS);
    }
    
    public void testCalculateBlessedProperty() {
        int requiredDice = 6;
        int requiredSuccesses = 2;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 4) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        Calculator calc = new Calculator(requiredDice, requiredSuccesses);
        calc.setIsBlessed(true);
        assertEquals(percentageWins, calc.calculate(), EPS);
    }    

    public void testCalculateCursed() {
        int requiredDice = 3;
        int requiredSuccesses = 1;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 6) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        assertEquals(percentageWins, new Calculator(requiredDice, requiredSuccesses, false, true).calculate(), EPS);
    }
    
    public void testCalculateCursedProperty() {
        int requiredDice = 3;
        int requiredSuccesses = 1;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 6) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        Calculator calc = new Calculator(requiredDice, requiredSuccesses);
        calc.setIsCursed(true);
        assertEquals(percentageWins, calc.calculate(), EPS);
    }    

    public void testCalculateNoSuccesses() {
        assertEquals(0.0, new Calculator(2, 3, false, false).calculate(), 0.0);
    }

    public void testCalculateChances() {
        int requiredDice = 7;
        int requiredSuccesses = 4;
        int requiredChances = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            for (int j = 0; j < requiredChances; j++) {
                int totalSuccesses = 0;
                for (int k = 0; k < requiredDice; k++) {
                    int dieValue = getRandomDieValue();
                    if (dieValue >= 5) {
                        totalSuccesses++;
                    }
                }
                if (totalSuccesses >= requiredSuccesses) {
                    totalWins++;
                    break;
                }
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        Calculator calc = new Calculator(requiredDice, requiredSuccesses);
        calc.setNumberOfChances(requiredChances);
        assertEquals(percentageWins, calc.calculate(), EPS);
    }

    public void testCalculateShotgun() {
        int requiredDice = 6;
        int requiredSuccesses = 4;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 6) {
                    totalSuccesses += 2;
                } else if (dieValue == 5) {
                    totalSuccesses += 1;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsShotgun(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }

    public void testCalculateShotgunBlessed() {
        int requiredDice = 6;
        int requiredSuccesses = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 6) {
                    totalSuccesses += 2;
                } else if (dieValue >= 4) {
                    totalSuccesses += 1;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, true, false);
        calculator.setIsShotgun(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }

    public void testCalculateShotgunCursed() {
        int requiredDice = 6;
        int requiredSuccesses = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 6) {
                    totalSuccesses += 2;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, true);
        calculator.setIsShotgun(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }

    public void testCalculateShotgunHuge() {
        int requiredDice = 12;
        int requiredSuccesses = 5;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 6) {
                    totalSuccesses += 2;
                } else if (dieValue == 5) {
                    totalSuccesses += 1;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsShotgun(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }

    public void testCalculateShotgunOneSuccess() {
        Calculator shotgunCalc = new Calculator(4, 1, false, false);
        shotgunCalc.setIsShotgun(true);
        assertEquals(new Calculator(4, 1, false, false).calculate(), shotgunCalc.calculate());
    }

    public void testCalculateShotgunImpossible() {
        int requiredDice = 2;
        int requiredSuccesses = 5;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 6) {
                    totalSuccesses += 2;
                } else if (dieValue == 5) {
                    totalSuccesses += 1;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsShotgun(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }

    public void testCalculateShotgunAlmostImpossible() {
        int requiredDice = 3;
        int requiredSuccesses = 6;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 6) {
                    totalSuccesses += 2;
                } else if (dieValue == 5) {
                    totalSuccesses += 1;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsShotgun(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }

    public void testCalcMandy() {
        int requiredDice = 6;
        int requiredSuccesses = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++;
                }
            }
            int firstRollSuccesses = totalSuccesses;
            for (int j = 0; j < requiredDice - firstRollSuccesses; j++) {
                int rerollDieValue = getRandomDieValue();
                if (rerollDieValue == 5 || rerollDieValue == 6) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsMandy(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);		
    }

    public void testCalcMandyCursed() {
        int requiredDice = 7;
        int requiredSuccesses = 4;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 6) {
                    totalSuccesses++;
                }
            }
            int firstRollSuccesses = totalSuccesses;
            for (int j = 0; j < requiredDice - firstRollSuccesses; j++) {
                int rerollDieValue = getRandomDieValue();
                if (rerollDieValue == 6) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, true);
        calculator.setIsMandy(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);		
    }

    public void testCalcMandyShotgun() {
        int requiredDice = 5;
        int requiredSuccesses = 4;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            int secondRollDice = requiredDice;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 6) {
                    totalSuccesses += 2;
                    secondRollDice--;
                } else if (dieValue == 5) {
                    totalSuccesses++;
                    secondRollDice--;
                }
            }
            for (int j = 0; j < secondRollDice; j++) {
                int rerollDieValue = getRandomDieValue();
                if (rerollDieValue == 6) {
                    totalSuccesses += 2;
                } else if (rerollDieValue == 5) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsMandy(true);
        calculator.setIsShotgun(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);		
    }

    public void testMandyImpossible() {
        int requiredDice = 2;
        int requiredSuccesses = 3;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsMandy(true);

        assertEquals(0.0, calculator.calculate(), 0.0);
    }

    public void testMandySameAsChances() {
        int requiredDice = 3;
        int requiredSuccesses = 1;

        Calculator twoChancesCalc = new Calculator(requiredDice, requiredSuccesses, false, false);
        twoChancesCalc.setNumberOfChances(2);
        Calculator mandyCalc = new Calculator(requiredDice, requiredSuccesses, false, false);
        mandyCalc.setIsMandy(true);
        assertEquals(twoChancesCalc.calculate(), mandyCalc.calculate(), EPS);
    }

    public void testMandyMultipleChances() {
        int requiredDice = 7;
        int requiredSuccesses = 5;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++;
                }
            }
            int firstRollSuccesses = totalSuccesses;
            for (int j = 0; j < requiredDice - firstRollSuccesses; j++) {
                int rerollDieValue = getRandomDieValue();
                if (rerollDieValue == 5 || rerollDieValue == 6) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            } else { //try again w/o mandy's ability
                totalSuccesses = 0;
                for (int j = 0; j < requiredDice; j++) {
                    int dieValue = getRandomDieValue();
                    if (dieValue >= 5) {
                        totalSuccesses++;
                    }
                }
                if (totalSuccesses >= requiredSuccesses) {
                    totalWins++;
                }
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setNumberOfChances(2);
        calculator.setIsMandy(true);
        assertEquals(percentageWins, calculator.calculate(), EPS); //two chances
    }

    public void testRerollOnes() {
        int requiredDice = 8;
        int requiredSuccesses = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            int numberOfOnes = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 5) {
                    totalSuccesses++;
                } else if (dieValue == 1) {
                    numberOfOnes++;
                }
            }
            for (int j = 0; j < numberOfOnes; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 5) {
                    totalSuccesses++;
                }				
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsRerollOnes(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }
    
    public void testSkidsOnes() {
        int requiredDice = 8;
        int requiredSuccesses = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            int numberOfOnes = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 5) {
                    totalSuccesses++;
                } else if (dieValue == 1) {
                    numberOfOnes++;
                }
            }
            for (int j = 0; j < numberOfOnes * 2; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 5) {
                    totalSuccesses++;
                }               
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsSkids(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }

    public void testRerollOnesCursed() {
        int requiredDice = 6;
        int requiredSuccesses = 2;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            int numberOfOnes = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 6) {
                    totalSuccesses++;
                } else if (dieValue == 1) {
                    numberOfOnes++;
                }
            }
            for (int j = 0; j < numberOfOnes; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 6) {
                    totalSuccesses++;
                }				
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, true);
        calculator.setIsRerollOnes(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }

    public void testRerollOnesBlessed() {
        int requiredDice = 3;
        int requiredSuccesses = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            int numberOfOnes = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 4) {
                    totalSuccesses++;
                } else if (dieValue == 1) {
                    numberOfOnes++;
                }
            }
            for (int j = 0; j < numberOfOnes; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 4) {
                    totalSuccesses++;
                }				
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, true, false);
        calculator.setIsRerollOnes(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }
    
    public void testSkidsOnesBlessed() {
        int requiredDice = 3;
        int requiredSuccesses = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            int numberOfOnes = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 4) {
                    totalSuccesses++;
                } else if (dieValue == 1) {
                    numberOfOnes++;
                }
            }
            for (int j = 0; j < numberOfOnes * 2; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 4) {
                    totalSuccesses++;
                }               
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, true, false);
        calculator.setIsSkids(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }

    public void testRerollOnesMultipleChances() {
        int requiredDice = 7;
        int requiredSuccesses = 5;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            int numberOfOnes = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++;
                } else if (dieValue == 1) {
                    numberOfOnes++;
                }
            }
            for (int j = 0; j < numberOfOnes; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 5 || dieValue == 6) {
                    totalSuccesses++;
                }				
            }

            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            } else { //try again w/o reroll ability
                totalSuccesses = 0;
                for (int j = 0; j < requiredDice; j++) {
                    int dieValue = getRandomDieValue();
                    if (dieValue >= 5) {
                        totalSuccesses++;
                    }
                }
                if (totalSuccesses >= requiredSuccesses) {
                    totalWins++;
                }
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setNumberOfChances(2);
        calculator.setIsRerollOnes(true);
        assertEquals(percentageWins, calculator.calculate(), EPS); //two chances
    }

    public void testRerollOnesShotgun() {
        int requiredDice = 10;
        int requiredSuccesses = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            int numberOfOnes = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 5) {
                    totalSuccesses++;
                } else if (dieValue == 6) {
                    totalSuccesses += 2;
                } else if (dieValue == 1) {
                    numberOfOnes++;
                }
            }
            for (int j = 0; j < numberOfOnes; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 5) {
                    totalSuccesses++;
                } else if (dieValue == 6) {
                    totalSuccesses += 2;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsRerollOnes(true);
        calculator.setIsShotgun(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }
    
    public void testSkidsOnesAlmostImpossible() {
        int requiredDice = 1;
        int requiredSuccesses = 2;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            int numberOfOnes = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 5) {
                    totalSuccesses++;
                } else if (dieValue == 1) {
                    numberOfOnes++;
                }
            }
            for (int j = 0; j < numberOfOnes * 2; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 5) {
                    totalSuccesses++;
                }               
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        assertTrue(percentageWins > 0);
        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, false);
        calculator.setIsSkids(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
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

    public void testAddOneComparedToBlessed()
    {
        int requiredDice = 5;
        int requiredSuccesses = 4;
        Calculator blessedCalc = new Calculator(requiredDice, requiredSuccesses, true, false);
        Calculator addOneCalc = new Calculator(requiredDice, requiredSuccesses, false, false);
        addOneCalc.setIsAddOne(true);
        assertEquals(blessedCalc.calculate(), addOneCalc.calculate(), 0.0);
    }

    public void testAddOneAndBlessed()
    {
        int requiredDice = 3;
        int requiredSuccesses = 3;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 3) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, true, false);
        calculator.setIsAddOne(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);		
    }

    public void testAddOneCursed()
    {
        int requiredDice = 8;
        int requiredSuccesses = 4;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 5) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, true);
        calculator.setIsAddOne(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);		
    }

    public void testAddOneShotgunCursed()
    {
        int requiredDice = 8;
        int requiredSuccesses = 4;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue == 6) {
                    totalSuccesses += 2;
                } else if (dieValue == 5) {
                    //even though a rolled 5 becomes a 6 for the purpose of checking successes, it doesn't count as a shotgun.
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, true);
        calculator.setIsShotgun(true);
        calculator.setIsAddOne(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);			
    }

    public void testRerollOnesAddOneBlessed()
    {
        int requiredDice = 6;
        int requiredSuccesses = 6;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            int numberOfOnes = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 3) {
                    totalSuccesses++;
                } else if (dieValue == 1) {
                    numberOfOnes++;
                }
            }
            for (int j = 0; j < numberOfOnes; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 3) {
                    totalSuccesses++;
                }				
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }

        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;
        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, true, false);
        calculator.setIsRerollOnes(true);
        calculator.setIsAddOne(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);
    }

    public void testCalcMandyCursedAddOne() {
        int requiredDice = 7;
        int requiredSuccesses = 5;

        int totalWins = 0;
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            int totalSuccesses = 0;
            for (int j = 0; j < requiredDice; j++) {
                int dieValue = getRandomDieValue();
                if (dieValue >= 5) {
                    totalSuccesses++;
                } 
            }
            int firstRollSuccesses = totalSuccesses;
            for (int j = 0; j < requiredDice - firstRollSuccesses; j++) {
                int rerollDieValue = getRandomDieValue();
                if (rerollDieValue >= 5) {
                    totalSuccesses++;
                }
            }
            if (totalSuccesses >= requiredSuccesses) {
                totalWins++;
            }
        }
        double percentageWins = (double)totalWins / NUMBER_ITERATIONS;

        Calculator calculator = new Calculator(requiredDice, requiredSuccesses, false, true);
        calculator.setIsMandy(true);
        calculator.setIsAddOne(true);
        assertEquals(percentageWins, calculator.calculate(), EPS);		
    }

    private int getRandomDieValue() {
        return random.nextInt(6) + 1;
    }

}
