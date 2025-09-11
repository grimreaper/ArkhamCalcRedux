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

import androidx.compose.ui.graphics.Color
import java.text.NumberFormat

/**
 * Given the result of a Calculator, determines appropriate formatting to be
 * displayed on UI.
 */
internal class CalculateResultFormatter
    (private val mResult: Double) {
    val color: Color
        get() {
            if (mResult > .66) {
                return Color.Green
            }
            if (mResult > .33) {
                return Color.Yellow
            }
            return Color.Red
        }

    val resultString: String
        get() {
            if (mResult >= .9995) {
                //show ">99.9%" instead of 100%
                return almostOneResultString
            }
            if (mResult > 0 && mResult < 0.0005) {
                //show "<0.1% instead of 0%
                return almostZeroResultString
            }
            return numberFormat.format(mResult)
        }

    companion object {

        private val almostZeroResultString: String
            get() = "<" + numberFormat.format(.001)

        private val almostOneResultString: String
            get() = ">" + numberFormat.format(.999)

        private val numberFormat: NumberFormat
            get() {
                val numberFormat =
                    NumberFormat.getPercentInstance()
                numberFormat.setMinimumFractionDigits(1)
                numberFormat.setMaximumFractionDigits(1)
                return numberFormat
            }
    }
}
