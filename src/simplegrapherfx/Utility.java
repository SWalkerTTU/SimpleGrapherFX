/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplegrapherfx;

import java.util.stream.IntStream;

/**
 *
 * @author scott.walker
 */
public class Utility {

    public static double stepCalc(double scale) {
        int range = 20 - (int) Math.round(Math.log10(scale) * 10);
        int power = (int) Math.floor(range / 10.0);
        int switchVal = range % 10 < 0 ? range % 10 + 10 : range % 10;
        double factor;
        switch (switchVal) {
            case 0:
            case 1:
                factor = 1;
                break;
            case 2:
            case 3:
            case 4:
                factor = 2;
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                factor = 5;
                break;
            case 9:
                factor = 10;
                break;
            default:
                factor = 1;
        }
        double tickStep = factor * Math.pow(10, power);
        return tickStep;
    }

    public static double poly(double xValue, double[] xCoeffs) {
        return IntStream.range(0, xCoeffs.length).parallel()
                .mapToDouble(i -> Math.pow(xValue, i) * xCoeffs[i]).sum();
    }

}
