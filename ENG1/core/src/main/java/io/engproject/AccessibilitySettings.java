package io.engproject;

/**
 * A class to store the game's settings for accessibility, which can also be toggled
 * here.
 */
public class AccessibilitySettings {

    private static boolean highContrast = false;

    public static void toggleHighContrast() {
        highContrast = !highContrast;
    }

    public static boolean isHighContrast() {
        return highContrast;
    }
}