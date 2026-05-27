package com.taczcompat.taczruntimecompat.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TaCZCompatConfig {
    public static final ModConfigSpec SPEC;
    
    public static final ModConfigSpec.BooleanValue ENABLE_SANITIZATION;
    public static final ModConfigSpec.BooleanValue ENABLE_CREATE_RADAR_FIX;
    public static final ModConfigSpec.BooleanValue DEBUG_MODE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("general");
        
        ENABLE_SANITIZATION = builder
                .comment("Enable runtime sanitization of TaCZ gun resource paths to prevent crashes.")
                .define("enableSanitization", true);
                
        ENABLE_CREATE_RADAR_FIX = builder
                .comment("Enable specific compatibility fixes when Create Radar is detected.")
                .define("enableCreateRadarFix", true);
                
        DEBUG_MODE = builder
                .comment("Enable verbose logging for original paths, sanitized paths, and texture redirects.")
                .define("debugMode", false);
                
        builder.pop();

        SPEC = builder.build();
    }
}
