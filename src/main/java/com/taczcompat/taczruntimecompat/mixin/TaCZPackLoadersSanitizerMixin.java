package com.taczcompat.taczruntimecompat.mixin;

import com.taczcompat.taczruntimecompat.util.ResourceLocationUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Sanitizes all ResourceLocation strings produced by TaCZ resource loader classes.
 * Covers GunPackLoader, AttachmentPackLoader, AmmoPackLoader and related loaders.
 *
 * Intercepts every ResourceLocation factory/constructor call made from these classes
 * and normalizes the raw string BEFORE it reaches the ResourceLocation constructor.
 *
 * This does NOT modify any Minecraft core class.
 */
@Pseudo
@Mixin(
        targets = {
                // Core pack loaders
                "com.tacz.timelessandclassics.resource.GunPackLoader",
                "com.tacz.timelessandclassics.resource.AttachmentPackLoader",
                "com.tacz.timelessandclassics.resource.AmmoPackLoader",
                // Additional pack loaders discovered at runtime
                "com.tacz.timelessandclassics.resource.index.GunIndexLoader",
                "com.tacz.timelessandclassics.resource.index.AttachmentIndexLoader",
                "com.tacz.timelessandclassics.resource.index.AmmoIndexLoader",
                "com.tacz.timelessandclassics.resource.display.GunDisplayLoader",
                "com.tacz.timelessandclassics.resource.display.AttachmentDisplayLoader",
                "com.tacz.timelessandclassics.resource.display.AmmoDisplayLoader",
                "com.tacz.timelessandclassics.resource.GunReloadManager",
                "com.tacz.timelessandclassics.resource.PackEventsHandler",
                "com.tacz.timelessandclassics.resource.CommonGunPackLoader",
                "com.tacz.timelessandclassics.resource.RecipeLoader",
                "com.tacz.timelessandclassics.resource.GunDataManager"
        },
        remap = false
)
public class TaCZPackLoadersSanitizerMixin {

    // ── 1-arg: ResourceLocation.parse(String) ─────────────────────────────────
    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;parse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            require = 0
    )
    private String taczcompat$loader$sanitizeParse(String id) {
        return ResourceLocationUtils.sanitizeId(id);
    }

    // ── 1-arg: ResourceLocation.tryParse(String) ──────────────────────────────
    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            require = 0
    )
    private String taczcompat$loader$sanitizeTryParse(String id) {
        return ResourceLocationUtils.sanitizeId(id);
    }

    // ── 1-arg: new ResourceLocation(String) ───────────────────────────────────
    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"
            ),
            require = 0
    )
    private String taczcompat$loader$sanitizeConstructor1(String id) {
        return ResourceLocationUtils.sanitizeId(id);
    }

    // ── 2-arg: ResourceLocation.fromNamespaceAndPath(String, String) path ─────
    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            index = 1,
            require = 0
    )
    private String taczcompat$loader$sanitizeFromNsPath(String path) {
        return ResourceLocationUtils.sanitizePath(path);
    }

    // ── 2-arg: new ResourceLocation(String, String) path ──────────────────────
    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;Ljava/lang/String;)V"
            ),
            index = 1,
            require = 0
    )
    private String taczcompat$loader$sanitizeConstructor2(String path) {
        return ResourceLocationUtils.sanitizePath(path);
    }
}
