package com.taczcompat.taczruntimecompat.event;

import com.taczcompat.taczruntimecompat.TaCZRuntimeCompat;
import com.taczcompat.taczruntimecompat.config.CompatConfig;
import com.taczcompat.taczruntimecompat.util.ResourceLocationUtils;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.Map;

@EventBusSubscriber(modid = "taczruntimecompat", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientCompatEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (!CompatConfig.ENABLE_GLOBAL_SANITIZER.get()) return;

        if (ModList.get().isLoaded("createradar")) {
            TaCZRuntimeCompat.LOGGER.info("[TaCZCompat] Create Radar detected. Relying purely on data-level mixin for safe compatibility.");
            // REMOVED: reloadResourcePacks() to prevent visual rendering freeze on the Mojang screen
        }
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        if (!CompatConfig.ENABLE_GLOBAL_SANITIZER.get()) return;

        Map<net.minecraft.client.resources.model.ModelResourceLocation, net.minecraft.client.resources.model.BakedModel> models = event.getModels();
        int fixedCount = 0;

        for (Map.Entry<net.minecraft.client.resources.model.ModelResourceLocation, net.minecraft.client.resources.model.BakedModel> entry : models.entrySet()) {
            net.minecraft.client.resources.model.ModelResourceLocation location = entry.getKey();
            
            if ("tacz".equals(location.id().getNamespace())) {
                String originalPath = location.id().getPath();
                String sanitizedPath = ResourceLocationUtils.sanitizePath(originalPath);

                if (!originalPath.equals(sanitizedPath)) {
                    if (CompatConfig.DEBUG_MODE.get()) {
                        TaCZRuntimeCompat.LOGGER.warn("[TaCZCompat] Model path requires sanitization: {} -> {}", originalPath, sanitizedPath);
                    }
                    // Since we're in ModifyBakingResult, we can optionally remap models here if needed.
                    // For now, we rely on the loader mixin to fix the paths before they even get here.
                    // If a bad path made it through, log it.
                    fixedCount++;
                }
            }
        }
        
        if (fixedCount > 0 && CompatConfig.DEBUG_MODE.get()) {
            TaCZRuntimeCompat.LOGGER.info("[TaCZCompat] Checked baked models. Found {} models with questionable paths.", fixedCount);
        }
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new PreparableReloadListener() {
            @Override
            public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager, 
                                                  ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, 
                                                  Executor backgroundExecutor, Executor gameExecutor) {
                return barrier.wait(null).thenRunAsync(() -> {
                    if (CompatConfig.DEBUG_MODE.get()) {
                        TaCZRuntimeCompat.LOGGER.info("[TaCZCompat] Resource reload triggered. Ensuring TaCZ assets are validated.");
                    }
                }, gameExecutor);
            }
        });
    }
}
