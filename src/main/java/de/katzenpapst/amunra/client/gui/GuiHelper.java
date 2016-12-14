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

    /**
     * Formats a time (in ticks) to a hh:mm:ss format, with minecraft hours, minutes and seconds
     * @param number
     * @return
     */
    public static String formatTime(int number) {

        double hoursFraction = number / 1000.0D;

        int hours = (int)hoursFraction;
        hoursFraction -= hours;
        hoursFraction *= 60.0D;

        int minutes = (int)hoursFraction;

        hoursFraction -= minutes;
        hoursFraction *= 60.0D;

        int seconds = (int) hoursFraction;

        if(hours > 24) {
            int days = hours / 24;
            hours -= days*24.0D;

            if(days > 9) {
                if(days >= 30) {
                    int months = days / 30;
                    days -= months * 30.0D;
                    if(months >= 12) {
                        int years = months / 12;
                        months -= years * 12.0D;
                        if(years >= 10) {
                            return String.format("> %dy");
                        } else {
                            return String.format("%dy %dm %dd", years, months, days);
                        }
                    } else {

                        return String.format("%dm %dd", months, days);
                    }
                } else {
                    return String.format("%dd", days);
                }
            } else {
                return String.format("%dd %d:%d:%d", days, hours, minutes, seconds);

            }
        } else {
            return String.format("%d:%d:%d", hours, minutes, seconds);
        }
    }

    /**
     * Converts a speed from AU/t into AU/h and formats the number
     *
     * @param number
     * @return
     */
    public static String formatSpeed(double number) {
        // which is rather simple, since one MC hour is 1000 ticks
        return formatMetric(number*1000, "AU/h");
    }

}
