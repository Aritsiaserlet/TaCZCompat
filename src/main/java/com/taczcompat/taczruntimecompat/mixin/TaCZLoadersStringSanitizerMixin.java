package com.taczcompat.taczruntimecompat.mixin;

import com.taczcompat.taczruntimecompat.util.ResourceLocationUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(
        targets = {
                "com.tacz.timelessandclassics.resource.GunPackLoader",
                "com.tacz.timelessandclassics.resource.AttachmentPackLoader",
                "com.tacz.timelessandclassics.resource.AmmoPackLoader"
        },
        remap = false
)
public class TaCZLoadersStringSanitizerMixin {

    // Target 1-arg ResourceLocation creation (tryParse, parse, <init>(String))
    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;parse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            require = 0
    )
    private String taczcompat$sanitizeParseArg(String id) {
        return ResourceLocationUtils.sanitizeId(id);
    }

    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            require = 0
    )
    private String taczcompat$sanitizeTryParseArg(String id) {
        return ResourceLocationUtils.sanitizeId(id);
    }

    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"
            ),
            require = 0
    )
    private String taczcompat$sanitizeConstructorArg(String id) {
        return ResourceLocationUtils.sanitizeId(id);
    }

    // Target 2-arg ResourceLocation creation (fromNamespaceAndPath, <init>(String, String))
    // We sanitize the path argument (index 1)
    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            ),
            index = 1,
            require = 0
    )
    private String taczcompat$sanitizeFromNamespaceAndPathArg(String path) {
        return ResourceLocationUtils.sanitizePath(path);
    }

    @ModifyArg(
            method = "/.*/",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;Ljava/lang/String;)V"
            ),
            index = 1,
            require = 0
    )
    private String taczcompat$sanitize2ArgConstructorPathArg(String path) {
        return ResourceLocationUtils.sanitizePath(path);
    }
}
