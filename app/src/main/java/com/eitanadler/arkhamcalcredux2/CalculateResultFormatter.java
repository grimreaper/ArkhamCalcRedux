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

import java.text.NumberFormat;

/**
 * Given the result of a Calculator, determines appropriate formatting to be 
 * displayed on UI.
 */
public class CalculateResultFormatter
{
    private static final int COLOR_GREEN = 0xFF00CC00;
    private static final int COLOR_YELLOW = 0xFFFFFF00;
    private static final int COLOR_RED = 0xFFFF0000;

    private double mResult;

    public CalculateResultFormatter(double result)
    {
        mResult = result;
    }

    public int getColor()
    {
        if (mResult > .66) {
            return COLOR_GREEN;
        }
        if (mResult > .33) {
            return COLOR_YELLOW;
        }
        return COLOR_RED;
    }

    public String getResultString()
    {
        if (mResult >= .9995) {
            //show ">99.9%" instead of 100%
            return getAlmostOneResultString();
        }
        if (mResult > 0 && mResult < 0.0005) {
            //show "<0.1% instead of 0%
            return getAlmostZeroResultString();
        }
        return getNumberFormat().format(mResult);
    }

    private static String getAlmostZeroResultString()
    {
        return "<" + getNumberFormat().format(.001);
    }

    private static String getAlmostOneResultString()
    {
        return ">" + getNumberFormat().format(.999);
    }

    private static NumberFormat getNumberFormat()
    {
        NumberFormat numberFormat= NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(1);
        numberFormat.setMaximumFractionDigits(1);
        return numberFormat;
    }
}
