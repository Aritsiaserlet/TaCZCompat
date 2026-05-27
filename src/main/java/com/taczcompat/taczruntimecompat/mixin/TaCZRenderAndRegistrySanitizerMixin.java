package com.taczcompat.taczruntimecompat.mixin;

import com.taczcompat.taczruntimecompat.util.ResourceLocationUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Sanitizes all ResourceLocation strings produced by TaCZ rendering, registry,
 * and client-side classes (model loaders, texture loaders, renderer providers).
 *
 * Intercepts every ResourceLocation factory/constructor call made from these classes
 * and normalizes the raw string BEFORE it reaches the ResourceLocation constructor.
 *
 * This does NOT modify any Minecraft core class.
 */
@Pseudo
@Mixin(
        targets = {
                // Rendering / model / texture loaders
                "com.tacz.timelessandclassics.client.renderer.GunRenderer",
                "com.tacz.timelessandclassics.client.renderer.AttachmentRenderer",
                "com.tacz.timelessandclassics.client.model.GunModel",
                "com.tacz.timelessandclassics.client.model.AttachmentModel",
                "com.tacz.timelessandclassics.client.model.loader.GunModelLoader",
                "com.tacz.timelessandclassics.client.resource.ClientGunPackLoader",
                "com.tacz.timelessandclassics.client.resource.GunModelManager",
                "com.tacz.timelessandclassics.client.resource.GunTextureManager",
                "com.tacz.timelessandclassics.client.resource.ClientPackEventsHandler",
                // Registry / item classes
                "com.tacz.timelessandclassics.api.item.gun.AbstractGunItem",
                "com.tacz.timelessandclassics.api.item.attachment.AbstractAttachmentItem",
                "com.tacz.timelessandclassics.item.GunItem",
                "com.tacz.timelessandclassics.item.AttachmentItem",
                "com.tacz.timelessandclassics.item.AmmoItem",
                "com.tacz.timelessandclassics.crafting.ModifyGunRecipe",
                "com.tacz.timelessandclassics.crafting.AttachmentModifyRecipe",
                // Capability / NBT deserialization
                "com.tacz.timelessandclassics.capability.gun.GunData",
                "com.tacz.timelessandclassics.capability.gun.GunDataManager",
                "com.tacz.timelessandclassics.network.packet.UpdateGunIndexPacket",
                "com.tacz.timelessandclassics.network.packet.SyncGunPackPacket"
        },
        remap = false
)
public class TaCZRenderAndRegistrySanitizerMixin {

    // ── 1-arg: ResourceLocation.parse(String) ─────────────────────────────────
    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;parse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            require = 0
    )
    private String taczcompat$render$sanitizeParse(String id) {
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
    private String taczcompat$render$sanitizeTryParse(String id) {
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
    private String taczcompat$render$sanitizeConstructor1(String id) {
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
    private String taczcompat$render$sanitizeFromNsPath(String path) {
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
    private String taczcompat$render$sanitizeConstructor2(String path) {
        return ResourceLocationUtils.sanitizePath(path);
    }
}
