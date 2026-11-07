package io.engproject;


public class AccessibilitySettings {

    private static boolean highContrast = false;

    public static void toggleHighContrast() {
        highContrast = !highContrast;
    }

    public static boolean isHighContrast() {
        return highContrast;
    }
}
