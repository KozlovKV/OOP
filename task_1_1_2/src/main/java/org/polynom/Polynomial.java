package org.polynom;

/**
 * Polynomial class.
 * <p>
 * All methods return new instance with new coefficients
 * so we can use one instance in a lot of computations.
 */
public class Polynomial {
    private final int[] coefficients;

    /**
     * @param coefficients int array which is copied to new instance of Polynomial.
     */
    public Polynomial(int[] coefficients) {
        this.coefficients = coefficients.clone();
    }

    /**
     * @return coefficients length.
     */
    public int length() {
        return this.coefficients.length;
    }

    /**
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
     * @param otherP second polynomial for the sum.
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
        return new Polynomial(newCoeffs);
    }

    /**
     * @param otherP second polynomial for subtract.
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
     * @param otherP second polynomial for multiply.
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
        return new Polynomial(newCoeffs);
    }

    /**
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
        int[] newCoeffs = new int[this.length() - power];
        for (int i = 0; i < newCoeffs.length; ++i) {
            newCoeffs[i] = this.coefficients[power + i];
        }
        int tmpPower = power;
        while (tmpPower > 0) {
            for (int i = 0; i < newCoeffs.length; ++i) {
                newCoeffs[i] *= (tmpPower + i);
            }
            tmpPower--;
        }
        return new Polynomial(newCoeffs);
    }

    /**
     * @param otherP the second Polynomial for comparing.
     * @return boolean result of comparing.
     *      Two empty Polynomial are assumed as equals.
     */
    public boolean isEqual(Polynomial otherP) {
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
     * Static method that returns string representation of one polynomial element
     *
     * @param coefficient int coefficient that is multiplied on x
     * @param power int power of x
     * @param isFirst this boolean flag is set <code>true</code> when we process the first string token.
     * @return element's string.
     *      <p>For zero coefficient method returns empty string.
     *      <p>For negative coefficient <code>` - `</code> will be placed in front.
     *      <p>For positive non-first coefficient <code>` + `</code> will be placed in front.
     *      <p>Common element format - <code>`[coefficient]x^[power]`</code>.
     *      <p>For <code>power == 1</code> part <code>`^[power]`</code> will be excluded.
     *      <p>For <code>power < 1</code> part <code>`x^[power]`</code> will be excluded
     *      <p>(Actually, this Polynomial realizations assumes only <code>power >= 0</code>).
     */
    static public String getPolyElement(
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
     * @return String representation of polynomial.
     * Every element is got from `getPolyElement` method.
     */
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
