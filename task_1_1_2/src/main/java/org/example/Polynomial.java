package org.example;

import java.util.Arrays; // TODO: Убрать обработчики с map (они, скорее всего, зарпщены)

public class Polynomial {
    int[] coeffs;

    public Polynomial(int[] coeffs) {
        this.coeffs = coeffs.clone();
    }

    public int length() {
        return this.coeffs.length;
    }

    public int evaluate(int x) {
        if (this.length() == 0) {
            return 0;
        }
        int powX = x;
        int res = this.coeffs[0];
        for (int i = 1; i < this.length(); ++i) {
            res += (this.coeffs[i] * powX);
            powX *= x;
        }
        return res;
    }

    public Polynomial plus(Polynomial otherP) {
        int maxLen = Math.max(this.length(), otherP.length());
        int minLen = Math.min(this.length(), otherP.length());
        int[] newCoeffs = new int[maxLen];
        for (int i = 0; i < minLen; ++i) {
            newCoeffs[i] = this.coeffs[i] + otherP.coeffs[i];
        }
        for (int i = minLen; i < maxLen; ++i) {
            if (this.length() == maxLen) {
                newCoeffs[i] = this.coeffs[i];
            } else {
                newCoeffs[i] = otherP.coeffs[i];
            }
        }
        return new Polynomial(newCoeffs);
    }

    public Polynomial minus(Polynomial otherP) {
        int[] negativeOtherCoeffs = Arrays.stream(otherP.coeffs.clone())
            .map(num -> num * -1).toArray();
        return this.plus(new Polynomial(negativeOtherCoeffs));
    }

    public Polynomial mlt(Polynomial otherP) {
        int newLen = this.length() + otherP.length() - 1;
        int[] newCoeffs = new int[newLen];
        for (int i = 0; i < otherP.length(); ++i) {
            for (int j = 0; j < this.length(); ++j) {
                newCoeffs[i + j] += (otherP.coeffs[i] * this.coeffs[j]);
            }
        }
        return new Polynomial(newCoeffs);
    }

    public Polynomial differentiate(int power) {
        if (power <= 0) {
            return this;
        }
        int[] newCoeffs = new int[this.length() - power];
        for (int i = 0; i < newCoeffs.length; ++i) {
            newCoeffs[i] = this.coeffs[power + i];
        }
        int tmpPower = power;
        while (tmpPower > 0) {
            for (int i = 0; i < newCoeffs.length; ++i) {
                newCoeffs[i] *= (power + i);
            }
            tmpPower--;
        }
        return new Polynomial(newCoeffs);
    }

    public boolean isEqual(Polynomial otherP) {
        if (this.length() != otherP.length()) {
            return false;
        }
        for (int i = 0; i < this.length(); ++i) {
            if (this.coeffs[i] != otherP.coeffs[i]) {
                return false;
            }
        }
        return true;
    }

    static private String getPolyElement(int a, int p, boolean isFirst) {
        String res = "";
        if (a == 0) {
            return res;
        }
        String tmp = "";
        if (a < 0) {
            a *= -1;
            tmp = " - ";
        } else if (!isFirst) {
            tmp = " + ";
        }
        res = res.concat(tmp);
        if (p > 1) {
            tmp = String.format("%dx^%d", a, p);
        } else if (p == 1) {
            tmp = String.format("%dx", a);
        } else {
            tmp = String.format("%d", a);
        }
        return res.concat(tmp);
    }

    public String toString() {
        int p = this.length() - 1;
        if (p < 0) {
            return "";
        }
        String res = Polynomial.getPolyElement(this.coeffs[p], p--, true);
        String tmp;
        while (p >= 0) {
            tmp = Polynomial.getPolyElement(this.coeffs[p], p--, false);
            res = res.concat(tmp);
        }
        return res;
    }
}
