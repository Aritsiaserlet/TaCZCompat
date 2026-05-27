package com.taczcompat.taczruntimecompat.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * Mod configuration definition using NeoForge's ModConfigSpec.
 * Defines all rules for the sanitizer, mixin layers, and debug output.
 */
public final class CompatConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ENABLE_GLOBAL_SANITIZER;
    public static final ModConfigSpec.BooleanValue ENABLE_TACZ_MIXIN;
    public static final ModConfigSpec.BooleanValue DEBUG_MODE;
    public static final ModConfigSpec.BooleanValue DRY_RUN;
    public static final ModConfigSpec.BooleanValue LOG_EVERY_FIX;
    public static final ModConfigSpec.BooleanValue CACHE_SANITIZED_RESULTS;
    public static final ModConfigSpec.IntValue MAX_CACHE_ENTRIES;
    public static final ModConfigSpec.BooleanValue STRICT_TACZ_ONLY_MODE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ALLOWED_NAMESPACES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCKED_NAMESPACES;
    public static final ModConfigSpec.BooleanValue SHOW_GUI_WARNING;
    public static final ModConfigSpec.BooleanValue SAFE_MODE;

    // Custom Rules
    public static final ModConfigSpec.BooleanValue REPLACE_COLON_WITH_UNDERSCORE;
    public static final ModConfigSpec.BooleanValue REPLACE_SPACES;
    public static final ModConfigSpec.BooleanValue FORCE_LOWERCASE;
    public static final ModConfigSpec.BooleanValue STRIP_UNICODE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_REPLACEMENT_RULES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("TaCZ Runtime Compat Configuration")
                .push("general");

        ENABLE_GLOBAL_SANITIZER = builder
                .comment("Master switch for ResourceLocation parsing hooks. If false, disables the core fix.")
                .define("enableGlobalSanitizer", true);

        ENABLE_TACZ_MIXIN = builder
                .comment("Enables the TaCZ-specific mixin that catches bad IDs directly from GunPackLoader. Provides better logs.")
                .define("enableTaCZMixin", true);

        DRY_RUN = builder
                .comment("If true, invalid IDs are logged but NOT fixed. Allows pack developers to see broken packs safely.")
                .define("dryRun", false);
                
        CACHE_SANITIZED_RESULTS = builder
                .comment("Caches sanitized results in memory to avoid repeated string manipulation. Recommended: true")
                .define("cacheSanitizedResults", true);

        MAX_CACHE_ENTRIES = builder
                .comment("Maximum number of entries in the sanitized cache to prevent memory bloat.")
                .defineInRange("maxCacheEntries", 8192, 1024, 65536);

        SHOW_GUI_WARNING = builder
                .comment("Shows an in-game chat warning to players when joining if invalid gun packs were repaired.")
                .define("showGuiWarning", true);

        SAFE_MODE = builder
                .comment("Emergency compatibility mode for unstable modpacks. Minimizes intervention and only logs.")
                .define("safeMode", false);

        builder.pop();

        builder.comment("Sanitization Rules")
                .push("rules");

        REPLACE_COLON_WITH_UNDERSCORE = builder
                .comment("Replace colons in paths with underscores.")
                .define("replaceColonWithUnderscore", true);

        REPLACE_SPACES = builder
                .comment("Replace spaces with underscores.")
                .define("replaceSpaces", true);

        FORCE_LOWERCASE = builder
                .comment("Force all characters to lowercase.")
                .define("forceLowercase", true);

        STRIP_UNICODE = builder
                .comment("Strip all non-ASCII unicode characters from IDs. Helpful for paths with emojis or foreign chars.")
                .define("stripUnicode", true);

        CUSTOM_REPLACEMENT_RULES = builder
                .comment("Custom character replacements. Format: 'target=replacement'. Example: '@=*'")
                .defineList("customReplacementRules", List.of(), obj -> obj instanceof String);

        builder.pop();

        builder.comment("Namespace filtering settings")
                .push("namespaces");

        STRICT_TACZ_ONLY_MODE = builder
                .comment("If true, only sanitizes namespaces listed in allowedNamespaces.")
                .define("strictTaCZOnlyMode", true);

        ALLOWED_NAMESPACES = builder
                .comment("List of namespaces allowed to be sanitized if strict mode is enabled.")
                .defineList("allowedNamespaces", List.of("tacz", "modern_kinetic_gun", "sfms", "cgm", "pointblank"), obj -> obj instanceof String);

        BLOCKED_NAMESPACES = builder
                .comment("List of namespaces that will NEVER be sanitized. Protects core game and loader resources.")
                .defineList("blockedNamespaces", List.of("minecraft", "neoforge", "forge", "realms"), obj -> obj instanceof String);

        builder.pop();

        builder.comment("Debugging and logging")
                .push("debug");

        DEBUG_MODE = builder
                .comment("Enables full debug reporting, statistics tracking, and log exporting.")
                .define("debugMode", false);

        LOG_EVERY_FIX = builder
                .comment("If true, prints every single sanitization event to the console. Spammy! Use for debugging only.")
                .define("logEveryFix", false);

        builder.pop();

        SPEC = builder.build();
    }
}
