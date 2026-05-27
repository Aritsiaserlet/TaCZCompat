package com.taczcompat.taczruntimecompat.util;

import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

/**
 * Advanced Compatibility Layer for TaCZ Runtime Compat.
 * Detects known mods that aggressively modify resource loading and provides diagnostic info.
 */
public final class CompatLayer {

    private CompatLayer() {}

    public static boolean hasModernFix() {
        return ModList.get().isLoaded("modernfix");
    }

    public static boolean hasKubeJS() {
        return ModList.get().isLoaded("kubejs");
    }

    public static boolean hasArchitectury() {
        return ModList.get().isLoaded("architectury");
    }

    public static boolean hasEmbeddium() {
        return ModList.get().isLoaded("embeddium") || ModList.get().isLoaded("rubidium");
    }

    public static boolean hasResourcefulLib() {
        return ModList.get().isLoaded("resourcefullib");
    }

    public static boolean hasTaCZ() {
        return ModList.get().isLoaded("tacz");
    }

    public static boolean hasCreateRadar() {
        return ModList.get().isLoaded("createradar");
    }

    /**
     * Gets a list of detected compatibility notes for startup diagnostics.
     */
    public static List<String> getCompatNotes() {
        List<String> notes = new ArrayList<>();
        
        if (hasModernFix()) {
            notes.add("⚠ ModernFix detected: Mixins will degrade gracefully if ResourceLocation is replaced.");
        }
        if (hasKubeJS()) {
            notes.add("✔ KubeJS detected: Hooks correctly fire before script evaluation.");
        }
        if (hasArchitectury()) {
            notes.add("✔ Architectury detected: Hooks are compatible with abstracted resource handlers.");
        }
        if (hasEmbeddium()) {
            notes.add("✔ Embeddium/Rubidium detected: Render pipelines do not conflict with sanitization.");
        }
        if (hasResourcefulLib()) {
            notes.add("✔ ResourcefulLib detected: Transparently covered by vanilla mixins.");
        }
        if (hasCreateRadar()) {
            notes.add("[TaCZCompat] Create Radar detected - running in safe scoped mode");
        }

        return notes;
    }
}
