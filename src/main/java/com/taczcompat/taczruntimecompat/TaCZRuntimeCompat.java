package com.taczcompat.taczruntimecompat;

import com.taczcompat.taczruntimecompat.config.CompatConfig;
import com.taczcompat.taczruntimecompat.util.CompatLayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TaCZ Runtime Compat — entry point.
 *
 * <p>Patches invalid TaCZ gun resource paths at data-loading time via a scoped Mixin
 * and event-based validation. No global ResourceLocation overrides.</p>
 */
@Mod(TaCZRuntimeCompat.MOD_ID)
public class TaCZRuntimeCompat {

    public static final String MOD_ID = "taczruntimecompat";
    public static final Logger LOGGER  = LogManager.getLogger(MOD_ID);

    public TaCZRuntimeCompat(IEventBus modEventBus, ModContainer modContainer) {
        // Register the full config (CompatConfig) — replaces the thin TaCZCompatConfig
        modContainer.registerConfig(ModConfig.Type.COMMON, CompatConfig.SPEC);

        LOGGER.info("[TaCZRuntimeCompat] Loaded. TaCZ path sanitization active.");

        // Print compat notes on startup
        for (String note : CompatLayer.getCompatNotes()) {
            LOGGER.info(note);
        }
    }
}


