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

/**
 * Static class that contains math functions not specific to Arkham Horror.
 */
public final class MathHelper
{
    private MathHelper()
    {
        //static helper class
    }

    /**
     * From n, choose r.  See http://en.wikipedia.org/wiki/Combination
     */
    public static long nCr(int n, int r)
    {
        return factorial(n) / (factorial(n - r) * factorial(r));
    }

    private static long factorial(int n)
    {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
}
