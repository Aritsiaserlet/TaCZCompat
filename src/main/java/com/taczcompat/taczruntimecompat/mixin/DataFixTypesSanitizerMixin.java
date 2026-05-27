package com.taczcompat.taczruntimecompat.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(DataFixTypes.class)
public class DataFixTypesSanitizerMixin {

    // Intercept DataFixer updates right before they run to strip out invalid 'minecraft:dummy' IDs.
    // This prevents the game from crashing when loading chunks or player data containing these IDs.
    @Inject(
        method = "update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;",
        at = @At("HEAD"),
        require = 0,
        remap = false
    )
    private void sanitizeTagBeforeFixing(DataFixer fixer, CompoundTag tag, int version, int newVersion, CallbackInfoReturnable<CompoundTag> cir) {
        if (tag != null) {
            sanitizeDummyBlockEntities(tag);
        }
    }

    private void sanitizeDummyBlockEntities(Tag tag) {
        if (tag instanceof CompoundTag compound) {
            List<String> keysToRemove = new ArrayList<>();
            for (String key : compound.getAllKeys()) {
                Tag child = compound.get(key);
                if (child instanceof CompoundTag childComp) {
                    if ("minecraft:dummy".equals(childComp.getString("id"))) {
                        keysToRemove.add(key);
                        continue;
                    }
                }
                // Recursively check children
                sanitizeDummyBlockEntities(child);
            }
            for (String key : keysToRemove) {
                compound.remove(key);
                System.out.println("[TaCZCompat] Sanitized: removed invalid 'minecraft:dummy' from NBT CompoundTag to prevent DataFixer crash.");
            }
        } else if (tag instanceof ListTag list) {
            for (int i = list.size() - 1; i >= 0; i--) {
                Tag child = list.get(i);
                if (child instanceof CompoundTag childComp) {
                    if ("minecraft:dummy".equals(childComp.getString("id"))) {
                        list.remove(i);
                        System.out.println("[TaCZCompat] Sanitized: removed invalid 'minecraft:dummy' from NBT ListTag to prevent DataFixer crash.");
                        continue;
                    }
                }
                // Recursively check children
                sanitizeDummyBlockEntities(child);
            }
        }
    }
}
