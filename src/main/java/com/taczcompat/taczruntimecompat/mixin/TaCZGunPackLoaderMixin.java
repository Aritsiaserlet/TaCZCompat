package com.taczcompat.taczruntimecompat.mixin;

import com.taczcompat.taczruntimecompat.util.ResourceLocationUtils;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Intercepts ResourceLocation creation during TaCZ's JSON loading phase.
 * Replaces direct constructor calls with our safe wrapper.
 */
@Pseudo
@Mixin(targets = "com.tacz.timelessandclassics.resource.GunPackLoader", remap = false)
public abstract class TaCZGunPackLoaderMixin {

    @Redirect(
            method = {
                "loadGunIndex",
                "loadGunDisplay",
                "loadModel",
                "loadTexture"
            },
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            require = 0
    )
    private ResourceLocation taczcompat$redirectResourceLocationCreation(String namespace, String path) {
        if ("tacz".equals(namespace)) {
            // Use our safe wrapper which sanitizes and safely creates the ResourceLocation
            return ResourceLocationUtils.safe(namespace, path);
        }

        // For non-tacz namespaces, attempt to create it safely using NeoForge 1.21.1 standards
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}



