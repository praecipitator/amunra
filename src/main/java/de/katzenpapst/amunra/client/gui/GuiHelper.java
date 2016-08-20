package de.katzenpapst.amunra.client.gui;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;



import net.minecraft.client.renderer.Tessellator;

public class GuiHelper {

    protected static DecimalFormat numberFormat = new DecimalFormat("#.##");

    public static final char[] metricHigh = {'k', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y'};
    public static final char[] metricLow  = {'m', 'µ', 'n', 'p', 'f', 'a', 'z', 'y'};



    public static String formatMetric(double number) {
        if(number < 0) {
            return "-"+formatMetric(number*-1);
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
            return String.format("%s%c", result, suffix);
        }
        return result;
    }

}
