package com.amber.amberutils.helpers;

public class ToggleHelper {

    public static ToggleHelper noSpaceToggle = new ToggleHelper(null);
    //Define more as needed for command building
    public static ToggleHelper notImplemented = new ToggleHelper(null);

    private Boolean value;

    public ToggleHelper(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
