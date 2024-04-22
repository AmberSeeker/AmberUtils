package com.amber.amberutils;

import net.minecraft.command.CommandBase;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.Dependency;

@Plugin(id = "amberutils", name = "AmberUtils", version = "0.0.1", description = "Utility stuff for pixelmon", dependencies = {@Dependency(id = "pixelmon")})
class AmberUtils {
    void main(String []args) {
        System.out.println("Hi.");
    }
}
