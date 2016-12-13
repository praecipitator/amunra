package de.katzenpapst.amunra.client.gui;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;



import net.minecraft.client.renderer.Tessellator;

public class GuiHelper {

    protected static DecimalFormat numberFormat = new DecimalFormat("#.##");

    public static final char[] metricHigh = {'k', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y'};
    public static final char[] metricLow  = {'m', 'µ', 'n', 'p', 'f', 'a', 'z', 'y'};

    public static String formatMetric(double number) {
        return formatMetric(number, "");
    }

    public static String formatMetric(double number, String unit) {
        if(number < 0) {
            return "-"+formatMetric(number*-1, unit);
        }
        if(number == 0) {
            return String.format("%s%s", numberFormat.format(number), unit);
        }
        char suffix = 0;
        String result = "";
        int numZeroes = (int) Math.floor( Math.log10(number) );
        int numThousands = (int) Math.floor(numZeroes / 3);
        if(numThousands > 0) {

            if(numThousands > metricHigh.length) {
                numThousands = metricHigh.length;
            }
            number = number / (Math.pow(1000, numThousands));
            suffix = metricHigh[numThousands-1];
            //result = String.valueOf(number)+" "+metricHigh[numThousands-1];
        } else if(numThousands < 0) {
            numThousands *= -1;
            if(numThousands > metricLow.length) {
                numThousands = metricLow.length;
            }
            number = number / (Math.pow(0.001,numThousands));
            // result = String.valueOf(number)+" "+metricLow[numThousands-1];
            suffix = metricLow[numThousands-1];
        }

        // String.format
        result = numberFormat.format(number);
        if(suffix != 0) {
            return String.format("%s%c%s", result, suffix, unit);
        }
        return String.format("%s%s", result, unit);
    }

    /**
     * Specialized version to format kilograms, because it's weird
     * @param number
     * @return
     */
    public static String formatKilogram(double number) {
        if(number < 0) {
            return "-"+formatKilogram(number*-1);
        }
        if(number < 1000) {
            // for 0 <= n < 1000, format the number using grams
            // this should prepend the k if needed
            return formatMetric(number*1000, "g");
        }
        // over 1000, format this using tons
        return formatMetric(number/1000, "t");

    }

}
