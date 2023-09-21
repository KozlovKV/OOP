package org.polynom;

import java.util.Arrays;

/**
 * Polynomial class.
 *
 * <p>All methods return new instance with new coefficients
 * so we can use one instance in a lot of computations.
 */
public class Polynomial {
    private int[] coefficients;

    /**
     * Polynomial class constructor.
     *
     * @param coefficients int array which is copied to new instance of Polynomial. (void array is assumed as [0])
     */
    public Polynomial(int[] coefficients) {
        if (coefficients.length == 0)
            this.coefficients = new int[]{0};
        else
            this.coefficients = coefficients.clone();
    }

    private void reduce() {
        int meaninglessZeros = 0;
        while (this.length() - 1 - meaninglessZeros > 0 &&
                this.coefficients[this.length() - 1 - meaninglessZeros] == 0) {
            meaninglessZeros++;
        }
        this.coefficients = Arrays.copyOf(
                this.coefficients, this.length() - meaninglessZeros
        );
    }

    /**
     * Coefficients' length getter.
     *
     * @return coefficients length.
     */
    public int length() {
        return this.coefficients.length;
    }

    /**
     * Evaluate Polynomial in point.
     *
     * @param x int value for x parameter.
     * @return int value for p(x).
     */
    public int evaluate(int x) {
        if (this.length() == 0) {
            return 0;
        }
        int powX = x;
        int res = this.coefficients[0];
        for (int i = 1; i < this.length(); ++i) {
            res += (this.coefficients[i] * powX);
            powX *= x;
        }
        return res;
    }

    /**
     * Method for summing.
     *
     * @param otherP second polynomial for summing.
     * @return new Polynomial that represents result of
     *      summing two Polynomial.
     */
    public Polynomial plus(Polynomial otherP) {
        int maxLen = Math.max(this.length(), otherP.length());
        int minLen = Math.min(this.length(), otherP.length());
        int[] newCoeffs = new int[maxLen];
        for (int i = 0; i < minLen; ++i) {
            newCoeffs[i] = this.coefficients[i] + otherP.coefficients[i];
        }
        for (int i = minLen; i < maxLen; ++i) {
            if (this.length() == maxLen) {
                newCoeffs[i] = this.coefficients[i];
            } else {
                newCoeffs[i] = otherP.coefficients[i];
            }
        }
        var result = new Polynomial(newCoeffs);
        result.reduce();
        return result;
    }

    /**
     * Method for subtraction.
     *
     * @param otherP second polynomial for subtraction.
     * @return new Polynomial that represents result of subtraction.
     */
    public Polynomial minus(Polynomial otherP) {
        int[] negativeOtherCoeffs = new int[otherP.length()];
        for (int i = 0; i < otherP.length(); ++i) {
            negativeOtherCoeffs[i] = otherP.coefficients[i] * -1;
        }
        return this.plus(new Polynomial(negativeOtherCoeffs));
    }

    /**
     * Method for multiplying.
     *
     * @param otherP second polynomial to multiply.
     * @return new Polynomial that represents result of
     *      multiplying two Polynomial
     */
    public Polynomial mlt(Polynomial otherP) {
        int newLen = this.length() + otherP.length() - 1;
        int[] newCoeffs = new int[newLen];
        for (int i = 0; i < otherP.length(); ++i) {
            for (int j = 0; j < this.length(); ++j) {
                newCoeffs[i + j] += (otherP.coefficients[i] * this.coefficients[j]);
            }
        }
        var result = new Polynomial(newCoeffs);
        result.reduce();
        return result;
    }

    /**
     * Method for differentiation.
     *
     * @param power the order of differentiation.
     * @return the result of differentiation.
     */
    public Polynomial differentiate(int power) {
        if (power <= 0) {
            return this;
        }
        if (this.length() - power <= 0) {
            return new Polynomial(new int[]{0});
        }
        int[] newCoeffs = Arrays.copyOfRange(this.coefficients, power, this.length());
        int tmpPower = power;
        while (tmpPower > 0) {
            for (int i = 0; i < newCoeffs.length; ++i) {
                newCoeffs[i] *= (tmpPower + i);
            }
            tmpPower--;
        }
        var result = new Polynomial(newCoeffs);
        result.reduce();
        return result;
    }

    /**
     * Method for comparing.
     *
     * @param obj the second Polynomial for comparing.
     * @return boolean result of comparing.
     *      Two empty Polynomial are assumed as equals.
     */
    @Override
    public boolean equals(Object obj) {
        Polynomial otherP = null;
        if (obj instanceof int[])
            otherP = new Polynomial((int[])obj);
        else if (obj instanceof Polynomial)
            otherP = (Polynomial) obj;
        if (this.length() != otherP.length()) {
            return false;
        }
        for (int i = 0; i < this.length(); ++i) {
            if (this.coefficients[i] != otherP.coefficients[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * String converter for one element.
     *
     * <p>Static method that returns string representation of
     * one polynomial element.
     *
     * @param coefficient int coefficient that is multiplied on x
     * @param power int power of x
     * @param isFirst this boolean flag is set <code>true</code> when
     *      we process the first string token.
     * @return element's string. For zero coefficient method returns
     *      empty string. For negative coefficient <code>` - `</code>
     *      will be placed in front. For positive non-first coefficient
     *      <code>` + `</code> will be placed in front.Common element
     *      format - <code>`[coefficient]x^[power]`</code>.
     *      For <code>power == 1</code> part <code>`^[power]`</code>
     *      will be excluded. For <code>power == 0</code> part
     *      <code>`x^[power]`</code> will be excluded. (Actually,
     *      this Polynomial realizations assumes only natural powers).
     */
    public static String getPolyElement(
        int coefficient, int power, boolean isFirst
    ) {
        String res = "";
        if (coefficient == 0) {
            return res;
        }
        String tmp = "";
        if (coefficient < 0) {
            coefficient *= -1;
            tmp = " - ";
        } else if (!isFirst) {
            tmp = " + ";
        }
        res = res.concat(tmp);
        if (power > 1) {
            tmp = String.format("%dx^%d", coefficient, power);
        } else if (power == 1) {
            tmp = String.format("%dx", coefficient);
        } else {
            tmp = String.format("%d", coefficient);
        }
        return res.concat(tmp);
    }

    /**
     * String converter.
     *
     * @return String representation of polynomial.
     *      Every element is got from <code>getPolyElement</code> method.
     */
    @Override
    public String toString() {
        int power = this.length() - 1;
        if (power < 0) {
            return "0";
        }
        String res = "";
        String tmp;
        while (power >= 0) {
            tmp = Polynomial.getPolyElement(this.coefficients[power], power--, res.isEmpty());
            res = res.concat(tmp);
        }
        return res.isEmpty() ? "0" : res;
    }
}
