package com.taczcompat.taczruntimecompat.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public class BlockEntitySanitizerMixin {

    // Intercept Block Entity loading directly.
    // If the ID is "minecraft:dummy", we force it to skip completely by returning null (a safe fallback),
    // which prevents it from being registered or processed further by the game.
    @Inject(
        method = "loadStatic",
        at = @At("HEAD"),
        cancellable = true,
        require = 0,
        remap = false
    )
    private static void skipInvalidDummyBlockEntity(BlockPos pos, BlockState state, CompoundTag tag, HolderLookup.Provider registries, CallbackInfoReturnable<BlockEntity> cir) {
        if (tag != null) {
            String id = tag.getString("id");
            if ("minecraft:dummy".equals(id)) {
                System.out.println("[TaCZCompat] BlockEntity.loadStatic intercepted: Skipping invalid 'minecraft:dummy' block entity to prevent crashes.");
                cir.setReturnValue(null); // Safe fallback: return null, effectively skipping the block entity
            }
        }
    }
}
