package com.eitanadler.arkhamcalcredux2.util

import androidx.compose.ui.graphics.Color
import com.eitanadler.arkhamcalcredux2.ui.theme.DiceChanceColoursPalette
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CalculateResultFormatterTest {
    private val palette = DiceChanceColoursPalette(
        isHighChance = Color.Red,
        isLowChance = Color.Green,
        isMidChance = Color.Blue,
    )

    @Test
    fun testLowPercentageFormat() {
        val calcFormatter = CalculateResultFormatter(
            0.10,
            palette,
        )
        assertThat(calcFormatter.resultString, `is`("10.0%"))
        assertThat(calcFormatter.color, `is`(palette.isLowChance))
    }

    @Test
    fun testMidPercentageFormat() {
        val calcFormatter = CalculateResultFormatter(
            0.50,
            palette,
        )
        assertThat(calcFormatter.resultString, `is`("50.0%"))
        assertThat(calcFormatter.color, `is`(palette.isMidChance))
    }

    @Test
    fun testHighPercentageFormat() {
        val calcFormatter = CalculateResultFormatter(
            0.90,
            palette,
        )
        assertThat(calcFormatter.resultString, `is`("90.0%"))
        assertThat(calcFormatter.color, `is`(palette.isHighChance))
    }

    @Test
    fun testAlmostCertainPercentageFormat() {
        val calcFormatter = CalculateResultFormatter(
            0.9997,
            palette,
        )
        assertThat(calcFormatter.resultString, `is`(">99.9%"))
        assertThat(calcFormatter.color, `is`(palette.isHighChance))
    }

    @Test
    fun testAlmostImpossiblePercentageFormat() {
        val calcFormatter = CalculateResultFormatter(
            0.0004,
            palette,
        )
        assertThat(calcFormatter.resultString, `is`("<0.1%"))
        assertThat(calcFormatter.color, `is`(palette.isLowChance))
    }

    @Test
    fun testImpossiblePercentageFormat() {
        val calcFormatter = CalculateResultFormatter(
            0.0,
            palette,
        )
        assertThat(calcFormatter.resultString, `is`("0.0%"))
        assertThat(calcFormatter.color, `is`(palette.isLowChance))
    }
}
