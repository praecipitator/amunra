package de.katzenpapst.amunra.helper;

public class InteroperabilityHelper {

    public static boolean hasIDismantleable = false;

    public static void initCompatibility() {
        try {
            if(Class.forName("cofh.api.block.IDismantleable") != null) {
                hasIDismantleable = true;
            }
        } catch (ClassNotFoundException e) {
            hasIDismantleable = false;
        }
    }

}
